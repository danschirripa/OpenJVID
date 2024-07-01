package com.javashell.openjvid;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.javashell.openjvid.handlers.MainFrameActionHandler;

public class MainFrameMenuBar extends JMenuBar {
	private MainFrameActionHandler handler;

	public MainFrameMenuBar(MainFrameActionHandler handler) {
		this.handler = handler;
		createMenuBar();
	}

	private void createMenuBar() {
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu toolsMenu = new JMenu("Tools");

		// File Menu item setup
		JMenuItem fileSave = new JMenuItem("Save");
		JMenuItem fileLoad = new JMenuItem("Load");
		JMenu loadRecent = new JMenu("Recent");
		fileMenu.add(fileSave);
		fileMenu.add(fileLoad);
		fileMenu.add(loadRecent);

		fileSave.setActionCommand(MainFrameActionHandler.FILESAVE);
		fileSave.addActionListener(handler);

		fileLoad.setActionCommand(MainFrameActionHandler.FILELOAD);
		fileLoad.addActionListener(handler);

		// TODO Add recent's menu

		// Edit Menu item setup
		JMenuItem editAddComponent = new JMenuItem("Add");
		JMenuItem editDeleteSelected = new JMenuItem("Delete");
		JMenuItem editSelectedProperties = new JMenuItem("Edit");
		editMenu.add(editAddComponent);
		editMenu.add(editDeleteSelected);
		editMenu.add(editSelectedProperties);

		editAddComponent.setActionCommand(MainFrameActionHandler.ADD);
		editAddComponent.addActionListener(handler);

		editDeleteSelected.setActionCommand(MainFrameActionHandler.DELETE);
		editDeleteSelected.addActionListener(handler);

		editSelectedProperties.setActionCommand(MainFrameActionHandler.EDITPROPS);
		editSelectedProperties.addActionListener(handler);

		// Tools Menu item setup

		// Menu aggregation
		add(fileMenu);
		add(editMenu);
		add(toolsMenu);
	}

}
