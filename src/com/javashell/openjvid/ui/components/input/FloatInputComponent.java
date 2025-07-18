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

public class FloatInputComponent extends InputComponent<Float> {
	private JFormattedTextField floatInput;
	private JLabel inputLabel;
	private NumberFormatter formatter;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
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

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(floatInput, BorderLayout.EAST);
		panel.setToolTipText(label);
		return panel;
	}

	public Float getParameter() {
		return (float) floatInput.getValue();
	}
}
