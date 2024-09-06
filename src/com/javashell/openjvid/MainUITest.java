package com.javashell.openjvid;

import com.javashell.flow.FlowController;
import com.javashell.openjvid.peripheral.PeripheralDiscoveryService;

public class MainUITest {
	public static void main(String[] args) throws Exception {
		Thread hookThread = new Thread(new Runnable() {
			public void run() {
				System.err.println("SHUTDOWN");
				var traces = Thread.getAllStackTraces();
				for (var key : traces.keySet()) {
					System.err.println(key.getName());
					var trace = traces.get(key);
					for (var elements : trace) {
						System.err.println(elements.toString());
					}
					System.err.println("---------------------------------");
				}
			}
		});

		Runtime.getRuntime().addShutdownHook(hookThread);

		MainFrame mf = new MainFrame();

		FlowController.startFlowControl();

		PeripheralDiscoveryService.initializeService();

		try {
			while (mf.isVisible()) {
				Thread.sleep(33);
				mf.repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
