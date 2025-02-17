package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

public class IntegerInputComponent extends JPanel {
	final JFormattedTextField intInput;
	final JLabel inputLabel;

	public IntegerInputComponent(String label) {
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

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(intInput, BorderLayout.EAST);
		setToolTipText(label);
	}

	public int getInt() {
		return (int) intInput.getValue();
	}
}
