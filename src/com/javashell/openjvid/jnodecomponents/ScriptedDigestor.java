package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

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

}
