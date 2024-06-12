package com.javashell.openjvid;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainFrameMenuBar extends JMenuBar {

	public MainFrameMenuBar() {
		createMenuBar();
	}

	private void createMenuBar() {
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu toolsMenu = new JMenu("Tools");
		
		// File Menu item setup
		JMenuItem fileSave = new JMenuItem("Save");
		JMenuItem fileLoad = new JMenuItem("Load");
		JMenu loadRecent = new JMenu("Recent >");
		
		// Edit Menu item setup
		JMenuItem editAddComponent =  new JMenuItem("Add");
		JMenuItem editDeleteSelected = new JMenuItem("Delete");
		JMenuItem editSelectedProperites = new JMenuItem("Edit");
		
		// Tools Menu item setup
	}

}
