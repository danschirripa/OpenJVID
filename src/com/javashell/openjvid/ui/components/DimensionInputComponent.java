package com.javashell.openjvid.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DimensionInputComponent extends JPanel {

	private static final long serialVersionUID = 6394672588698710214L;

	final JTextField widthInput, heightInput;
	final JLabel inputLabel;

	public DimensionInputComponent(String label) {
		widthInput = new JTextField();
		heightInput = new JTextField();
		inputLabel = new JLabel(label);

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());
		add(inputLabel, BorderLayout.NORTH);
		
		JPanel inputPanel = new JPanel();
		BoxLayout layout = new BoxLayout(inputPanel, BoxLayout.LINE_AXIS);
		inputPanel.setLayout(layout);
		inputPanel.add(widthInput);
		inputPanel.add(heightInput);
		add(inputPanel, BorderLayout.CENTER);
		setToolTipText(label);
	}

	public Dimension getDimension() {
		int width = Integer.parseInt(widthInput.getText());
		int height = Integer.parseInt(heightInput.getText());
		return new Dimension(width, height);
	}

}
