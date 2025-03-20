package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.javashell.video.VideoDigestor;

public class ScriptedDigestor extends VideoDigestor {
	private final jVidScriptedComponent script;
	private boolean isOpen = false;

	public ScriptedDigestor(jVidScriptedComponent script) {
		super(new Dimension());
		this.script = script;
	}

	@Override
	public BufferedImage processFrame(BufferedImage frame) {
		if (isOpen)
			return script.processFrame(frame);
		else
			return frame;
	}

	@Override
	public boolean open() {
		isOpen = true;
		return true;
	}

	@Override
	public boolean close() {
		isOpen = false;
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
