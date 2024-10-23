package com.javashell.openjvid;

import java.io.File;

import javax.swing.SwingUtilities;

import com.javashell.flow.FlowController;
import com.javashell.openjvid.configuration.jVidConfigurationParser;
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

		File savedConfig = null;

		if (args.length > 0) {
			savedConfig = new File(args[0]);
			if (!savedConfig.exists()) {
				System.err.println("Specified configuration file innaccessible, " + savedConfig.getAbsolutePath());
				savedConfig = null;
			}
		}

		Runtime.getRuntime().addShutdownHook(hookThread);

		MainFrame mf = new MainFrame();

		FlowController.startFlowControl();

		PeripheralDiscoveryService.initializeService();

		final File toLoad = savedConfig;

		if (savedConfig != null)
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					mf.loadConfiguration(toLoad);
				}
			});

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
