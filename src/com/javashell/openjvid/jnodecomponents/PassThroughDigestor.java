package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

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

}
