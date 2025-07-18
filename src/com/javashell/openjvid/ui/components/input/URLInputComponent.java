package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class URLInputComponent extends InputComponent<URL> {

	private static final long serialVersionUID = 2500697982517696041L;

	private JTextField urlInput;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		inputLabel = new JLabel(label);
		urlInput = new JTextField();
		urlInput.setColumns(15);

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(urlInput, BorderLayout.EAST);
		panel.setToolTipText(label);
		return panel;
	}

	@Override
	public URL getParameter() {
		try {
			return new URL(urlInput.getText());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
