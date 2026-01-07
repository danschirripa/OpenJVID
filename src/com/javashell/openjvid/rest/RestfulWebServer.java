package com.javashell.openjvid.rest;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.javashell.flow.FlowController;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.MainFrame;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation;
import com.javashell.openjvid.ui.components.AddComponentDialog;
import com.javashell.openjvid.ui.components.input.InputComponent;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RestfulWebServer {
	private HttpServer server;
	private HashMap<String, HttpHandler> endpoints;
	private final JNodeFlowPane flowPane;
	private final MainFrameActionHandler handler;
	private final MainFrame mf;

	/*
	 * 
	 * Need to define what options should be available and exposed to the Rest API,
	 * as well as determine encrypted negotiation
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

	private void initialize() throws IOException {
		server = HttpServer.create(new InetSocketAddress(2424), 0);
		createEndpoints();
		populateEndpoints();
		server.start();
	}

	private void populateEndpoints() {
		System.out.println(endpoints.size() + " endpoints found.");

		var endpointURIs = endpoints.keySet();
		for (String uri : endpointURIs) {
			server.createContext(uri, endpoints.get(uri));
			System.out.println("Added endpoint " + uri);
		}
	}

	private void createEndpoints() {
		String statusEndpoint = "/api/status";
		endpoints.put(statusEndpoint, exchange -> {
			setHeaders(exchange);

			JSONObject statusObject = new JSONObject();
			statusObject.put("IsPlaying", FlowController.isFlowing());
			statusObject.put("JackCapable", FlowController.jackManager.isJackCapable());
			statusObject.put("Framerate", FlowController.getAverageInternalFrameRate());
			statusObject.put("FrameDelta", FlowController.getAverageFrameDelta());

			var statusString = statusObject.toString();
			exchange.sendResponseHeaders(200, statusString.getBytes().length);
			OutputStream out = exchange.getResponseBody();
			out.write(statusString.getBytes());
			out.flush();
			exchange.close();
		});

		String transportEndpoint = "/api/transport";
		endpoints.put(transportEndpoint, exchange -> {
			setHeaders(exchange);

			if ("GET".equals(exchange.getRequestMethod())) {
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
			if ("POST".equals(exchange.getRequestMethod())) {
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

			if ("GET".equals(exchange.getRequestMethod())) {
				var table = mf.getNodeComponents();
				JSONObject nodeTable = new JSONObject();
				var keySet = table.keySet();
				for (var id : keySet) {
					JSONObject idTable = new JSONObject();
					var node = table.get(id);
					var nodeName = node.getNodeName();
					var nodeType = node.getNodeType();
					var nodeContentClass = node.getNode().retrieveNodeContents().getClass().toString();

					idTable.put("name", nodeName);
					idTable.put("type", nodeType.name());
					idTable.put("contentClass", nodeContentClass);

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
			if ("GET".equals(exchange.getRequestMethod())) {
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
			} else {
				exchange.sendResponseHeaders(400, -1);
				exchange.close();
			}
		});

		String nodeListEndpoint = "/api/nodes/listAvailable";
		endpoints.put(nodeListEndpoint, exchange -> {
			setHeaders(exchange);

			if ("GET".equals(exchange.getRequestMethod())) {
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

			if ("GET".equals(exchange.getRequestMethod())) {
				var in = exchange.getRequestBody();
				byte[] requestBytes = in.readAllBytes();
				String requestString = new String(requestBytes);
				try {

					JSONObject request = new JSONObject(requestString);

					JSONObject responseObject = new JSONObject();

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
						index++;
					}

					responseObject.put("Count", index);

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
			if ("POST".equals(exchange.getRequestMethod())) {
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

					SwingUtilities.invokeAndWait(() -> {
						try {
							Component vidComp;
							vidComp = (Component) callback.invoke(null, objList);
							flowPane.add(vidComp);
						} catch (IllegalAccessException | InvocationTargetException e) {
							try {
								exchange.sendResponseHeaders(500, -1);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							e.printStackTrace();
							return;
						}
						try {
							exchange.sendResponseHeaders(200, -1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					exchange.sendResponseHeaders(400, -1);
					exchange.close();
				}
			}
		});

	}

	private void setHeaders(HttpExchange exchange) {
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.getResponseHeaders().add("Connection", "Keep-Alive");
	}

}
