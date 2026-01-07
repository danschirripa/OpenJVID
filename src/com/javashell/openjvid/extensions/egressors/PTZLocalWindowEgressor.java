package com.javashell.openjvid.extensions.egressors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.javashell.control.protocols.VISCA;
import com.javashell.openjvid.ui.IconManager;
import com.javashell.video.ControlInterface;
import com.javashell.video.VideoEgress;
import com.javashell.video.camera.PTZControlInterface;
import com.javashell.video.camera.PTZMove;

public class PTZLocalWindowEgressor extends VideoEgress implements ControlInterface {
	private JFrame egressFrame;
	private BufferedImage curFrame;
	private JPanel egressPanel, controlPane;
	private PTZControlInterface selectedInterface = null;

	public PTZLocalWindowEgressor(Dimension resolution) {
		this(resolution, true);
	}

	public PTZLocalWindowEgressor(Dimension resolution, boolean doScale) {
		super(resolution);

		egressFrame = new JFrame("Local Window Egressor") {
			@Override
			public void update(Graphics g) {
				paint(g);
			}
		};
		egressFrame.setSize(resolution);
		egressFrame.setTitle("Local Window Egressor");

		curFrame = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_ARGB);

		egressPanel = new JPanel() {

			@Override
			public void paintComponent(Graphics g) {
				int width = resolution.width, height = resolution.height;
				if (doScale) {
					int ratio = egressFrame.getWidth() / width;
					width = egressFrame.getWidth();
					height = height * ratio;
				}
				g.drawImage(curFrame, 0, 0, width, height, egressFrame);
			}
		};
		egressPanel.setSize(resolution);
		egressPanel.setPreferredSize(resolution);

		egressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		egressFrame.add(egressPanel);
		createControlFrame();
		egressFrame.setContentPane(egressPanel);
		egressFrame.add(controlPane);
	}

	@Override
	public BufferedImage processFrame(BufferedImage frame) {
		curFrame.getGraphics().drawImage(frame, 0, 0, egressFrame);
		if (egressFrame.isVisible()) {
			try {
				egressFrame.repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return frame;
	}

	@Override
	public boolean open() {
		egressFrame.setVisible(true);
		return true;
	}

	@Override
	public boolean close() {
		egressFrame.setVisible(false);
		return true;
	}

	private void createControlFrame() {
		controlPane = new JPanel();
		JButton up = new JButton();
		up.setIcon(IconManager.getSVGIcon("Arrow_drop_up.svg", 40, 40));
		up.setOpaque(false);
		up.setBackground(new Color(0, 0, 0, 0));
		up.setContentAreaFilled(false);
		up.setBorderPainted(false);
		up.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(1, 0, 0));
				up.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 0));
				up.setContentAreaFilled(false);
			}

		});
		JButton left = new JButton();
		left.setIcon(IconManager.getSVGIcon("Arrow_drop_left.svg", 40, 40));
		left.setOpaque(false);
		left.setBackground(new Color(0, 0, 0, 0));
		left.setContentAreaFilled(false);
		left.setBorderPainted(false);
		left.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, -1, 0));
				left.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 0));
				left.setContentAreaFilled(false);
			}

		});
		JButton right = new JButton();
		right.setIcon(IconManager.getSVGIcon("Arrow_drop_right.svg", 40, 40));
		right.setOpaque(false);
		right.setBackground(new Color(0, 0, 0, 0));
		right.setContentAreaFilled(false);
		right.setBorderPainted(false);
		right.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 1, 0));
				right.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 0));
				right.setContentAreaFilled(false);
			}

		});
		JButton down = new JButton();
		down.setIcon(IconManager.getSVGIcon("Arrow_drop_down_big.svg", 40, 40));
		down.setOpaque(false);
		down.setBackground(new Color(0, 0, 0, 0));
		down.setContentAreaFilled(false);
		down.setBorderPainted(false);
		down.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(-1, 0, 0));
				down.setContentAreaFilled(true);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 0));
				down.setContentAreaFilled(false);
			}

		});
		JButton in = new JButton("In");
		in.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 1));
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 0));
			}

		});
		JButton out = new JButton("Out");
		out.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, -1));
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				selectedInterface.processControl(new PTZMove(0, 0, 0));
			}

		});
		JButton home = new JButton("Home");
		home.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectedInterface.processControl(VISCA.HOME);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		controlPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;

		c.gridx = 0;
		c.gridy = 1;

		controlPane.add(left, c);

		c.gridx = 1;
		c.gridy = 0;

		controlPane.add(up, c);

		c.gridx = 2;
		c.gridy = 1;

		controlPane.add(right, c);

		c.gridx = 1;
		c.gridy = 2;

		controlPane.add(down, c);

		c.gridx = 4;

		controlPane.add(Box.createHorizontalStrut(10), c);

		c.gridx = 6;

		controlPane.add(Box.createHorizontalStrut(10), c);

		c.gridx = 7;
		c.gridy = 2;

		controlPane.add(home, c);

		c.gridx = 5;
		c.gridy = 0;

		controlPane.add(in, c);

		c.gridy = 2;

		controlPane.add(out, c);

		controlPane.setSize(150, 150);
		controlPane.setOpaque(false);
	}

	@Override
	public boolean addSubscriber(ControlInterface cf) {
		if (cf instanceof PTZControlInterface) {
			selectedInterface = (PTZControlInterface) cf;
			return true;
		}
		return false;
	}

	@Override
	public void processControl(Object obj) {
	}

	@Override
	public void removeControlEgressor(ControlInterface cf) {
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
