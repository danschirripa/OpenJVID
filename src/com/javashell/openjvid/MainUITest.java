package com.javashell.openjvid;

import com.javashell.flow.FlowController;

public class MainUITest {
	public static void main(String[] args) throws Exception {
		MainFrame mf = new MainFrame();
		
		FlowController.startFlowControl();

		try {
			while (mf.isVisible()) {
				Thread.sleep(33);
				mf.repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
