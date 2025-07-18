package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DimensionInputComponent extends InputComponent<Dimension> {

	private static final long serialVersionUID = 6394672588698710214L;

	private JTextField widthInput, heightInput;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		widthInput = new JTextField();
		heightInput = new JTextField();
		inputLabel = new JLabel(label);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());
		panel.add(inputLabel, BorderLayout.NORTH);

		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.LINE_AXIS);
		inputPanel.setLayout(layout);
		inputPanel.add(widthInput);
		inputPanel.add(heightInput);
		panel.add(inputPanel, BorderLayout.CENTER);
		panel.setToolTipText(label);
		return panel;
	}

	@Override
	public Dimension getParameter() {
		int width = Integer.parseInt(widthInput.getText());
		int height = Integer.parseInt(heightInput.getText());
		return new Dimension(width, height);
	}

}
