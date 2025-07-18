package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

public class IntegerInputComponent extends InputComponent<Integer> {
	private JFormattedTextField intInput;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		inputLabel = new JLabel(label);

		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);

		formatter.setCommitsOnValidEdit(true);

		intInput = new JFormattedTextField(formatter);
		intInput.setColumns(15);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(intInput, BorderLayout.EAST);
		panel.setToolTipText(label);
		return panel;
	}

	public Integer getParameter() {
		return (int) intInput.getValue();
	}
}
