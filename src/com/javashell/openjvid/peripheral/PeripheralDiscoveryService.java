package com.javashell.openjvid.peripheral;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PeripheralDiscoveryService {

	public static final int PORT = 10606;
	public static final String ADDRESS = "224.0.1.200";
	private static String localJsonDescriptor;
	private static MulticastSocket socket;
	private static DatagramSocket senderSocket;
	private static Thread mCastThread;
	private static Thread tcpThread;
	private static Timer mCastAdvertisementTimer;
	private static final byte[] advertisementData = new byte[16];

	private static final Hashtable<InetAddress, PeripheralDescriptor> discoveredPeripherals = new Hashtable<InetAddress, PeripheralDescriptor>();

	public static void initializeService() throws IOException {
		SocketAddress sa = new InetSocketAddress(ADDRESS, PORT);
		socket = new MulticastSocket();
		senderSocket = new DatagramSocket();
		Stream<NetworkInterface> nics = NetworkInterface.networkInterfaces();
		Iterator<NetworkInterface> nicIter = nics.iterator();
		while (nicIter.hasNext()) {
			final NetworkInterface nic = nicIter.next();
			socket.joinGroup(sa, nic);
			System.out.println("Joined " + nic.getDisplayName());
		}

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
		Gson gson = builder.create();
		PeripheralDescriptor localDescriptor = new PeripheralDescriptor(null, 0, 0);
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
				try {
					ServerSocket ss = new ServerSocket(PORT);
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
					senderSocket.send(packet);
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
			discoveredPeripherals.put(sourceAddress, desc);
		}
	}

	private static PeripheralDescriptor retrievePeripheralDescriptor(InetAddress peripheral) throws IOException {
		Socket sock = new Socket(peripheral, PORT);

		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		sock.getOutputStream().write(PeripheralServerCommands.RETRIEVE_DESCRIPTOR.name().getBytes());
		PeripheralDescriptor desc = gson.fromJson(new InputStreamReader(sock.getInputStream()),
				PeripheralDescriptor.class);

		sock.close();

		return desc;
	}

	private static void handleClient(Socket sock) throws IOException {
		Scanner sc = new Scanner(sock.getInputStream());
		String command = sc.nextLine();

		if (command.equals(PeripheralServerCommands.RETRIEVE_DESCRIPTOR.name())) {
			sock.getOutputStream().write(localJsonDescriptor.getBytes());
		}
		sc.close();
		sock.close();
	}

	private enum PeripheralServerCommands {
		RETRIEVE_DESCRIPTOR {

		}
	}

}
