package com.javashell.openjvid.ui.components.input.gstreamer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bytedeco.javacv.VideoInputFrameGrabber;

public class VideoInputDeviceInputComponent extends JPanel {
	JComboBox<String> videoInputSelector;
	final JLabel inputLabel;

	final Hashtable<String, String> nameToPathMapping = new Hashtable<String, String>();

	public VideoInputDeviceInputComponent(String label) {
		inputLabel = new JLabel(label);

		File[] videoDevices = new File("/sys/class/video4linux").listFiles();

		String[] videoDeviceNames = new String[videoDevices.length];
		int i = 0;

		for (File device : videoDevices) {
			String deviceName = device.getName();
			try {
				final File name = new File(device, "name");
				Scanner sc = new Scanner(new FileInputStream(name));
				deviceName = sc.nextLine();
				sc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String newDeviceName = deviceName;
			int count = 0;
			while (nameToPathMapping.containsKey(newDeviceName)) {
				newDeviceName = deviceName + count;
				count++;
			}
			deviceName = newDeviceName;
			nameToPathMapping.put(deviceName, device.getName());
			videoDeviceNames[i] = deviceName;
			i++;
		}

		videoInputSelector = new JComboBox<String>(videoDeviceNames);

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(videoInputSelector, BorderLayout.EAST);
		setToolTipText(label);
	}

	public VideoInputClient getString() {
		return new VideoInputClient((String) videoInputSelector.getSelectedItem());
	}

	public static class VideoInputClient {
		public final String clientName;

		public VideoInputClient(String clientName) {
			this.clientName = clientName;
		}
	}
}
