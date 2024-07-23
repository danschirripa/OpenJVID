package com.javashell.openjvid.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileInputComponent extends JPanel {
	private JTextField pathInputField;
	private JLabel inputLabel;
	
	public FileInputComponent(String label) {
		pathInputField = new JTextField();
		inputLabel = new JLabel(label);

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());
		add(inputLabel, BorderLayout.NORTH);

		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.LINE_AXIS);
		inputPanel.setLayout(layout);
		inputPanel.add(pathInputField);
		add(inputPanel, BorderLayout.CENTER);
		setToolTipText(label);
	}
	
	public File getFile() {
		return new File(pathInputField.getText());
	}
}
