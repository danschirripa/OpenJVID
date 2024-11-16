package com.javashell.openjvid.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.flow.FlowController;

public class JackInputComponent extends JPanel {
	final JComboBox jackClientSelector;
	final JLabel inputLabel;

	public JackInputComponent(String label) {
		inputLabel = new JLabel(label);

		jackClientSelector = new JComboBox(FlowController.jackManager.getRegisteredClients().keySet().toArray());

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(jackClientSelector, BorderLayout.EAST);
		setToolTipText(label);
	}

	public JackInputClient getString() {
		return new JackInputClient((String) jackClientSelector.getSelectedItem());
	}

	public static class JackInputClient {
		public final String clientName;

		public JackInputClient(String clientName) {
			this.clientName = clientName;
		}
	}
}
