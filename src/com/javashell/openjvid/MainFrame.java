package com.javashell.openjvid;

import javax.swing.JFrame;

import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.ui.AddComponentDialog;

public class MainFrame extends JFrame{
	private static final long serialVersionUID = -4865451275996292868L;
	private JNodeFlowPane flowPane;
	private MainFrameMenuBar menuBar;
	private MainFrameActionHandler handler;
	
	public MainFrame() {
		createFrame();
		setSize(500, 500);
		setVisible(true);
	}
	
	private void createFrame() {
		flowPane = new JNodeFlowPane();
		setContentPane(flowPane);
		setName("OpenjVid");
		setTitle("OpenjVid");
		
		menuBar = new MainFrameMenuBar();
		setJMenuBar(menuBar);
		
		handler = new MainFrameActionHandler(flowPane);
	}

	public void createAndShowAddComponentDialog() {
		new AddComponentDialog(this,flowPane);
	}
}
