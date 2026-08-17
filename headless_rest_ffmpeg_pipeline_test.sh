#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

if [[ ! -e /dev/video0 ]]; then
  echo "ERROR: /dev/video0 was not found"
  exit 1
fi

CODEC="${1:-mjpeg}"
FRAME_RATE="${2:-30}"

RUNTIME_LOG="/tmp/openjvid_headless_runtime.log"
rm -f "$RUNTIME_LOG"

mvn -q -DskipTests exec:java \
  -Dexec.mainClass=com.javashell.openjvid.MainUITest \
  -Dexec.args='-headless' \
  >"$RUNTIME_LOG" 2>&1 &
RUNTIME_PID=$!

cleanup() {
  if kill -0 "$RUNTIME_PID" >/dev/null 2>&1; then
    kill "$RUNTIME_PID" >/dev/null 2>&1 || true
    wait "$RUNTIME_PID" >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

wait_for_rest() {
  for _ in $(seq 1 120); do
    if curl -sS -o /dev/null -w '%{http_code}' http://127.0.0.1:2424/api/nodes/listAvailable | grep -q '^200$'; then
      return 0
    fi
    sleep 0.5
  done
  return 1
}

if ! wait_for_rest; then
  echo "ERROR: OpenJVID REST endpoint did not come online"
  echo "--- runtime log tail ---"
  tail -n 120 "$RUNTIME_LOG" || true
  exit 1
fi

create_node() {
  local payload="$1"
  curl -sS -X POST \
    -H 'Content-Type: application/json' \
    -d "$payload" \
    -o /dev/null \
    -w '%{http_code}' \
    http://127.0.0.1:2424/api/nodes/create
}

ingest_payload=$(cat <<JSON
{"NodeType":"FFmpeg Ingest (Video Device)","ParameterList":{"Resolution":[1920,1080],"Device Path":["/dev/video0"],"Codec":["${CODEC}"],"Framerate":["${FRAME_RATE}"]}}
JSON
)

scaler_payload='{"NodeType":"Scaling Digest","ParameterList":{"Input Resolution":[1920,1080],"Output Resolution":[720,480],"Name":["REST Scale 720x480"]}}'
preview_payload='{"NodeType":"Preview Frame","ParameterList":{"Resolution":[720,480],"Name":["REST Preview 720x480"]}}'

ingest_code="$(create_node "$ingest_payload")"
scaler_code="$(create_node "$scaler_payload")"
preview_code="$(create_node "$preview_payload")"

echo "create_ingest_status=$ingest_code"
echo "create_scaler_status=$scaler_code"
echo "create_preview_status=$preview_code"

if [[ "$ingest_code" != "200" || "$scaler_code" != "200" || "$preview_code" != "200" ]]; then
  echo "ERROR: One or more create calls failed"
  echo "--- runtime log tail ---"
  tail -n 150 "$RUNTIME_LOG" || true
  exit 1
fi

ACTIVE_JSON="$(curl -sS http://127.0.0.1:2424/api/nodes/active)"
export ACTIVE_JSON

readarray -t IDS < <(/usr/bin/python - <<'PY'
import json
import os

active = json.loads(os.environ["ACTIVE_JSON"])
ingest_id = None
scaler_id = None
preview_id = None

for node_id, meta in active.items():
    if not isinstance(meta, dict):
        continue
    name = meta.get("name", "")
    klass = meta.get("contentClass", "")
    if klass == "FFMPEGIngestor":
        ingest_id = node_id
    if name == "REST Scale 720x480":
        scaler_id = node_id
    if name == "REST Preview 720x480":
        preview_id = node_id

print(ingest_id or "")
print(scaler_id or "")
print(preview_id or "")
PY
)

INGEST_ID="${IDS[0]:-}"
SCALER_ID="${IDS[1]:-}"
PREVIEW_ID="${IDS[2]:-}"

echo "ingest_id=$INGEST_ID"
echo "scaler_id=$SCALER_ID"
echo "preview_id=$PREVIEW_ID"

if [[ -z "$INGEST_ID" || -z "$SCALER_ID" || -z "$PREVIEW_ID" ]]; then
  echo "ERROR: Failed to resolve node IDs from /api/nodes/active"
  echo "active_json=$ACTIVE_JSON"
  echo "--- runtime log tail ---"
  tail -n 150 "$RUNTIME_LOG" || true
  exit 1
fi

link_chain() {
  local origin="$1"
  local target="$2"
  curl -sS -X POST \
    -H 'Content-Type: application/json' \
    -d "{\"Origin\":\"${origin}\",\"Target\":\"${target}\"}" \
    -o /dev/null \
    -w '%{http_code}' \
    http://127.0.0.1:2424/api/nodes/linkages
}

link_1_code="$(link_chain "$INGEST_ID" "$SCALER_ID")"
link_2_code="$(link_chain "$SCALER_ID" "$PREVIEW_ID")"

echo "link_ingest_to_scaler_status=$link_1_code"
echo "link_scaler_to_preview_status=$link_2_code"

if [[ "$link_1_code" != "200" || "$link_2_code" != "200" ]]; then
  echo "ERROR: One or more linkage calls failed"
  echo "--- runtime log tail ---"
  tail -n 150 "$RUNTIME_LOG" || true
  exit 1
fi

LINKAGES_JSON="$(curl -sS http://127.0.0.1:2424/api/nodes/linkages)"
export LINKAGES_JSON
export INGEST_ID SCALER_ID PREVIEW_ID

/usr/bin/python - <<'PY'
import json
import os
import sys

links = json.loads(os.environ["LINKAGES_JSON"])
ingest = os.environ["INGEST_ID"]
scaler = os.environ["SCALER_ID"]
preview = os.environ["PREVIEW_ID"]

ok = True
if scaler not in links.get(ingest, []):
    ok = False
if preview not in links.get(scaler, []):
    ok = False

print("linkages_json=" + json.dumps(links, separators=(",", ":")))
if not ok:
    print("ERROR: Linkage graph does not include expected chain", file=sys.stderr)
    sys.exit(1)
PY

echo "PASS: Headless non-emulated REST pipeline created and linked"
