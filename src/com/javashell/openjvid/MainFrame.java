package com.javashell.openjvid;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.radiance.theming.api.RadianceThemingCortex;
import org.pushingpixels.radiance.theming.api.skin.GraphiteSiennaSkin;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.configuration.jVidConfigurationParser;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.ui.AddComponentDialog;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -4865451275996292868L;
	private JNodeFlowPane flowPane;
	private MainFrameMenuBar menuBar;
	private MainFrameActionHandler handler;
	private final boolean isEmulated;

	public MainFrame(boolean isEmulated) throws InvocationTargetException, InterruptedException {
		this.isEmulated = isEmulated;
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				RadianceThemingCortex.GlobalScope.setSkin(new GraphiteSiennaSkin());
				createFrame();
				setSize(500, 500);
				setVisible(true);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});

	}

	private void createFrame() {
		flowPane = new JNodeFlowPane();
		handler = new MainFrameActionHandler(flowPane, this);

		setContentPane(flowPane);
		setName("OpenjVid");
		setTitle("OpenjVid");

		menuBar = new MainFrameMenuBar(handler, flowPane);
		setJMenuBar(menuBar);
	}

	public void createAndShowAddComponentDialog() {
		new AddComponentDialog(this, flowPane, isEmulated);
	}

	public void loadConfiguration(File configuration) {
		jVidConfigurationParser.loadConfiguration(flowPane, configuration);
	}
}
