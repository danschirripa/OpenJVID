package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StringInputComponent extends JPanel {
	private static final long serialVersionUID = 1046830625005382768L;

	final JTextField stringInput;
	final JLabel inputLabel;

	public StringInputComponent(String label) {
		inputLabel = new JLabel(label);
		stringInput = new JTextField();
		stringInput.setColumns(15);

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(stringInput, BorderLayout.EAST);
		setToolTipText(label);
	}

	public String getString() {
		return stringInput.getText();
	}

}
