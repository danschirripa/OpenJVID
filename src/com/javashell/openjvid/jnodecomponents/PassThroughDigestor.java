package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.javashell.video.VideoDigestor;

public class PassThroughDigestor extends VideoDigestor {

	public PassThroughDigestor() {
		super(new Dimension());
	}

	@Override
	public BufferedImage processFrame(BufferedImage frame) {
		return frame;
	}

	@Override
	public boolean open() {
		return true;
	}

	@Override
	public boolean close() {
		return true;
	}

	@Override
	public HashMap<String, Object> getPropertyTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPropertyTable(HashMap<String, Object> table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
