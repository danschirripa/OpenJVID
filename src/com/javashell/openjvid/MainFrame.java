package com.javashell.openjvid;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.radiance.theming.api.RadianceThemingCortex;
import org.pushingpixels.radiance.theming.api.skin.GraphiteSiennaSkin;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.configuration.jVidConfigurationParser;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.rest.RestfulWebServer;
import com.javashell.openjvid.ui.components.AddComponentDialog;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -4865451275996292868L;
	private JNodeFlowPane flowPane;
	private MainFrameMenuBar menuBar;
	private MainFrameActionHandler handler;
	private RestfulWebServer rws;
	private final boolean isEmulated;
	private Hashtable<UUID, jVidNodeComponent<?>> nodeComponents;

	public MainFrame(boolean isEmulated) throws InvocationTargetException, InterruptedException {
		this.isEmulated = isEmulated;
		nodeComponents = new Hashtable<UUID, jVidNodeComponent<?>>();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				RadianceThemingCortex.GlobalScope.setSkin(new GraphiteSiennaSkin());
				createFrame();
				setSize(500, 500);
				setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
				setVisible(true);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});

	}

	private void createFrame() {
		flowPane = new JNodeFlowPane() {
			@Override
			public Component add(Component c) {
				super.add(c);
				if (c instanceof jVidNodeComponent) {
					var node = (jVidNodeComponent<?>) c;
					nodeComponents.put(node.getUUID(), node);
				}
				return c;
			}

			@Override
			public void remove(Component c) {
				super.remove(c);
				if (c instanceof jVidNodeComponent) {
					var node = (jVidNodeComponent<?>) c;
					nodeComponents.remove(node.getUUID());
				}
			}
		};
		handler = new MainFrameActionHandler(flowPane, this);
		rws = new RestfulWebServer(flowPane, this, handler);

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

	public Hashtable<UUID, jVidNodeComponent<?>> getNodeComponents() {
		return nodeComponents;
	}

	protected JNodeFlowPane getFlowPane() {
		return flowPane;
	}

}
