package com.javashell.openjvid.ui.components.input;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.video.egressors.experimental.FFMPEGStreamEgressor;

public class FFMPEGVideoCodecInputComponent extends JPanel {
	final JComboBox<FFMPEGStreamEgressor.VideoCodec> ffmpegCodecs;
	final JLabel inputLabel;

	public FFMPEGVideoCodecInputComponent(String label) {
		inputLabel = new JLabel(label);

		ffmpegCodecs = new JComboBox<FFMPEGStreamEgressor.VideoCodec>(FFMPEGStreamEgressor.VideoCodec.values());

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		setLayout(new BorderLayout());

		add(inputLabel, BorderLayout.WEST);
		add(ffmpegCodecs, BorderLayout.EAST);
		setToolTipText(label);
	}

	public FFMPEGStreamEgressor.VideoCodec getCodec() {
		return (FFMPEGStreamEgressor.VideoCodec) ffmpegCodecs.getSelectedItem();
	}

}
