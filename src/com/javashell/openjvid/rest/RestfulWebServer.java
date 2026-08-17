package com.javashell.openjvid.rest;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.javashell.flow.FlowController;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.MainFrame;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation;
import com.javashell.openjvid.ui.components.AddComponentDialog;
import com.javashell.openjvid.ui.components.input.InputComponent;
import com.javashell.video.digestors.MultiviewDigestor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Lightweight RESTful server used by OpenJVID to expose internal application
 * state and control endpoints over HTTP.
 *
 * <p>
 * This class creates an HttpServer bound to port 2424 and registers a set of
 * JSON-based endpoints used for querying status, controlling transport
 * (play/pause), listing nodes, inspecting node/link information, and creating
 * nodes programmatically.
 * </p>
 *
 * <p>
 * Important: endpoint handlers generally expect and return JSON payloads. The
 * server runs on the application's thread environment and uses
 * {@link SwingUtilities#invokeAndWait(Runnable)} when UI modifications are
 * required.
 * </p>
 */
public class RestfulWebServer {
	/** The embedded HttpServer instance that listens for HTTP requests. */
	private HttpServer server;

	/** Map of endpoint URI -> HttpHandler used to populate the HttpServer. */
	private HashMap<String, HttpHandler> endpoints;

	/** Reference to the flow pane containing nodes and linkages. */
	private final JNodeFlowPane flowPane;

	/**
	 * Action handler for main frame actions (unused directly by endpoints but kept
	 * for future commands).
	 */
	private final MainFrameActionHandler handler;

	/** Reference to the primary application MainFrame used for node access. */
	private final MainFrame mf;

	/*
	 *
	 * Need to define what options should be available and exposed to the Rest API,
	 * as well as determine encrypted negotiation
	 */
	/**
	 * Construct and start the REST server.
	 *
	 * @param flowPane reference to the JNodeFlowPane (used for node/link queries
	 *                 and node creation)
	 * @param mf       reference to the MainFrame (used to enumerate node
	 *                 components)
	 * @param handler  action handler for UI commands (reserved for future use)
	 */
	public RestfulWebServer(JNodeFlowPane flowPane, MainFrame mf, MainFrameActionHandler handler) {
		this.flowPane = flowPane;
		this.handler = handler;
		this.mf = mf;
		this.endpoints = new HashMap<String, HttpHandler>();
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize and start the HttpServer.
	 *
	 * Binds to port 2424, constructs the endpoint map, registers contexts and
	 * starts the server.
	 *
	 * @throws IOException when the server socket cannot be opened
	 */
	private void initialize() throws IOException {
		server = HttpServer.create(new InetSocketAddress(2424), 0);
		createEndpoints();
		populateEndpoints();
		server.start();
	}

	/**
	 * Register all entries from {@link #endpoints} with the embedded HttpServer.
	 * This method iterates the endpoint map and calls
	 * {@link HttpServer#createContext(String, HttpHandler)} for each entry.
	 */
	private void populateEndpoints() {
		System.out.println(endpoints.size() + " endpoints found.");

		var endpointURIs = endpoints.keySet();
		for (String uri : endpointURIs) {
			server.createContext(uri, endpoints.get(uri));
			System.out.println("Added endpoint " + uri);
		}
	}

	/**
	 * Create and populate the {@link #endpoints} map. The method registers the
	 * following JSON endpoints (summary):
	 *
	 * <ul>
	 * <li>/api/status (GET) - returns overall running status and frame timing</li>
	 * <li>/api/transport (GET/POST) - query or set play/pause state</li>
	 * <li>/api/nodes/active (GET) - list currently active node components</li>
	 * <li>/api/nodes/linkages (GET) - list outgoing linkages per node</li>
	 * <li>/api/nodes/listAvailable (GET) - list available node types that can be
	 * created</li>
	 * <li>/api/nodes/details (GET) - return parameter details for a node type
	 * (expects JSON request with Name)</li>
	 * <li>/api/nodes/create (POST) - create a node instance by invoking the
	 * registered factory callback</li>
	 * <li>/api/nodes/multiview (GET/POST) - inspect and control multiview layouts,
	 * including presets and custom viewport slot geometry</li>
	 * </ul>
	 *
	 * <p>
	 * Each handler accepts and returns JSON and uses HTTP status codes to indicate
	 * success (200), client errors (400) or server errors (500). UI modifications
	 * are dispatched on the Swing event thread when needed.
	 * </p>
	 */
	private void createEndpoints() {
		String statusEndpoint = "/api/status";
		endpoints.put(statusEndpoint, exchange -> {
			setHeaders(exchange);
			try {
				JSONObject statusObject = new JSONObject();
				statusObject.put("IsPlaying", FlowController.isFlowing());
				var jackManager = FlowController.getJackManager();
				statusObject.put("JackCapable", jackManager != null && jackManager.isJackCapable());
				statusObject.put("Framerate", FlowController.getAverageInternalFrameRate());
				statusObject.put("FrameDelta", FlowController.getAverageFrameDelta());

				var statusString = statusObject.toString();
				exchange.sendResponseHeaders(200, statusString.getBytes().length);
				OutputStream out = exchange.getResponseBody();
				out.write(statusString.getBytes());
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				exchange.sendResponseHeaders(500, -1);
			} finally {
				exchange.close();
			}
		});

		String configEndpoint = "/api/config";
		endpoints.put(configEndpoint, exchange -> {
			setHeaders(exchange);

			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();

			if ("GET".equals(method)) {
				String config = mf.dumpConfiguration();
				JSONObject configObject = new JSONObject();
				configObject.put("Config", config);

				var configObjectString = configObject.toString();
				exchange.sendResponseHeaders(200, configObjectString.getBytes().length);
				OutputStream out = exchange.getResponseBody();
				out.write(configObjectString.getBytes());
				out.flush();
				exchange.close();
			} else if ("POST".equals(method)) {
				InputStream in = exchange.getRequestBody();
				byte[] requestBytes = in.readAllBytes();
				String requestString = new String(requestBytes);
				JSONObject request = new JSONObject(requestString);
				if (request.has("Config")) {
					String config = request.getString("Config");
					try {
						// Load configuration on the Swing thread
						SwingUtilities.invokeAndWait(() -> {
							mf.loadConfiguration(config);
						});
						exchange.sendResponseHeaders(200, -1);
					} catch (Exception e) {
						e.printStackTrace();
						exchange.sendResponseHeaders(500, -1);
					}
				} else {
					exchange.sendResponseHeaders(400, -1);
				}
				exchange.close();
			} else {
				exchange.sendResponseHeaders(400, -1);
				exchange.close();
			}
		});

		String transportEndpoint = "/api/transport";
		endpoints.put(transportEndpoint, exchange -> {
			setHeaders(exchange);
			// Respect X-HTTP-Method-Override when present so clients can send bodies for
			// GET
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();

			if ("GET".equals(method)) {
				JSONObject transportObject = new JSONObject();
				transportObject.put("IsPlaying", FlowController.isFlowing());

				var transportString = transportObject.toString();
				exchange.sendResponseHeaders(200, transportString.getBytes().length);
				OutputStream out = exchange.getResponseBody();
				out.write(transportString.getBytes());
				out.flush();
				exchange.close();
				return;
			}
			if ("POST".equals(method)) {
				InputStream in = exchange.getRequestBody();
				byte[] requestBytes = in.readAllBytes();
				String requestString = new String(requestBytes);
				JSONObject request = new JSONObject(requestString);
				if (request.has("IsPlaying")) {
					var isPlaying = request.getBoolean("IsPlaying");
					if (isPlaying) {
						FlowController.resumeFlow();
					} else {
						FlowController.pauseFlow();
					}
					exchange.sendResponseHeaders(200, -1);
				} else {
					exchange.sendResponseHeaders(400, -1);
				}
				exchange.close();
				return;
			}
			exchange.sendResponseHeaders(400, -1);
			exchange.close();
		});

		String listActiveNodesEndpoint = "/api/nodes/active";
		endpoints.put(listActiveNodesEndpoint, exchange -> {
			setHeaders(exchange);
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();

			if ("GET".equals(method)) {
				var table = mf.getNodeComponents();
				JSONObject nodeTable = new JSONObject();
				var keySet = table.keySet();
				for (var id : keySet) {
					JSONObject idTable = new JSONObject();
					var node = table.get(id);
					var nodeName = node.getNodeName();
					var nodeType = node.getNodeType();
					Object contents = null;
					try {
						contents = node.getNode().retrieveNodeContents();
					} catch (Exception e) {
						// defensive: if retrieval fails, leave contents null
						contents = null;
					}
					String nodeContentClass = "";
					if (contents != null) {
						try {
							nodeContentClass = contents.getClass().getSimpleName();
						} catch (Exception e) {
							// fallback to toString if something unusual happens
							nodeContentClass = contents.getClass().getName();
						}
					}

					idTable.put("name", nodeName);
					idTable.put("type", nodeType.name());
					idTable.put("contentClass", nodeContentClass);
					idTable.put("FlowNodeUUID", node.getNode().getUUID().toString());

					nodeTable.put(node.getUUID().toString(), idTable);
				}

				String nodeTableString = nodeTable.toString();

				exchange.sendResponseHeaders(200, nodeTableString.getBytes().length);
				OutputStream out = exchange.getResponseBody();
				out.write(nodeTableString.getBytes());
				out.flush();

				exchange.close();
			} else {
				exchange.sendResponseHeaders(400, -1);
				exchange.close();
			}
		});

		String listLinkagesEndpoint = "/api/nodes/linkages";
		endpoints.put(listLinkagesEndpoint, exchange -> {
			setHeaders(exchange);
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();
			if ("GET".equals(method)) {
				var linkages = flowPane.getLinkages();
				var keySet = linkages.keySet();

				JSONObject linkagesTable = new JSONObject();

				for (var key : keySet) {
					JSONArray linkageArray = new JSONArray();
					var linkage = linkages.get(key);
					for (var link : linkage) {
						linkageArray.put(link.getNode().getUUID().toString());
					}

					linkagesTable.put(key.getUUID().toString(), linkageArray);
				}

				String responseString = linkagesTable.toString();
				exchange.sendResponseHeaders(200, responseString.getBytes().length);
				OutputStream out = exchange.getResponseBody();
				out.write(responseString.getBytes());
				out.flush();
				exchange.close();
			} else if ("POST".equals(method)) {
				// Expect JSON: { "Origin": "uuid-string", "Target": "uuid-string" }
				try {
					InputStream in = exchange.getRequestBody();
					byte[] requestBytes = in.readAllBytes();
					String requestString = new String(requestBytes);
					JSONObject request = new JSONObject(requestString);

					if (!request.has("Origin") || !request.has("Target")) {
						exchange.sendResponseHeaders(400, -1);
						exchange.close();
						return;
					}

					UUID originUUID = UUID.fromString(request.getString("Origin"));
					UUID targetUUID = UUID.fromString(request.getString("Target"));

					var originComp = mf.getNodeComponents().get(originUUID);
					var targetComp = mf.getNodeComponents().get(targetUUID);

					if (originComp == null || targetComp == null) {
						exchange.sendResponseHeaders(404, -1);
						exchange.close();
						return;
					}

					final boolean[] created = new boolean[1];
					SwingUtilities.invokeAndWait(() -> {
						try {
							flowPane.createLinkage(originComp, targetComp);
							created[0] = true;
						} catch (IncorrectLinkageException e) {
							created[0] = false;
						}
					});

					if (created[0]) {
						exchange.sendResponseHeaders(200, -1);
					} else {
						// Bad linkage (type mismatch etc.)
						exchange.sendResponseHeaders(400, -1);
					}
					exchange.close();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						exchange.sendResponseHeaders(500, -1);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					exchange.close();
				}
			} else if ("DELETE".equals(method)) {
				// Expect JSON: { "Origin": "uuid-string", "Target": "uuid-string" }
				try {
					InputStream in = exchange.getRequestBody();
					byte[] requestBytes = in.readAllBytes();
					String requestString = new String(requestBytes);
					JSONObject request = new JSONObject(requestString);

					if (!request.has("Origin") || !request.has("Target")) {
						exchange.sendResponseHeaders(400, -1);
						exchange.close();
						return;
					}

					UUID originUUID = UUID.fromString(request.getString("Origin"));
					UUID targetUUID = UUID.fromString(request.getString("Target"));

					var originComp = mf.getNodeComponents().get(originUUID);
					var targetComp = mf.getNodeComponents().get(targetUUID);

					if (originComp == null || targetComp == null) {
						exchange.sendResponseHeaders(404, -1);
						exchange.close();
						return;
					}

					final Object[] linkageToRemove = new Object[1];
					var linkSet = flowPane.getLinkages().get(originComp);
					if (linkSet == null) {
						exchange.sendResponseHeaders(404, -1);
						exchange.close();
						return;
					}

					for (var link : linkSet) {
						if (link.getNode() == targetComp) {
							linkageToRemove[0] = link;
							break;
						}
					}

					if (linkageToRemove[0] == null) {
						exchange.sendResponseHeaders(404, -1);
						exchange.close();
						return;
					}

					boolean[] removed = new boolean[1];
					SwingUtilities.invokeAndWait(() -> {
						try {
							flowPane.removeLinkage((JNodeFlowPane.Linkage) linkageToRemove[0]);
							removed[0] = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					});

					if (removed[0]) {
						exchange.sendResponseHeaders(200, -1);
					} else {
						exchange.sendResponseHeaders(500, -1);
					}
					exchange.close();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						exchange.sendResponseHeaders(500, -1);
					} catch (IOException ioe) {
						e.printStackTrace();
					}
					exchange.close();
				}
			} else {
				exchange.sendResponseHeaders(400, -1);
				exchange.close();
			}
		});

		String nodeListEndpoint = "/api/nodes/listAvailable";
		endpoints.put(nodeListEndpoint, exchange -> {
			setHeaders(exchange);
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();

			if ("GET".equals(method)) {
				var nodeTypes = AddComponentDialog.componentTypes;
				JSONObject transportObject = new JSONObject();
				transportObject.put("Count", nodeTypes.length);
				transportObject.put("AvailableNodes", new JSONArray(nodeTypes));

				var transportString = transportObject.toString();
				exchange.sendResponseHeaders(200, transportString.getBytes().length);
				OutputStream out = exchange.getResponseBody();
				out.write(transportString.getBytes());
				out.flush();
				exchange.close();
				return;
			}
			exchange.sendResponseHeaders(400, -1);
			exchange.close();
		});

		String nodeDetailsEndpoint = "/api/nodes/details";
		endpoints.put(nodeDetailsEndpoint, exchange -> {
			setHeaders(exchange);
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();

			if ("GET".equals(method)) {
				var in = exchange.getRequestBody();
				byte[] requestBytes = in.readAllBytes();
				String requestString = new String(requestBytes);
				try {

					JSONObject request = new JSONObject(requestString);

					JSONObject responseObject = new JSONObject();
					JSONArray parameters = new JSONArray();

					String nodeName = (String) request.get("Name");
					var callbackMethod = AddComponentDialog.callBackMethods.get(nodeName);
					var pList = callbackMethod.getParameters();
					var pTypeList = callbackMethod.getParameterTypes();

					int index = 0;
					for (var param : pTypeList) {
						if (param.isAssignableFrom(JNodeFlowPane.class))
							continue;
						var p = pList[index];
						ParameterLabelAnnotation.Label labelAnnotation = p
								.getAnnotation(ParameterLabelAnnotation.Label.class);
						String label = p.getName();
						if (labelAnnotation != null) {
							label = labelAnnotation.label();
						}
						responseObject.put(label, param.getName());
						JSONObject parameterObject = new JSONObject();
						parameterObject.put("Label", label);
						parameterObject.put("Type", param.getName());
						parameterObject.put("Index", index);
						parameters.put(parameterObject);
						index++;
					}

					responseObject.put("Count", index);
					responseObject.put("Parameters", parameters);

					var responseString = responseObject.toString();
					exchange.sendResponseHeaders(200, responseString.getBytes().length);
					OutputStream out = exchange.getResponseBody();
					out.write(responseString.getBytes());
					out.flush();
					exchange.close();
				} catch (Exception e) {
					exchange.sendResponseHeaders(500, -1);
					exchange.close();
					return;
				}
				return;
			}
			exchange.sendResponseHeaders(400, -1);
			exchange.close();
		});

		String createNodeEndpoint = "/api/nodes/create";
		/*
		 * { "NodeType": "Test Input", "ParameterList": { "Pattern": [ "SMPTE_COLORBARS"
		 * ], "Resolution": [1920, 1080], "Name": ["Test Input"] } }
		 */
		endpoints.put(createNodeEndpoint, exchange -> {
			setHeaders(exchange);
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();
			if ("POST".equals(method)) {
				try {
					var in = exchange.getRequestBody();
					byte[] requestBytes = in.readAllBytes();
					String requestString = new String(requestBytes);

					JSONObject requestObject = new JSONObject(requestString);
					String nodeType = requestObject.getString("NodeType").toString();

					// List of parameters, key is param label, value is param value
					JSONObject parameterList = requestObject.getJSONObject("ParameterList");

					Method callback = AddComponentDialog.callBackMethods.get(nodeType);
					if (callback == null) {
						exchange.sendResponseHeaders(400, -1);
						exchange.close();
						return;
					}

					var pList = callback.getParameters();
					var pTypeList = callback.getParameterTypes();
					var objList = new Object[pList.length];

					int index = 0;
					for (var param : pTypeList) {
						if (param.isAssignableFrom(JNodeFlowPane.class)) {
							objList[index] = flowPane;
							index++;
							continue;
						}

						var p = pList[index];
						ParameterLabelAnnotation.Label labelAnnotation = p
								.getAnnotation(ParameterLabelAnnotation.Label.class);
						String label = p.getName();
						if (labelAnnotation != null) {
							label = labelAnnotation.label();
						}

						if (!parameterList.has(label)) {
							Object defaultValue = defaultValueForType(param);
							if (defaultValue == null) {
								exchange.sendResponseHeaders(400, -1);
								exchange.close();
								return;
							}
							objList[index] = defaultValue;
							index++;
							continue;
						}

						var paramValue = (JSONArray) parameterList.get(label);
						var paramValueArray = new Object[paramValue.length()];
						for (int i = 0; i < paramValue.length(); i++)
							paramValueArray[i] = paramValue.get(i);

						try {
							var inputComponent = (InputComponent<?>) AddComponentDialog.inputComponents.get(param)
									.getConstructors()[0].newInstance();
							var realizedParam = inputComponent.attemptCreation(paramValueArray);
							objList[index] = realizedParam;
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | SecurityException e) {
							exchange.sendResponseHeaders(500, -1);
							e.printStackTrace();
							exchange.close();
							return;
						}
						index++;
					}

					final boolean[] created = new boolean[1];
					SwingUtilities.invokeAndWait(() -> {
						try {
							Component vidComp;
							vidComp = (Component) callback.invoke(null, objList);
							flowPane.add(vidComp);
							created[0] = true;
						} catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
							created[0] = false;
						}
					});

					if (created[0]) {
						exchange.sendResponseHeaders(200, -1);
					} else {
						exchange.sendResponseHeaders(500, -1);
					}
					exchange.close();

				} catch (Exception e) {
					e.printStackTrace();
					exchange.sendResponseHeaders(500, -1);
					exchange.close();
				}
				return;
			}
			exchange.sendResponseHeaders(400, -1);
			exchange.close();
		});

		String multiviewEndpoint = "/api/nodes/multiview";
		endpoints.put(multiviewEndpoint, exchange -> {
			setHeaders(exchange);
			String method = exchange.getRequestHeaders().getFirst("X-HTTP-Method-Override");
			if (method == null)
				method = exchange.getRequestMethod();

			if ("GET".equals(method)) {
				try {
					JSONObject response = new JSONObject();
					JSONObject nodes = new JSONObject();
					for (var entry : mf.getNodeComponents().entrySet()) {
						UUID nodeId = entry.getKey();
						var nodeComp = entry.getValue();
						Object contents;
						try {
							contents = nodeComp.getNode().retrieveNodeContents();
						} catch (Exception e) {
							continue;
						}
						if (contents instanceof MultiviewDigestor) {
							nodes.put(nodeId.toString(), toMultiviewState(nodeId, nodeComp.getNodeName(),
									(MultiviewDigestor) contents));
						}
					}
					response.put("Count", nodes.length());
					response.put("Nodes", nodes);

					String payload = response.toString();
					exchange.sendResponseHeaders(200, payload.getBytes().length);
					OutputStream out = exchange.getResponseBody();
					out.write(payload.getBytes());
					out.flush();
					exchange.close();
				} catch (Exception e) {
					e.printStackTrace();
					exchange.sendResponseHeaders(500, -1);
					exchange.close();
				}
				return;
			}

			if ("POST".equals(method)) {
				try {
					InputStream in = exchange.getRequestBody();
					byte[] requestBytes = in.readAllBytes();
					String requestString = new String(requestBytes);
					JSONObject request = new JSONObject(requestString);

					if (!request.has("Node")) {
						exchange.sendResponseHeaders(400, -1);
						exchange.close();
						return;
					}

					UUID nodeId = UUID.fromString(request.getString("Node"));
					var nodeComp = mf.getNodeComponents().get(nodeId);
					if (nodeComp == null) {
						exchange.sendResponseHeaders(404, -1);
						exchange.close();
						return;
					}

					final MultiviewDigestor[] holder = new MultiviewDigestor[1];
					final boolean[] applied = new boolean[] { true };

					SwingUtilities.invokeAndWait(() -> {
						Object contents = nodeComp.getNode().retrieveNodeContents();
						if (!(contents instanceof MultiviewDigestor)) {
							holder[0] = null;
							applied[0] = false;
							return;
						}
						MultiviewDigestor mvd = (MultiviewDigestor) contents;
						holder[0] = mvd;

						if (request.has("Layout")) {
							String layout = request.getString("Layout");
							if (!mvd.setLayoutMode(layout)) {
								applied[0] = false;
								return;
							}
						}

						if (request.has("Rows") && request.has("Columns")) {
							int rows = request.getInt("Rows");
							int cols = request.getInt("Columns");
							mvd.setGridLayout(rows, cols);
						}

						if (request.has("Slots")) {
							JSONArray slots = request.getJSONArray("Slots");
							for (int i = 0; i < slots.length(); i++) {
								JSONObject slot = slots.getJSONObject(i);
								int slotIndex = slot.getInt("Index");
								boolean ok = mvd.setViewportBounds(slotIndex, slot.getInt("X"), slot.getInt("Y"),
										slot.getInt("Width"), slot.getInt("Height"));
								if (!ok) {
									applied[0] = false;
									return;
								}
								if (slot.has("Z")) {
									boolean zOk = mvd.setViewportZOrder(slotIndex, slot.getInt("Z"));
									if (!zOk) {
										applied[0] = false;
										return;
									}
								}
							}
						}

						if (request.has("SourceOrder")) {
							JSONArray sourceOrder = request.getJSONArray("SourceOrder");
							ArrayList<UUID> ordered = new ArrayList<UUID>();
							HashMap<UUID, UUID> componentToFlow = getComponentToFlowNodeMap();
							for (int i = 0; i < sourceOrder.length(); i++) {
								if (sourceOrder.isNull(i)) {
									ordered.add(null);
								} else {
									UUID requested = UUID.fromString(sourceOrder.getString(i));
									UUID resolved = componentToFlow.getOrDefault(requested, requested);
									ordered.add(resolved);
								}
							}
							if (!mvd.setSourceOrder(ordered)) {
								applied[0] = false;
								return;
							}
						}

						if (request.has("Actions")) {
							JSONArray actions = request.getJSONArray("Actions");
							for (int i = 0; i < actions.length(); i++) {
								JSONObject action = actions.getJSONObject(i);
								String type = action.getString("Type");
								int slotIndex = action.getInt("Index");
								boolean ok = false;
								switch (type) {
								case "BringToFront":
									ok = mvd.bringViewportToFront(slotIndex);
									break;
								case "SendToBack":
									ok = mvd.sendViewportToBack(slotIndex);
									break;
								case "Raise":
									ok = mvd.raiseViewport(slotIndex);
									break;
								case "Lower":
									ok = mvd.lowerViewport(slotIndex);
									break;
								default:
									ok = false;
									break;
								}
								if (!ok) {
									applied[0] = false;
									return;
								}
							}
						}
					});

					if (!applied[0] || holder[0] == null) {
						exchange.sendResponseHeaders(400, -1);
						exchange.close();
						return;
					}

					JSONObject response = toMultiviewState(nodeId, nodeComp.getNodeName(), holder[0]);
					String payload = response.toString();
					exchange.sendResponseHeaders(200, payload.getBytes().length);
					OutputStream out = exchange.getResponseBody();
					out.write(payload.getBytes());
					out.flush();
					exchange.close();
				} catch (Exception e) {
					e.printStackTrace();
					exchange.sendResponseHeaders(500, -1);
					exchange.close();
				}
				return;
			}

			exchange.sendResponseHeaders(400, -1);
			exchange.close();
		});

		// Root path serves the flow studio application.
		endpoints.put("/", new HTTPServlet("flow_studio.html", "OpenJVID Flow Studio"));

		// Keep the existing console reachable from a hidden path.
		endpoints.put("/rest_console", new HTTPServlet("rest_console.html", "OpenJVID REST Console"));

	}

	/**
	 * Apply common response headers used by all JSON endpoints. Currently sets
	 * Content-Type to application/json and Connection to Keep-Alive.
	 *
	 * @param exchange the HttpExchange being prepared
	 */
	private void setHeaders(HttpExchange exchange) {
		// Log each incoming request for debugging the REST console interactions
		try {
			System.out.println("REST request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
		} catch (Exception e) {
			// ignore logging failures
		}
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.getResponseHeaders().add("Connection", "Keep-Alive");
	}

	private JSONObject toMultiviewState(UUID nodeId, String nodeName, MultiviewDigestor mvd) {
		JSONObject state = new JSONObject();
		state.put("Node", nodeId.toString());
		state.put("Name", nodeName);
		HashMap<String, Object> props = mvd.getPropertyTable();
		state.put("Layout", props.get("Layout"));
		state.put("Viewports", props.get("Viewports"));
		state.put("Rows", props.get("Rows"));
		state.put("Columns", props.get("Columns"));
		state.put("ResolutionWidth", mvd.getResolution().width);
		state.put("ResolutionHeight", mvd.getResolution().height);

		JSONArray slots = new JSONArray();
		List<Map<String, Integer>> layout = mvd.getViewportLayoutSnapshot();
		for (Map<String, Integer> slot : layout) {
			slots.put(new JSONObject(slot));
		}
		state.put("Slots", slots);
		List<String> sourceOrderFlow = mvd.getSourceOrderSnapshot();
		state.put("SourceOrder", new JSONArray(sourceOrderFlow));
		JSONArray sourceOrderComponents = new JSONArray();
		HashMap<UUID, UUID> flowToComponent = getFlowNodeToComponentMap();
		for (String flowId : sourceOrderFlow) {
			if (flowId == null) {
				sourceOrderComponents.put(JSONObject.NULL);
				continue;
			}
			UUID componentId = flowToComponent.get(UUID.fromString(flowId));
			if (componentId == null) {
				sourceOrderComponents.put(JSONObject.NULL);
			} else {
				sourceOrderComponents.put(componentId.toString());
			}
		}
		state.put("SourceOrderComponents", sourceOrderComponents);
		return state;
	}

	private HashMap<UUID, UUID> getComponentToFlowNodeMap() {
		HashMap<UUID, UUID> out = new HashMap<UUID, UUID>();
		for (var entry : mf.getNodeComponents().entrySet()) {
			out.put(entry.getKey(), entry.getValue().getNode().getUUID());
		}
		return out;
	}

	private Object defaultValueForType(Class<?> type) {
		if (type == String.class) {
			return "";
		}
		if (type == int.class) {
			return 0;
		}
		if (type == float.class) {
			return 0f;
		}
		return null;
	}

	private HashMap<UUID, UUID> getFlowNodeToComponentMap() {
		HashMap<UUID, UUID> out = new HashMap<UUID, UUID>();
		for (var entry : mf.getNodeComponents().entrySet()) {
			out.put(entry.getValue().getNode().getUUID(), entry.getKey());
		}
		return out;
	}

}
