package com.javashell.openjvid;

import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.javashell.openjvid.ui.IconManager;

public class LuaEditorTest {

	public static void main(String[] args) {
		JFrame iconViewer = new JFrame("Icons");
		JPanel iconPanel = new JPanel();
		HashSet<String> iconNames = IconManager.getIconNames();
		JComboBox<Object> iconSelection = new JComboBox<Object>(iconNames.toArray());
	}

}
