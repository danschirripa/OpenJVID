package com.javashell.openjvid;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.radiance.theming.api.RadianceThemingCortex;
import org.pushingpixels.radiance.theming.api.skin.GraphiteSiennaSkin;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.ui.AddComponentDialog;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -4865451275996292868L;
	private JNodeFlowPane flowPane;
	private MainFrameMenuBar menuBar;
	private MainFrameActionHandler handler;

	public MainFrame() throws InvocationTargetException, InterruptedException {

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				RadianceThemingCortex.GlobalScope.setSkin(new GraphiteSiennaSkin());
				createFrame();
				setSize(500, 500);
				setVisible(true);
			}
		});

	}

	private void createFrame() {
		flowPane = new JNodeFlowPane();
		handler = new MainFrameActionHandler(flowPane, this);

		setContentPane(flowPane);
		setName("OpenjVid");
		setTitle("OpenjVid");

		menuBar = new MainFrameMenuBar(handler);
		setJMenuBar(menuBar);
	}

	public void createAndShowAddComponentDialog() {
		new AddComponentDialog(this, flowPane);
	}
}
