package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

public class FloatInputComponent extends JPanel {
	final JFormattedTextField floatInput;
	final JLabel inputLabel;
	final NumberFormatter formatter;

	public FloatInputComponent(String label) {
		inputLabel = new JLabel(label);

		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		format.setDecimalSeparatorAlwaysShown(true);

		formatter = new NumberFormatter(format);
		formatter.setValueClass(Float.class);
		formatter.setMinimum(0.0f);
		formatter.setMaximum(Float.MAX_VALUE);
		formatter.setAllowsInvalid(false);

		formatter.setCommitsOnValidEdit(true);

		floatInput = new JFormattedTextField(formatter);
		floatInput.setColumns(15);

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(floatInput, BorderLayout.EAST);
		setToolTipText(label);
	}

	public float getFloat() {
		return (float) floatInput.getValue();
	}
}
