package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.flow.FlowController;
import com.javashell.openjvid.ui.components.input.JackInputComponent.JackInputClient;
import com.javashell.video.ingestors.TestPatternIngestor.TestPattern;

public class TestPatternInputComponent extends InputComponent<TestPattern> {
	private JComboBox testPatternSelector;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		inputLabel = new JLabel(label);

		testPatternSelector = new JComboBox(TestPattern.values());

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(testPatternSelector, BorderLayout.EAST);
		panel.setToolTipText(label);

		return panel;
	}

	@Override
	public TestPattern getParameter() {
		return (TestPattern) testPatternSelector.getSelectedItem();
	}

	public TestPattern attemptCreation(Object... params) {
		return TestPattern.valueOf((String) params[0]);
	}
}
