package com.javashell.openjvid;

import com.javashell.flow.FlowController;
import com.javashell.openjvid.peripheral.PeripheralDiscoveryService;

public class MainUITest {
	public static void main(String[] args) throws Exception {
		MainFrame mf = new MainFrame();
		
		FlowController.startFlowControl();
		
		PeripheralDiscoveryService.initializeService();

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
