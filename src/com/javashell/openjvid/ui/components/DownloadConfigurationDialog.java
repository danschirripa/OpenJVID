package com.javashell.openjvid.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.javashell.openjvid.peripheral.PeripheralDiscoveryService;

public class DownloadConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 1119674200613690938L;
	private JComboBox<InetAddress> peripheralChoices;
	private JLabel inputLabel;
	private File confFile;

	public DownloadConfigurationDialog() {
		JPanel mainPanel = new JPanel();
		JPanel selectionPanel = createPeripheralSelectionPanel();
		JPanel confSelectionPanel = createConfigurationSelectionPanel();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(selectionPanel, BorderLayout.NORTH);
		mainPanel.add(confSelectionPanel, BorderLayout.CENTER);

		JButton uploadConfig = new JButton("Download");
		uploadConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				var selectedIP = (InetAddress) peripheralChoices.getSelectedItem();
				var peripheral = PeripheralDiscoveryService.getDiscoveredPeripherals().get(selectedIP);
				String config = PeripheralDiscoveryService.downloadConfiguration(peripheral);
				try {
					FileOutputStream fout = new FileOutputStream(confFile);
					fout.write(config.getBytes());
					fout.flush();
					fout.close();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
				}
				JOptionPane.showMessageDialog(null, "Configuration download complete");
			}
		});

		mainPanel.add(uploadConfig, BorderLayout.SOUTH);

		setSize(400, 500);
		setContentPane(mainPanel);
	}

	private JPanel createPeripheralSelectionPanel() {
		PeripheralDiscoveryService.getDiscoveredPeripherals();
		final JPanel peripheralSelectionPanel = new JPanel();

		Set<InetAddress> discoveredAddresses = PeripheralDiscoveryService.getDiscoveredPeripherals().keySet();
		InetAddress[] addresses = new InetAddress[discoveredAddresses.size()];
		addresses = discoveredAddresses.toArray(addresses);
		peripheralChoices = new JComboBox<InetAddress>(addresses);
		inputLabel = new JLabel("Discovered Peripherals");

		peripheralSelectionPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		peripheralSelectionPanel.setLayout(new BorderLayout());
		peripheralSelectionPanel.add(inputLabel, BorderLayout.NORTH);

		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.LINE_AXIS);
		inputPanel.setLayout(layout);
		inputPanel.add(peripheralChoices);
		peripheralSelectionPanel.add(inputPanel, BorderLayout.CENTER);

		return peripheralSelectionPanel;
	}

	private JPanel createConfigurationSelectionPanel() {
		final JPanel configSelectionPanel = new JPanel();

		JButton chooseButton = new JButton("Select File");
		JTextField selectedFileName = new JTextField();
		selectedFileName.setEditable(false);
		selectedFileName.setText("No file selected");
		selectedFileName.setBackground(Color.WHITE);
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser selectionDialog = new JFileChooser();
				if (selectionDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					confFile = selectionDialog.getSelectedFile();
					selectedFileName.setText(confFile.getName());
				}
			}
		});
		configSelectionPanel.add(chooseButton);
		configSelectionPanel.add(selectedFileName);
		return configSelectionPanel;
	}

}
