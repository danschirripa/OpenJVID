package com.javashell.openjvid.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.javashell.openjvid.ui.IconManager;

public class IconViewer extends JFrame {

	private static final long serialVersionUID = -6550493554481571856L;

	public IconViewer() {
		super("Icon Preview");
		JPanel iconPanel = new JPanel();
		List<String> iconNames = IconManager.getIconNames();
		JComboBox<Object> iconSelection = new JComboBox<Object>(iconNames.toArray());

		iconPanel.setLayout(new BorderLayout());
		iconPanel.add(iconSelection, BorderLayout.SOUTH);

		JPanel iconDisplay = new JPanel();
		JLabel icon = new JLabel();
		iconDisplay.add(icon);

		iconPanel.add(iconDisplay, BorderLayout.CENTER);

		iconSelection.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				icon.setIcon(IconManager.getSVGIcon(iconSelection.getSelectedItem().toString(), 250, 250));
				revalidate();
			}

		});

		add(iconPanel);
		setSize(300, 300);
		setVisible(true);
	}
}
