package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringInputComponent extends InputComponent<String> {
	private static final long serialVersionUID = 1046830625005382768L;

	private JTextField stringInput;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		JLabel inputLabel = new JLabel(label);
		stringInput = new JTextField();
		stringInput.setColumns(15);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(stringInput, BorderLayout.EAST);
		panel.setToolTipText(label);

		return panel;
	}

	@Override
	public String getParameter() {
		return stringInput.getText();
	}

}
