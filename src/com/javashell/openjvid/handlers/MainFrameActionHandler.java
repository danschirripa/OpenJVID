package com.javashell.openjvid.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.javashell.flow.FlowController;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.MainFrame;
import com.javashell.openjvid.configuration.jVidConfigurationParser;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.DigestNodeFactory;
import com.javashell.openjvid.peripheral.PeripheralDescriptor;
import com.javashell.openjvid.peripheral.PeripheralDiscoveryService;
import com.javashell.openjvid.ui.UploadConfigurationDialog;
import com.javashell.video.VideoProcessor;

public class MainFrameActionHandler implements MouseListener, MouseMotionListener, ActionListener {

	public static final String ADD = "ADD", DELETE = "DELETE", EDITPROPS = "EDIT_PROPS", FILESAVE = "FILE_SAVE,",
			FILELOAD = "FILE_LOAD", UPLOAD = "UPLOAD", DOWNLOAD = "DOWNLOAD", DEBUG = "DEBUG";

	private final JNodeFlowPane flowPane;
	private final MainFrame frame;

	public MainFrameActionHandler(JNodeFlowPane flowPane, MainFrame frame) {
		this.flowPane = flowPane;
		this.frame = frame;
		PeripheralDiscoveryService.setMainFrameActionHandler(this, flowPane);
	}

	public void addOpenJVIDPeripheral(PeripheralDescriptor pd, UUID sessionID, int port) {
		jVidNodeComponent<VideoProcessor> periph = DigestNodeFactory.createOpenJVIDPeripheral(pd, sessionID, port,
				flowPane);

		if (periph != null) {
			FlowController.registerUncheckedFlowNode(periph.getNode());
			flowPane.add(periph);
		} else {
			System.err.println("Received null reference when adding peripheral component");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case ADD:
			frame.createAndShowAddComponentDialog();
			break;
		case DELETE:
			((jVidNodeComponent<VideoProcessor>) flowPane.getSelection()).getNode().retrieveNodeContents().close();
			flowPane.remove(flowPane.getSelection());
			break;
		case EDITPROPS:
			break;
		case FILESAVE:
			JFileChooser selectionDialog = new JFileChooser();
			if (selectionDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				jVidConfigurationParser.saveConfiguration(flowPane, selectionDialog.getSelectedFile());
			}
			break;
		case FILELOAD:
			selectionDialog = new JFileChooser();
			if (selectionDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				jVidConfigurationParser.loadConfiguration(flowPane, selectionDialog.getSelectedFile());
			}
			break;
		case UPLOAD:
			UploadConfigurationDialog dia = new UploadConfigurationDialog();
			dia.setVisible(true);
		case DEBUG:
			JMenuItem debugOption = (JMenuItem) e.getSource();
			flowPane.setDebugLinkages(debugOption.isSelected());
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
