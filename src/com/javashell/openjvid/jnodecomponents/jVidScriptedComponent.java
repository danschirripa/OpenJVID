package com.javashell.openjvid.jnodecomponents;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.hk.lua.Lua;
import com.hk.lua.LuaFactory;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.lua.LuaManager;
import com.javashell.openjvid.lua.swing.LuaComponent;
import com.javashell.openjvid.lua.utilities.LuajVidNodeComponent;
import com.javashell.openjvid.ui.IconManager;
import com.javashell.openjvid.ui.components.LuaCodeEditorFrame;
import com.javashell.video.VideoProcessor;

public class jVidScriptedComponent extends jVidNodeComponent<VideoProcessor> {
	private static final long serialVersionUID = -2151504432881312600L;

	private String script = "";
	private boolean isEditing = false;

	private LuajVidNodeComponent luaComp;

	private LuaObject processCallback = null, controlCallback = null;
	private LuaInterpreter interp;

	public jVidScriptedComponent(JNodeFlowPane flow) {
		super(flow, new LuaFlowNode());
		((LuaFlowNode) this.getNode()).setScriptedComponent(this);
		this.getNode().retrieveNodeContents().open();
		setComponentPopupMenu(generatePopupMenu());
		startScript();
		setIcon(IconManager.getSVGIcon("terminal.svg", 100, 100).getImage());
	}

	public jVidScriptedComponent(String script, JNodeFlowPane flow) {
		super(flow, new LuaFlowNode());
		((LuaFlowNode) this.getNode()).setScriptedComponent(this);
		this.getNode().retrieveNodeContents().open();
		setComponentPopupMenu(generatePopupMenu());
		this.script = script;
		startScript();
		setIcon(IconManager.getSVGIcon("terminal", 100, 100).getImage());
	}

	public BufferedImage processFrame(BufferedImage frame) {

		if (processCallback != null) {
			var gw = new LuaComponent.LuaGraphicsWrapper(frame.getGraphics());
			processCallback.call(interp, gw);
		} else {
			System.out.println("Callback still null");
		}

		return frame;
	}

	public void processControl(Object obj) {
		if (controlCallback != null) {
			controlCallback.call(interp, null);
		}
	}

	public void fireControl(Object args) {
		((LuaDigestor) getNode().retrieveNodeContents()).fireControl(args);
	}

	private JPopupMenu generatePopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem editMenuItem = new JMenuItem("Edit");

		editMenuItem.addActionListener(listener -> {
			if (isEditing == false) {
				isEditing = true;
				var lcem = new LuaCodeEditorFrame(script, getName());
				lcem.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				lcem.addWindowListener(new WindowListener() {

					@Override
					public void windowActivated(WindowEvent arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void windowClosed(WindowEvent arg0) {
						isEditing = false;
						script = lcem.getScriptText();
						startScript();
						getNodeComponentDescriptor().setInitArgs(new Object[] { script, getNodeName() });
					}

					@Override
					public void windowClosing(WindowEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void windowDeactivated(WindowEvent arg0) {
					}

					@Override
					public void windowDeiconified(WindowEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void windowIconified(WindowEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void windowOpened(WindowEvent arg0) {
						// TODO Auto-generated method stub

					}

				});
			}
		});

		popup.add(editMenuItem);
		return popup;

	}

	public void setProcessFrameLuaCallback(LuaObject func) {
		System.out.println("set callback");
		processCallback = func;
	}

	public void setControlCallback(LuaObject func) {
		controlCallback = func;
	}

	private void startScript() {
		try {
			LuaFactory factory = Lua.factory(script);
			Lua.importStandard(factory);
			LuaManager.injectAllHooks(factory);
			factory.compile();
			interp = factory.build();
			luaComp = new LuajVidNodeComponent(interp, this);
			interp.execute(luaComp);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

}
