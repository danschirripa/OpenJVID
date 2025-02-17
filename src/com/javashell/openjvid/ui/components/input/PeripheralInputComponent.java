package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.InetAddress;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.openjvid.peripheral.PeripheralDescriptor;
import com.javashell.openjvid.peripheral.PeripheralDiscoveryService;

public class PeripheralInputComponent extends JPanel {
	private JComboBox<InetAddress> peripheralChoices;
	private JLabel inputLabel;

	public PeripheralInputComponent(String label) {
		inputLabel = new JLabel(label);
		Set<InetAddress> discoveredAddresses = PeripheralDiscoveryService.getDiscoveredPeripherals().keySet();
		InetAddress[] addresses = new InetAddress[discoveredAddresses.size()];
		addresses = discoveredAddresses.toArray(addresses);
		peripheralChoices = new JComboBox<InetAddress>(addresses);
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());
		add(inputLabel, BorderLayout.NORTH);

		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.LINE_AXIS);
		inputPanel.setLayout(layout);
		inputPanel.add(peripheralChoices);
		add(inputPanel, BorderLayout.CENTER);
		setToolTipText(label);
	}

	public PeripheralDescriptor getPeripheralDescriptor() {
		return PeripheralDiscoveryService.getDiscoveredPeripherals().get(peripheralChoices.getSelectedItem());
	}
}
