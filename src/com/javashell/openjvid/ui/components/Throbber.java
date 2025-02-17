package com.javashell.openjvid.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

public class Throbber extends JComponent {
	private static final long serialVersionUID = 3283744974725472848L;
	private int width, height, numCircles, angularDelta, iteration = 0, circularRadius, alphaDelta;
	private Color color;
	private Point midpoint;
	private int frameDelayMs;
	private boolean doPaint;

	public Throbber(int width, int height, int numCircles, Color renderColor, float frameRate) {
		this.width = width;
		this.height = height;
		this.numCircles = numCircles;
		this.angularDelta = 360 / numCircles;
		this.color = renderColor;
		this.midpoint = new Point(width / 2, height / 2);
		this.circularRadius = (width / 5);
		this.alphaDelta = 255 / numCircles;
		this.frameDelayMs = (int) (frameRate * 1000);
		System.out.println(frameDelayMs + " " + frameRate);
	}

	public void startPaint() {
		doPaint = true;
		repaint();
	}

	public void stopPaint() {
		doPaint = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		int angle = iteration * angularDelta;
		int opacity = 255;
		// g.drawOval(midpoint.x, midpoint.y, 10, 10);

		for (int i = 0; i < numCircles; i++) {
			int cX = (int) (midpoint.x + (((width / 2) - (circularRadius)) * Math.cos(Math.toRadians(angle))));
			int cY = (int) (midpoint.y + (((height / 2) - (circularRadius)) * Math.sin(Math.toRadians(angle))));
			g.setColor(new Color(color.getRed(), color.getBlue(), color.getGreen(), opacity));
			g.fillOval(cX - (circularRadius / 2), cY - (circularRadius / 2), circularRadius, circularRadius);
			angle = angle + angularDelta;
			opacity = opacity - alphaDelta;
		}

		iteration++;
		if (iteration == numCircles)
			iteration = 0;
		try {
			if (doPaint && isVisible()) {
				Thread.sleep(frameDelayMs);
				repaint();
			}
			if (!isVisible())
				doPaint = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
