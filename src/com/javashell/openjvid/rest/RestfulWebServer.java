package com.javashell.openjvid.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.javashell.flow.FlowController;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.MainFrame;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
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
			JSONObject statusObject = new JSONObject();
			statusObject.put("IsPlaying", FlowController.isFlowing());
			statusObject.put("JackCapable", FlowController.jackManager.isJackCapable());

			var statusString = statusObject.toString();
			exchange.sendResponseHeaders(200, statusString.getBytes().length);
			OutputStream out = exchange.getResponseBody();
			out.write(statusString.getBytes());
			out.flush();
			exchange.close();
		});

		String transportEndpoint = "/api/transport";
		endpoints.put(transportEndpoint, exchange -> {
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

		String listNodesEndpoint = "/api/nodes";
		endpoints.put(listNodesEndpoint, exchange -> {
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

	}

}
