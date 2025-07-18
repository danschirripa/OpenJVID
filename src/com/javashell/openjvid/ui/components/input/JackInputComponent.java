package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.flow.FlowController;
import com.javashell.openjvid.ui.components.input.JackInputComponent.JackInputClient;

public class JackInputComponent extends InputComponent<JackInputClient> {
	private JComboBox jackClientSelector;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		inputLabel = new JLabel(label);

		jackClientSelector = new JComboBox(FlowController.jackManager.getRegisteredClients().keySet().toArray());

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(jackClientSelector, BorderLayout.EAST);
		panel.setToolTipText(label);

		return panel;
	}

	public JackInputClient getParameter() {
		return new JackInputClient((String) jackClientSelector.getSelectedItem());
	}

	public static class JackInputClient {
		public final String clientName;

		public JackInputClient(String clientName) {
			this.clientName = clientName;
		}
	}
}
