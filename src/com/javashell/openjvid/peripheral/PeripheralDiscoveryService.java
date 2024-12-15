package com.javashell.openjvid.peripheral;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.configuration.jVidConfigurationParser;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.ui.AddComponentDialog;

public class PeripheralDiscoveryService {

	public static final int PORT = 10606;
	public static final String ADDRESS = "224.0.1.200";
	private static String localJsonDescriptor;
	private static MulticastSocket socket;
	private static Thread mCastThread;
	private static Thread tcpThread;
	private static Timer mCastAdvertisementTimer;
	private static final byte[] advertisementData = new byte[16];
	private static MainFrameActionHandler handler = null;
	private static JNodeFlowPane flowPane = null;

	private static final Hashtable<InetAddress, PeripheralDescriptor> discoveredPeripherals = new Hashtable<InetAddress, PeripheralDescriptor>();

	public static void initializeService() throws IOException {
		InetSocketAddress sa = new InetSocketAddress(ADDRESS, PORT);
		socket = new MulticastSocket(PORT);
		Stream<NetworkInterface> nics = NetworkInterface.networkInterfaces();
		Iterator<NetworkInterface> nicIter = nics.iterator();
		while (nicIter.hasNext()) {
			final NetworkInterface nic = nicIter.next();
			try {
				socket.joinGroup(sa, nic);
				System.out.println("Joined " + nic.getDisplayName());
			} catch (Exception e) {
				System.err.println("Failed to join on " + nic.getDisplayName());
			}
		}

		socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);

		advertisementData[0] = 'O';
		advertisementData[1] = 'p';
		advertisementData[2] = 'e';
		advertisementData[3] = 'n';
		advertisementData[4] = 'J';
		advertisementData[5] = 'V';
		advertisementData[6] = 'I';
		advertisementData[7] = 'D';
		advertisementData[8] = advertisementData[7];
		advertisementData[9] = advertisementData[6];
		advertisementData[10] = advertisementData[5];
		advertisementData[11] = advertisementData[4];
		advertisementData[12] = advertisementData[3];
		advertisementData[13] = advertisementData[2];
		advertisementData[14] = advertisementData[1];
		advertisementData[15] = advertisementData[0];

		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.serializeNulls();
		Gson gson = builder.create();

		PeripheralDescriptor localDescriptor = new PeripheralDescriptor(AddComponentDialog.callBackMethods);
		localJsonDescriptor = gson.toJson(localDescriptor);

		mCastThread = new Thread(new Runnable() {
			public void run() {
				byte[] data = new byte[16];
				DatagramPacket packet = new DatagramPacket(data, 16);
				try {
					socket.receive(packet);
					parsePacket(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mCastThread.setName("OpenJVID - Peripheral Discovery");
		mCastThread.start();

		tcpThread = new Thread(new Runnable() {
			public void run() {
				try (ServerSocket ss = new ServerSocket(PORT)) {
					while (!ss.isClosed()) {
						Socket s = ss.accept();
						// Deal with inter-peripheral tcp communications
						final Thread clientThread = new Thread(new Runnable() {
							public void run() {
								try {
									handleClient(s);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						clientThread.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		tcpThread.setName("OpenJVID - Peripheral Communication");
		tcpThread.start();

		final DatagramPacket packet = new DatagramPacket(advertisementData, 16, InetAddress.getByName(ADDRESS), PORT);

		TimerTask advertisementTask = new TimerTask() {

			@Override
			public void run() {
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		mCastAdvertisementTimer = new Timer();
		mCastAdvertisementTimer.scheduleAtFixedRate(advertisementTask, 0, 5000);
	}

	private static void parsePacket(DatagramPacket packet) throws IOException {
		final InetAddress sourceAddress = packet.getAddress();
		final byte[] data = packet.getData();
		if (Arrays.equals(data, advertisementData) && !discoveredPeripherals.containsKey(sourceAddress)) {
			// Parse peripheral advertisement data
			final PeripheralDescriptor desc = retrievePeripheralDescriptor(sourceAddress);
			desc.setInetAddress(sourceAddress);
			discoveredPeripherals.put(sourceAddress, desc);
			System.out.println("Discovered " + sourceAddress.getCanonicalHostName());
		}
	}

	private static PeripheralDescriptor retrievePeripheralDescriptor(InetAddress peripheral) throws IOException {
		Socket sock = new Socket(peripheral, PORT);

		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		sock.getOutputStream().write((PeripheralServerCommands.RETRIEVE_DESCRIPTOR.name() + "\n").getBytes());
		PeripheralDescriptor desc = gson.fromJson(new InputStreamReader(sock.getInputStream()),
				PeripheralDescriptor.class);

		sock.close();

		Map<String, String> env = desc.getHd();
		for (String key : env.keySet()) {
			System.out.println(key + ": " + env.get(key));
		}
		return desc;
	}

	private static void handleClient(Socket sock) throws IOException {
		final BufferedInputStream bin = new BufferedInputStream(sock.getInputStream());
		String command = readUntil((byte) '\n', bin);

		if (command.equals(PeripheralServerCommands.RETRIEVE_DESCRIPTOR.name())) {
			sock.getOutputStream().write(localJsonDescriptor.getBytes());
		} else if (command.equals(PeripheralServerCommands.UPDATE_DESCRIPTOR.name())) {
		} else if (command.equals(PeripheralServerCommands.NEGOTIATE_PERIPHERAL_CONNECTION.name())) {
			System.out.println("Received peripheral negotiation");
			int port = Integer.parseInt(readUntil((byte) '\n', bin));
			UUID sessionID = UUID.fromString(readUntil((byte) '\n', bin));
			System.out.println("Negotiation: " + port + " - " + sessionID.toString());
			handler.addOpenJVIDPeripheral(discoveredPeripherals.get(sock.getInetAddress()), sessionID, port);
			System.out.println("Negotiation complete");
		} else if (command.equals(PeripheralServerCommands.UPLOAD_CONFIGURATION.name())) {
			System.out.println("Receiving configuration from " + sock.getInetAddress().getCanonicalHostName());
			File tmpConfigFile = File.createTempFile("jvid", "tmp");

			final byte[] lengthBytes = bin.readNBytes(4);
			final int length = ByteBuffer.wrap(lengthBytes).getInt();
			try (FileOutputStream fout = new FileOutputStream(tmpConfigFile)) {
				System.out.println("Reading " + length);
				int finalLength = 0;
				for (int i = 0; i < length; i++) {
					fout.write(bin.read());
					finalLength = i;
				}
				System.out.println("Read " + finalLength);
				fout.flush();
				fout.close();
				System.out.println("Configuration received: " + tmpConfigFile.getAbsolutePath());
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						jVidConfigurationParser.loadConfiguration(flowPane, tmpConfigFile);
					}
				});
				System.out.println("Configuration loaded");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equals(PeripheralServerCommands.DOWNLOAD_CONFIGURATION.name())) {
			final String configurationString = jVidConfigurationParser.dumpConfiguration(flowPane);
			sock.getOutputStream().write(configurationString.getBytes());
		}
		bin.close();
		sock.close();
	}

	public static void setMainFrameActionHandler(MainFrameActionHandler handler, JNodeFlowPane flowPane) {
		PeripheralDiscoveryService.handler = handler;
		PeripheralDiscoveryService.flowPane = flowPane;
	}

	public static Socket negotiatePeripheralCommunications(PeripheralDescriptor pd, ServerSocket serv, UUID sessionID) {
		try (Socket sock = new Socket()) {
			sock.connect(new InetSocketAddress(pd.getInetAddress(), PORT));
			sock.getOutputStream().write((PeripheralServerCommands.NEGOTIATE_PERIPHERAL_CONNECTION.name() + "\n"
					+ serv.getLocalPort() + "\n" + sessionID.toString() + "\n").getBytes());
			sock.close();
			return serv.accept();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void uploadConfiguration(File configuration, PeripheralDescriptor desc) {
		System.out.println("Sending configuration file " + configuration.getName() + " to "
				+ desc.getInetAddress().getCanonicalHostName());
		try (Socket sock = new Socket()) {
			final FileInputStream fin = new FileInputStream(configuration);
			final byte[] confBytes = fin.readAllBytes();
			sock.connect(new InetSocketAddress(desc.getInetAddress(), PORT));
			System.out.println("Connection successful");
			sock.getOutputStream().write((PeripheralServerCommands.UPLOAD_CONFIGURATION.name() + "\n").getBytes());
			sock.getOutputStream().write(ByteBuffer.allocate(4).putInt(confBytes.length).array());
			System.out.println("Wrote UPLOAD\\n " + confBytes.length);
			for (int i = 0; i < confBytes.length; i++) {
				sock.getOutputStream().write(confBytes[i]);
			}
			System.out.println("Wrote conf");
			fin.close();
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Hashtable<InetAddress, PeripheralDescriptor> getDiscoveredPeripherals() {
		return discoveredPeripherals;
	}

	private enum PeripheralServerCommands {
		RETRIEVE_DESCRIPTOR, UPDATE_DESCRIPTOR, NEGOTIATE_PERIPHERAL_CONNECTION, UPLOAD_CONFIGURATION,
		DOWNLOAD_CONFIGURATION
	}

	private static String readUntil(byte delimiter, InputStream in) throws IOException {
		String s = "";
		byte[] next = new byte[1];
		while ((next[0] = (byte) in.read()) != delimiter) {
			s = s + new String(next);
		}
		return s;
	}

}
