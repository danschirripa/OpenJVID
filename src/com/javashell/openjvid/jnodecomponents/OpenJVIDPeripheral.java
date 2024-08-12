package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.InetAddress;

import com.javashell.video.VideoProcessor;

public class OpenJVIDPeripheral implements VideoProcessor{
	
	public OpenJVIDPeripheral(InetAddress node) {
		
	}

	@Override
	public BufferedImage processFrame(BufferedImage frame) {
		return null;
	}

	@Override
	public boolean open() {
		return false;
	}

	@Override
	public boolean close() {
		return false;
	}

	@Override
	public Dimension getResolution() {
		return null;
	}

}
