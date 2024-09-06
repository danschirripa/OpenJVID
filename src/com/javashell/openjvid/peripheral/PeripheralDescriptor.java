package com.javashell.openjvid.peripheral;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation;

public final class PeripheralDescriptor {
	private HashMap<String, HashMap<String, String>> availableCodecs;
	private HashMap<String, String> environment;
	private InetAddress sourceAddress;

	public PeripheralDescriptor(HashMap<String, Method> availableCodecs) {
		this.availableCodecs = parseMethodMapping(availableCodecs);
		environment = new HashMap<String, String>();
		environment.put("java.version", System.getProperty("java.version"));
		environment.put("java.vendor", System.getProperty("java.vendor"));
		environment.put("os.arch", System.getProperty("os.arch"));
		environment.put("os.name", System.getProperty("os.name"));
		environment.put("os.version", System.getProperty("os.version"));
	}

	public PeripheralDescriptor() {

	}

	public Set<String> getAvailableCodecs() {
		return availableCodecs.keySet();
	}

	public HashMap<String, HashMap<String, String>> getCodecMetaInformation() {
		return availableCodecs;
	}

	public Map<String, String> getHd() {
		return environment;
	}

	protected void setInetAddress(InetAddress source) throws IOException {
		if (sourceAddress == null)
			this.sourceAddress = source;
		else
			throw new IOException("InetAddress already set for peripheral source.");
	}

	public InetAddress getInetAddress() {
		return sourceAddress;
	}

	private HashMap<String, HashMap<String, String>> parseMethodMapping(HashMap<String, Method> methods) {
		final HashMap<String, HashMap<String, String>> parsedMappings = new HashMap<>();
		for (String key : methods.keySet()) {
			Method m = methods.get(key);
			Class<?>[] paramTypes = m.getParameterTypes();
			final Parameter[] params = m.getParameters();
			int index = 0;

			final HashMap<String, String> methodMapping = new HashMap<String, String>();

			for (Class<?> param : paramTypes) {
				Parameter p = params[index];
				// Identify type name, or specified label
				ParameterLabelAnnotation.Label labelAnnotation = p.getAnnotation(ParameterLabelAnnotation.Label.class);
				String label = p.getName();
				if (labelAnnotation != null) {
					label = labelAnnotation.label();
				}

				// Parse parameter type and generate proper input panel

				if (param.isInstance(new String())) {
					methodMapping.put(label, "STRING");
				}

				try {
					if (param.isInstance(new URL("https://google.com"))) {
						methodMapping.put(label, "URL");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (param.isAssignableFrom(int.class)) {
					methodMapping.put(label, "INT");
				}

				if (param.isInstance(new Dimension())) {
					methodMapping.put(label, "DIMENSION");
				}

				if (param.isInstance(new File(""))) {
					methodMapping.put(label, "FILE");
				}
				index++;
			}
			parsedMappings.put(key, methodMapping);
		}
		return parsedMappings;
	}

}
