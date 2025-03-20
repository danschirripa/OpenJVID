package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;

import com.javashell.video.ControlInterface;
import com.javashell.video.VideoDigestor;

public class LuaDigestor extends VideoDigestor implements ControlInterface {
	private jVidScriptedComponent comp;
	private boolean isOpen = false;

	private HashSet<ControlInterface> subscribers;

	public LuaDigestor() {
		super(new Dimension(1920, 1080));
		subscribers = new HashSet<ControlInterface>();

	}

	public void setScriptedComponent(jVidScriptedComponent comp) {
		this.comp = comp;
	}

	@Override
	public BufferedImage processFrame(BufferedImage frame) {
		if (isOpen)
			return comp.processFrame(frame);
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
	public boolean addSubscriber(ControlInterface cf) {
		subscribers.add(cf);
		return true;
	}

	@Override
	public void processControl(Object obj) {
		comp.processControl(obj);
	}

	@Override
	public void removeControlEgressor(ControlInterface cf) {
		subscribers.remove(cf);
	}

	public void fireControl(Object args) {
		for (var cf : subscribers)
			cf.processControl(args);
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
