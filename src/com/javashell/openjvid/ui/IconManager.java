package com.javashell.openjvid.ui;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.ImageIcon;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

public final class IconManager {
	private final static HashSet<String> iconNames;
	static {
		// Load all available icon names
		iconNames = new HashSet<String>();
		InputStream fileDbStream = IconManager.class.getResourceAsStream("/assets/icons/default_pack/icon.lst");
		Scanner sc = new Scanner(fileDbStream);
		while (sc.hasNextLine()) {
			String nextFileName = sc.nextLine();
			iconNames.add(nextFileName);
		}
		sc.close();
	}

	public static HashSet<String> getIconNames() {
		return iconNames;
	}

	public static ImageIcon getSVGIcon(String iconName, int width, int height) {
		try {
			BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
			transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
			transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
			TranscoderInput input = new TranscoderInput();
			final InputStream iconInputStream = IconManager.class
					.getResourceAsStream("/assets/icons/default_pack/" + iconName + ".svg");
			input.setInputStream(iconInputStream);
			transcoder.transcode(input, null);
			ImageIcon icon = new ImageIcon(transcoder.getFinalImage());
			return icon;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static class BufferedImageTranscoder extends ImageTranscoder {
		private BufferedImage img = null;

		@Override
		public BufferedImage createImage(int arg0, int arg1) {
			BufferedImage img = new BufferedImage(arg0, arg1, BufferedImage.TYPE_INT_ARGB);
			return img;
		}

		@Override
		public void writeImage(BufferedImage arg0, TranscoderOutput arg1) throws TranscoderException {
			this.img = arg0;
		}

		public BufferedImage getFinalImage() {
			return img;
		}

	}

}
