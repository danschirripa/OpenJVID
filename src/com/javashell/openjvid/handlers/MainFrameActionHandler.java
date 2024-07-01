package com.javashell.openjvid.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.MainFrame;

public class MainFrameActionHandler implements MouseListener, MouseMotionListener, ActionListener {

	public static final String ADD = "ADD", DELETE = "DELETE", EDITPROPS = "EDIT_PROPS", FILESAVE = "FILE_SAVE,",
			FILELOAD = "FILE_LOAD";

	private final JNodeFlowPane flowPane;
	private final MainFrame frame;

	public MainFrameActionHandler(JNodeFlowPane flowPane, MainFrame frame) {
		this.flowPane = flowPane;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case ADD:
			frame.createAndShowAddComponentDialog();
			break;
		case DELETE:
			break;
		case EDITPROPS:
			break;
		case FILESAVE:
			break;
		case FILELOAD:
			break;
		}
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
