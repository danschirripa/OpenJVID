package com.javashell.openjvid.peripheral;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class PeripheralDescriptor {
	private HashSet<String> availableCodecs;
	private int numberOfInputs, numberOfOutputs;
	private HostDescriptor hd;

	public PeripheralDescriptor(HashSet<String> availableCodecs, int numberOfInputs, int numberOfOutputs) {
		this.availableCodecs = availableCodecs;
		this.numberOfInputs = numberOfInputs;
		this.numberOfOutputs = numberOfOutputs;
	}

	private class HostDescriptor {
		private final Map<String, String> environment;

		public HostDescriptor() {
			environment = new HashMap<String, String>();
			
			environment.put("java.version", System.getenv("java.version"));
			environment.put("java.vendor", System.getenv("java.vendor"));
			environment.put("os.arch", System.getenv("os.arch"));
			environment.put("os.name", System.getenv("os.name"));
			environment.put("os.version", System.getenv("os.version"));
		}
	}

}
