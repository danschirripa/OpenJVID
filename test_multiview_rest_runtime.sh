#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

RUNTIME_LOG="/tmp/openjvid_multiview_rest_runtime.log"
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
    code="$(curl -sS -o /dev/null -w '%{http_code}' http://127.0.0.1:2424/api/nodes/listAvailable || true)"
    if [[ "$code" == "200" ]]; then
      return 0
    fi
    sleep 0.5
  done
  return 1
}

if ! wait_for_rest; then
  echo "ERROR: OpenJVID REST did not come online"
  tail -n 120 "$RUNTIME_LOG" || true
  exit 1
fi

/usr/bin/python - <<'PY'
import json
import time
from urllib import request, error

BASE = "http://127.0.0.1:2424"


def req(path, method="GET", payload=None):
    data = json.dumps(payload).encode("utf-8") if payload is not None else None
    r = request.Request(BASE + path, data=data, headers={"Content-Type": "application/json"}, method=method)
    try:
        with request.urlopen(r, timeout=10) as resp:
            body = resp.read().decode("utf-8")
            return resp.status, body
    except error.HTTPError as e:
        return e.code, e.read().decode("utf-8", errors="replace")


def must_status(name, got, expected=200):
    if got != expected:
        raise SystemExit(f"{name} expected HTTP {expected}, got {got}")


def create(node_type, param_list):
    status, body = req("/api/nodes/create", "POST", {"NodeType": node_type, "ParameterList": param_list})
    must_status(f"create {node_type}", status)
    return body


def get_active():
    status, body = req("/api/nodes/active")
    must_status("get active", status)
    return json.loads(body)


def node_id_by_name(active, name):
    for node_id, meta in active.items():
        if isinstance(meta, dict) and meta.get("name") == name:
            return node_id
    return None


def link(origin, target):
    status, _ = req("/api/nodes/linkages", "POST", {"Origin": origin, "Target": target})
    must_status(f"link {origin} -> {target}", status)


def post_multiview(step, payload):
    status, body = req("/api/nodes/multiview", "POST", payload)
    if status != 200:
        raise SystemExit(f"multiview update failed at {step}: HTTP {status} body={body}")
    return json.loads(body)


def get_multiview_map():
    status, body = req("/api/nodes/multiview")
    must_status("multiview get", status)
    obj = json.loads(body)
    return obj.get("Nodes", {})


# Create two test-pattern sources + multiview + preview sink.
create("Test Input", {
    "Resolution": [1280, 720],
    "Pattern": ["SMPTE_COLORBARS"],
    "Name": ["MV Source A"],
})
create("Test Input", {
    "Resolution": [1280, 720],
    "Pattern": ["LINEAR_ACCURACY"],
    "Name": ["MV Source B"],
})
create("Multiview Digest", {
    "Resolution": [1280, 720],
    "Viewports": ["2"],
    "Rows": ["1"],
    "Columns": ["2"],
})
create("Preview Frame", {
    "Resolution": [1280, 720],
    "Name": ["MV Preview"],
})

active = get_active()
source_a = node_id_by_name(active, "MV Source A")
source_b = node_id_by_name(active, "MV Source B")
preview = node_id_by_name(active, "MV Preview")
multiview = None
for node_id, meta in active.items():
    if isinstance(meta, dict) and meta.get("contentClass") == "MultiviewDigestor":
        multiview = node_id
        break

if not all([source_a, source_b, preview, multiview]):
    raise SystemExit(f"failed to resolve node IDs: source_a={source_a} source_b={source_b} multiview={multiview} preview={preview}")

# Link sourceA/sourceB -> multiview -> preview.
link(source_a, multiview)
link(source_b, multiview)
link(multiview, preview)

# Ensure transport running while we mutate layout.
must_status("transport play", req("/api/transport", "POST", {"IsPlaying": True})[0])

# 1) GRID layout 1x2
state = post_multiview("grid", {
    "Node": multiview,
    "Layout": "GRID",
    "Rows": 1,
    "Columns": 2,
})
if state.get("Layout") != "GRID":
    raise SystemExit(f"expected GRID layout, got {state.get('Layout')}")

# 2) PIP_RIGHT preset while flowing
time.sleep(0.75)
state = post_multiview("pip_right", {
    "Node": multiview,
    "Layout": "PIP_RIGHT",
})
if state.get("Layout") != "PIP_RIGHT":
    raise SystemExit(f"expected PIP_RIGHT layout, got {state.get('Layout')}")

# 3) Custom slot geometry move/resize while flowing
time.sleep(0.75)
state = post_multiview("custom_slot_initial", {
    "Node": multiview,
    "Slots": [
        {"Index": 0, "X": 0, "Y": 0, "Width": 960, "Height": 720, "Z": 0},
        {"Index": 1, "X": 820, "Y": 60, "Width": 360, "Height": 240, "Z": 10},
    ],
})
if state.get("Layout") != "CUSTOM":
    raise SystemExit(f"expected CUSTOM layout after slot update, got {state.get('Layout')}")

# 4) Move PiP again + swap source order during flow transport
time.sleep(0.75)
state = post_multiview("custom_slot_move", {
    "Node": multiview,
    "Slots": [
        {"Index": 1, "X": 760, "Y": 430, "Width": 420, "Height": 280, "Z": 20},
    ],
})

z_by_index = {slot.get("Index"): slot.get("Z") for slot in state.get("Slots", []) if isinstance(slot, dict)}
if z_by_index.get(1) != 20:
    raise SystemExit(f"expected slot 1 z-order 20, got {z_by_index.get(1)}")

# 5) Exercise z-order action helpers.
state = post_multiview("action_send_to_back", {
    "Node": multiview,
    "Actions": [
        {"Type": "SendToBack", "Index": 1},
    ],
})
z_by_index = {slot.get("Index"): slot.get("Z") for slot in state.get("Slots", []) if isinstance(slot, dict)}
if z_by_index.get(1, 0) >= z_by_index.get(0, 0):
    raise SystemExit(f"expected slot 1 to be behind slot 0 after SendToBack, got {z_by_index}")

state = post_multiview("action_bring_to_front", {
    "Node": multiview,
    "Actions": [
        {"Type": "BringToFront", "Index": 1},
    ],
})
z_by_index = {slot.get("Index"): slot.get("Z") for slot in state.get("Slots", []) if isinstance(slot, dict)}
if z_by_index.get(1, 0) <= z_by_index.get(0, 0):
    raise SystemExit(f"expected slot 1 to be in front of slot 0 after BringToFront, got {z_by_index}")

swap_status, swap_body = req("/api/nodes/multiview", "POST", {
    "Node": multiview,
    "SourceOrder": [source_b, source_a],
})
must_status("multiview source swap", swap_status)
state = json.loads(swap_body)
source_order_components = state.get("SourceOrderComponents", [])
if len(source_order_components) < 2 or source_order_components[0] != source_b or source_order_components[1] != source_a:
    raise SystemExit(f"unexpected SourceOrderComponents after swap: {source_order_components}")

source_order = state.get("SourceOrder", [])

# Echo final state so the shell script output is useful.
print("multiview_node", multiview)
print("source_a", source_a)
print("source_b", source_b)
print("preview", preview)
print("final_layout", state.get("Layout"))
print("final_source_order", json.dumps(source_order))
print("final_source_order_components", json.dumps(source_order_components))
print("final_slots", json.dumps(state.get("Slots", [])))
print("source_swap_status", swap_status)
print("PASS: multiview layout and PiP movement tested during active transport")
PY
