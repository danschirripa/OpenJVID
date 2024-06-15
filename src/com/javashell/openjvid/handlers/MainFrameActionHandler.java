package com.javashell.openjvid.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import com.javashell.jnodegraph.JNodeFlowPane;

public class MainFrameActionHandler implements MouseListener, MouseMotionListener, ActionListener {

	private final JNodeFlowPane flowPane;

	public MainFrameActionHandler(JNodeFlowPane flowPane) {
		this.flowPane = flowPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {

		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
