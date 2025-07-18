package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.video.egressors.experimental.FFMPEGStreamEgressor;

public class FFMPEGVideoCodecInputComponent extends InputComponent<FFMPEGStreamEgressor.VideoCodec> {
	private JComboBox<FFMPEGStreamEgressor.VideoCodec> ffmpegCodecs;
	private JLabel inputLabel;

	public JPanel getPanel(String label) {
		JPanel panel = new JPanel();
		inputLabel = new JLabel(label);

		ffmpegCodecs = new JComboBox<FFMPEGStreamEgressor.VideoCodec>(FFMPEGStreamEgressor.VideoCodec.values());

		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		panel.setLayout(new BorderLayout());

		panel.add(inputLabel, BorderLayout.WEST);
		panel.add(ffmpegCodecs, BorderLayout.EAST);
		panel.setToolTipText(label);

		return panel;
	}

	public FFMPEGStreamEgressor.VideoCodec getParameter() {
		return (FFMPEGStreamEgressor.VideoCodec) ffmpegCodecs.getSelectedItem();
	}

}
