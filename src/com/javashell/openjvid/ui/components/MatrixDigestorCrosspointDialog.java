package com.javashell.openjvid.ui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		JPanel crossPointPanel = new JPanel();
		crossPointPanel.setLayout(layout);

		for (var sink : sinks) {
			JLabel sinkLabel = new JLabel(sink.getNodeName());
			sinkLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			System.out.println("Generated sink label");
			crossPointPanel.add(sinkLabel, c);
			c.gridx++;
		}
		c.gridx = 0;
		c.gridy = 1;

		for (var source : sources) {
			JLabel sourceLabel = new JLabel(source.getNodeName());
			crossPointPanel.add(sourceLabel, c);
			c.gridx++;
			for (var sink : sinks) {
				JButton crosspointButton = createCrosspointButton(source, sink);
				if (matrix.matrix.get(source.getNode()) != null
						&& matrix.matrix.get(source.getNode()).contains(sink.getNode()))
					crosspointButton.setSelected(true);
				crossPointPanel.add(crosspointButton, c);
				c.gridx++;
			}
			for (var sink : transceivers) {
				JButton crosspointButton = createCrosspointButton(source, sink);
				if (matrix.matrix.get(source.getNode()) != null
						&& matrix.matrix.get(source.getNode()).contains(sink.getNode()))
					crosspointButton.setSelected(true);
				crossPointPanel.add(crosspointButton, c);
				c.gridx++;
			}
			c.gridy++;
			c.gridx = 0;
		}

		for (var source : transceivers) {
			JLabel sourceLabel = new JLabel(source.getNodeName());
			crossPointPanel.add(sourceLabel, c);
			c.gridx++;
			for (var sink : sinks) {
				JButton crosspointButton = createCrosspointButton(source, sink);
				if (matrix.matrix.get(source.getNode()) != null
						&& matrix.matrix.get(source.getNode()).contains(sink.getNode()))
					crosspointButton.setSelected(true);
				crossPointPanel.add(crosspointButton, c);
				c.gridx++;
			}
		}

		setSize(500, 500);
		crossPointPanel.setSize(500, 500);
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
				if (crosspointButton.isSelected()) {
					crosspointButton.setSelected(false);
					matrix.breakCrossPoint(source.getNode(), sink.getNode());
					crosspoints.get(source).remove(sink);
					System.out.println("Removed crosspoint " + source.getNodeName() + " : " + sink.getNodeName());
				} else {
					crosspointButton.setSelected(true);
					matrix.createCrossPoint(source.getNode(), sink.getNode());
					crosspoints.get(source).add(sink);
					System.out.println("Added crosspoint " + source.getNodeName() + " : " + sink.getNodeName());
				}

			}
		});
		return crosspointButton;
	}

}
