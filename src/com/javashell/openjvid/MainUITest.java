package com.javashell.openjvid;

import java.io.File;

import javax.swing.SwingUtilities;

import com.hk.lua.LuaLibrary;
import com.javashell.flow.FlowController;
import com.javashell.openjvid.lua.JVIDLuaLibrary;
import com.javashell.openjvid.lua.JavashellLuaLibrary;
import com.javashell.openjvid.lua.LuaManager;
import com.javashell.openjvid.lua.exceptions.LuaLibraryLoadException;
import com.javashell.openjvid.peripheral.PeripheralDiscoveryService;

public class MainUITest {
	public static void main(String[] args) throws Exception {
		// Setup system exit hooks to dump stack trace on clean exit for debugging
		Thread hookThread = new Thread(new Runnable() {
			public void run() {
				System.err.println("SHUTDOWN, DUMPING STACK TRACE FOR DEBUG");
				var traces = Thread.getAllStackTraces();
				for (var key : traces.keySet()) {
					System.err.println(key.getName());
					var trace = traces.get(key);
					for (var elements : trace) {
						System.err.println(elements.toString());
					}
					System.err.println("---------------------------------");
				}
				System.err.println("STACKDUMP");
			}
		});

		// If a configuration file was provided as an argument, attempt to verify the
		// specified file
		File savedConfig = null;

		if (args.length > 0) {
			savedConfig = new File(args[0]);
			if (!savedConfig.exists()) {
				System.err.println("Specified configuration file innaccessible, " + savedConfig.getAbsolutePath());
				savedConfig = null;
			}
		}

		// Add the exit hook declared above
		Runtime.getRuntime().addShutdownHook(hookThread);

		// Create the frame, (false = not emulation, true = emulation mode)
		// Emulation mode creates dummy objects representative of actual flownodes, for
		// graph creation without physical hardware
		MainFrame mf = new MainFrame(false);

		// Import and register all Lua extensions
		try {
			LuaManager.registerHook("Socket", JavashellLuaLibrary.SOCKET);
			LuaManager.registerHook("Timer", JavashellLuaLibrary.TIMER);
			LuaManager.registerHook("Package", LuaLibrary.PACKAGE);
			LuaManager.registerHook("Generic", JavashellLuaLibrary.JSH);
			LuaManager.registerHook("Desktop", JavashellLuaLibrary.DESKTOP);

			JVIDLuaLibrary jvidLib = new JVIDLuaLibrary(mf, mf.getFlowPane());

			final LuaLibrary<JVIDLuaLibrary.Lib> SYSTEM = new LuaLibrary<>("System", JVIDLuaLibrary.Lib.class);
			LuaManager.registerHook("System", SYSTEM);

		} catch (LuaLibraryLoadException e) {
			e.printStackTrace();
		}

		// Initiate the FlowController (Begin processing the graph)
		FlowController.startFlowControl();

		// Initialize the PeripheralDiscoveryService to find and communicate with other
		// instances of OpenJVID on the local network
		PeripheralDiscoveryService.initializeService();

		// Finalize the file object for later loading in the Swing thread
		final File toLoad = savedConfig;

		if (savedConfig != null)
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					// Load the specified configuration to the graph
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
