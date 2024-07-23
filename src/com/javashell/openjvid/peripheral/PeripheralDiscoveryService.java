package com.javashell.openjvid.peripheral;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class PeripheralDiscoveryService {

	public static final int PORT = 10606;
	public static final String ADDRESS = "224.0.1.200";
	private static MulticastSocket socket;
	private static Thread mCastThread;
	private static Timer mCastAdvertisementTimer;
	private static final byte[] advertisementData = new byte[16];

	public static void initializeService() throws IOException {
		SocketAddress sa = new InetSocketAddress(ADDRESS, PORT);
		socket = new MulticastSocket(sa);
		Stream<NetworkInterface> nics = NetworkInterface.networkInterfaces();
		Iterator<NetworkInterface> nicIter = nics.iterator();
		while (nicIter.hasNext()) {
			socket.joinGroup(sa, nicIter.next());
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

		mCastThread = new Thread(new Runnable() {
			public void run() {
				byte[] data = new byte[16];
				DatagramPacket packet = new DatagramPacket(data, 16);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mCastThread.setName("OpenJVID - Peripheral Discovery");
		mCastThread.start();

		final DatagramPacket packet = new DatagramPacket(advertisementData, 16);

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
		mCastAdvertisementTimer.scheduleAtFixedRate(advertisementTask, 0, 10000);
	}

	private static void parsePacket(DatagramPacket packet) {
		final InetAddress sourceAddress = packet.getAddress();
		final byte[] data = packet.getData();
		if(Arrays.equals(data, advertisementData)) {
			//Parse peripheral advertisement data
		}
	}

}
