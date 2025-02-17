package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class URLInputComponent extends JPanel{
	
	private static final long serialVersionUID = 2500697982517696041L;
	
	final JTextField urlInput;
	final JLabel inputLabel;

	public URLInputComponent(String label) {
		inputLabel = new JLabel(label);
		urlInput = new JTextField();
		urlInput.setColumns(15);

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(urlInput, BorderLayout.EAST);
		setToolTipText(label);
	}

	public URL getURL() throws MalformedURLException {
		return new URL(urlInput.getText());
	}
}
