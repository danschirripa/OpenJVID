package com.javashell.openjvid.ui.components;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ThrobberPopup extends JFrame {
	private static final long serialVersionUID = -1071185878357958436L;
	private Throbber throb;

	public ThrobberPopup(int width, int height, int numCircles, Color color, float frameRate) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				throb = new Throbber(width, height, numCircles, color, frameRate);

				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				setUndecorated(true);

				setLayout(null);
				setShape(new Ellipse2D.Double(0, 0, width, height));
				throb.setBounds(0, 0, width, height);

				add(throb);
				throb.startPaint();

				setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().x - width,
						GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().y - height);
				setSize(width, height);
				setVisible(true);
			}
		});

	}

}
