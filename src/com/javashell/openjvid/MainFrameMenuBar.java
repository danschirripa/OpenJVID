package com.javashell.openjvid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.javashell.flow.FlowController;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.configuration.jVidConfigurationParser;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.ui.components.IconViewer;

public class MainFrameMenuBar extends JMenuBar {
	private MainFrameActionHandler handler;
	private JMenu loadRecent = new JMenu("Recent");
	private LinkedList<String> recentPaths = new LinkedList<String>();
	private JNodeFlowPane flowPane;

	public MainFrameMenuBar(MainFrameActionHandler handler, JNodeFlowPane flowPane) {
		this.handler = handler;
		this.flowPane = flowPane;
		createMenuBar();
		loadRecents();
	}

	private void createMenuBar() {
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu toolsMenu = new JMenu("Tools");

		JMenuItem frameRate = new JMenuItem();
		Thread frameRatePainter = new Thread(new Runnable() {
			public void run() {
				while (flowPane.isVisible()) {
					try {
						Thread.sleep(250);
					} catch (Exception e) {
						frameRate.setText("[n/a fps]");
						return;
					}
					frameRate.setText(FlowController.getAverageInternalFrameRate() + " fps    |    "
							+ FlowController.getAverageFrameDelta() + "ns delta");
				}
			}
		});
		frameRatePainter.start();

		frameRate.setText("[n/a fps]");
		frameRate.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		frameRate.setBorderPainted(true);

		// File Menu item setup
		JMenuItem fileSave = new JMenuItem("Save");
		JMenuItem fileLoad = new JMenuItem("Load");
		fileMenu.add(fileSave);
		fileMenu.add(fileLoad);
		fileMenu.add(loadRecent);

		fileSave.setActionCommand(MainFrameActionHandler.FILESAVE);
		fileSave.addActionListener(handler);

		fileLoad.setActionCommand(MainFrameActionHandler.FILELOAD);
		fileLoad.addActionListener(handler);

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
		JMenuItem toolUpload = new JMenuItem("Upload");
		JMenuItem toolDownload = new JMenuItem("Download");
		JMenuItem toolIconPreview = new JMenuItem("Icons");

		JCheckBoxMenuItem toolDebugLinks = new JCheckBoxMenuItem("Link Debugger");

		toolsMenu.add(toolUpload);
		toolsMenu.add(toolDownload);
		toolsMenu.add(toolIconPreview);
		toolsMenu.addSeparator();
		toolsMenu.add(toolDebugLinks);

		toolUpload.setActionCommand(MainFrameActionHandler.UPLOAD);
		toolUpload.addActionListener(handler);

		toolDownload.setActionCommand(MainFrameActionHandler.DOWNLOAD);
		toolDownload.addActionListener(handler);

		toolIconPreview.addActionListener((e) -> {
			new IconViewer();
		});

		toolDebugLinks.setActionCommand(MainFrameActionHandler.DEBUG);
		toolDebugLinks.addActionListener(handler);

		// Menu aggregation
		add(fileMenu);
		add(editMenu);
		add(toolsMenu);
		add(Box.createHorizontalGlue());
		add(frameRate);
	}

	private void loadRecents() {
		File recentFile = new File(System.getProperty("user.home"), ".jvid");
		try {
			if (!recentFile.exists())
				recentFile.createNewFile();
			FileInputStream fin = new FileInputStream(recentFile);
			Scanner sc = new Scanner(fin);
			while (sc.hasNextLine()) {
				final File file = new File(sc.nextLine());
				if (file.exists()) {
					recentPaths.add(file.getAbsolutePath());
					final JMenuItem loadItem = new JMenuItem(file.getName());
					loadItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jVidConfigurationParser.loadConfiguration(flowPane, file);
						}
					});
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
