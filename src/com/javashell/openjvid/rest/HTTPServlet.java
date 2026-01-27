package com.javashell.openjvid.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Simple HTTP handler which serves a tiny single-page UI for exercising the
 * REST endpoints exposed by the embedded HttpServer. The page is intentionally
 * lightweight and uses fetch() to call the API on the same host/origin.
 *
 * To expose the page, register an instance of this handler with the server, for
 * example:
 *
 * server.createContext("/", new HTTPServlet());
 *
 */
public class HTTPServlet implements HttpHandler {

	// Cached HTML content loaded from assets/rest_console.html (classpath or file)
	private final String html;

	public HTTPServlet() {
		this.html = loadHtml();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// Only serve GET for the UI page
		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(405, -1);
			exchange.close();
			return;
		}

		byte[] bytes = html.getBytes(StandardCharsets.UTF_8);

		exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
		exchange.sendResponseHeaders(200, bytes.length);
		try (OutputStream out = exchange.getResponseBody()) {
			out.write(bytes);
			out.flush();
		}
		exchange.close();
	}

	private String loadHtml() {
		// First, try to load from classpath so that the file can be packaged inside a
		// JAR
		String resourcePath = "/rest_console.html";
		try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
			if (in != null) {
				byte[] bytes = in.readAllBytes();
				return new String(bytes, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			// Ignore and fallback to file system
		}

		// Fallback: try to load from the repository assets folder relative to working
		// dir
		Path p = Paths.get("assets", "rest_console.html");
		try {
			if (Files.exists(p)) {
				byte[] bytes = Files.readAllBytes(p);
				return new String(bytes, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			// Fall through
		}

		// As a last resort, return a minimal built-in HTML informing about missing file
		return "<!doctype html><html><head><meta charset=\"utf-8\"><title>OpenJVID REST Console</title></head><body><h2>OpenJVID REST Console</h2><p>rest_console.html not found in classpath or assets folder.</p></body></html>";
	}
}
