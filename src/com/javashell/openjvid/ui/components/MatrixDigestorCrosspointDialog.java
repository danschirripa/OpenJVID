package com.javashell.openjvid.ui.components;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.video.VideoProcessor;
import com.javashell.video.digestors.MatrixDigestor;

public class MatrixDigestorCrosspointDialog extends JDialog {
	private MatrixDigestor matrix;
	private final HashSet<jVidNodeComponent<VideoProcessor>> sources, sinks, transceivers;
	private final HashMap<jVidNodeComponent<VideoProcessor>, HashSet<jVidNodeComponent<VideoProcessor>>> crosspoints;
	private JPopupMenu popupMenu;

	private JPanel lastCrosspointPanel;

	public MatrixDigestorCrosspointDialog() {
		sources = new HashSet<jVidNodeComponent<VideoProcessor>>();
		sinks = new HashSet<jVidNodeComponent<VideoProcessor>>();
		transceivers = new HashSet<jVidNodeComponent<VideoProcessor>>();
		crosspoints = new HashMap<jVidNodeComponent<VideoProcessor>, HashSet<jVidNodeComponent<VideoProcessor>>>();

		popupMenu = new JPopupMenu();
		JMenuItem editCrosspoints = new JMenuItem("Crosspoints");
		editCrosspoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isVisible()) {
					createAndShowDialog();
				}
			}
		});
		popupMenu.add(editCrosspoints);

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				remove(lastCrosspointPanel);
				lastCrosspointPanel = null;
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

		});
	}

	public void setMatrix(MatrixDigestor matrix) {
		this.matrix = matrix;
	}

	public void addSource(jVidNodeComponent<VideoProcessor> source) {
		sources.add(source);
		crosspoints.put(source, new HashSet<jVidNodeComponent<VideoProcessor>>());
	}

	public void addSink(jVidNodeComponent<VideoProcessor> sink) {
		sinks.add(sink);
	}

	public void addTransceiver(jVidNodeComponent<VideoProcessor> transceiver) {
		transceivers.add(transceiver);
		crosspoints.put(transceiver, new HashSet<jVidNodeComponent<VideoProcessor>>());
	}

	public void removeComponent(jVidNodeComponent<VideoProcessor> comp) {
		sources.remove(comp);
		sinks.remove(comp);
		transceivers.remove(comp);
		crosspoints.remove(comp);
	}

	public void createAndShowDialog() {
		if (lastCrosspointPanel != null)
			remove(lastCrosspointPanel);

		int numSinks = 0, numSources = 0;
		numSinks = sinks.size() + transceivers.size();
		numSources = sources.size() + transceivers.size();

		System.out.println("Total sources " + numSources);
		System.out.println("Total sinks " + numSinks);

		GridLayout layout = new GridLayout(numSources + 1, numSinks + 1);
		JPanel crossPointPanel = new JPanel();
		crossPointPanel.setLayout(layout);

		for (var sink : sinks) {
			JLabel sinkLabel = new JLabel(sink.getNodeName());
			sinkLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			System.out.println("Generated sink label");
			crossPointPanel.add(sinkLabel);
		}

		for (var source : sources) {
			JLabel sourceLabel = new JLabel(source.getNodeName());
			crossPointPanel.add(sourceLabel);
			for (var sink : sinks) {
				JButton crosspointButton = createCrosspointButton(source, sink);
				if (matrix.matrix.get(source.getNode()) != null
						&& matrix.matrix.get(source.getNode()).contains(sink.getNode()))
					crosspointButton.setSelected(true);
				crossPointPanel.add(crosspointButton);
			}
			for (var sink : transceivers) {
				JButton crosspointButton = createCrosspointButton(source, sink);
				if (matrix.matrix.get(source.getNode()) != null
						&& matrix.matrix.get(source.getNode()).contains(sink.getNode()))
					crosspointButton.setSelected(true);
				crossPointPanel.add(crosspointButton);
			}
		}

		for (var source : transceivers) {
			JLabel sourceLabel = new JLabel(source.getNodeName());
			crossPointPanel.add(sourceLabel);
			for (var sink : sinks) {
				JButton crosspointButton = createCrosspointButton(source, sink);
				if (matrix.matrix.get(source.getNode()) != null
						&& matrix.matrix.get(source.getNode()).contains(sink.getNode()))
					crosspointButton.setSelected(true);
				crossPointPanel.add(crosspointButton);
			}
		}

		setSize(500, 500);
		add(crossPointPanel);
		lastCrosspointPanel = crossPointPanel;
		setVisible(true);
	}

	public HashMap<jVidNodeComponent<VideoProcessor>, HashSet<jVidNodeComponent<VideoProcessor>>> getCrosspoints() {
		return crosspoints;
	}

	public JPopupMenu getCrosspointJMenu() {
		return popupMenu;
	}

	private JButton createCrosspointButton(jVidNodeComponent<VideoProcessor> source,
			jVidNodeComponent<VideoProcessor> sink) {
		JButton crosspointButton = new JButton();
		crosspointButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Crosspoint pressed");
				if (crosspointButton.isSelected()) {
					crosspointButton.setSelected(false);
					matrix.breakCrossPoint(source.getNode(), sink.getNode());
					crosspoints.get(source).remove(sink);
				} else {
					crosspointButton.setSelected(true);
					matrix.createCrossPoint(source.getNode(), sink.getNode());
					crosspoints.get(source).add(sink);
				}

			}
		});
		return crosspointButton;
	}

}
