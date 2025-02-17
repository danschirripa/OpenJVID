package com.javashell.openjvid.configuration;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class jVidFileFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		if (arg0.getName().toLowerCase().endsWith(".jvid") || arg0.isDirectory())
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return ".jvid";
	}

}
