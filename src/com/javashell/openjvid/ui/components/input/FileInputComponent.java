package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileInputComponent extends InputComponent<File> {
	private JTextField pathInputField;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		pathInputField = new JTextField();
		inputLabel = new JLabel(label);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());
		panel.add(inputLabel, BorderLayout.NORTH);

		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.LINE_AXIS);
		inputPanel.setLayout(layout);
		inputPanel.add(pathInputField);
		panel.add(inputPanel, BorderLayout.CENTER);
		panel.setToolTipText(label);
		return panel;
	}

	public File getParameter() {
		return new File(pathInputField.getText());
	}
}
