package com.javashell.openjvid;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import javax.swing.JFrame;

import com.javashell.flow.FlowController;
import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.video.VideoProcessor;
import com.javashell.video.camera.Camera;
import com.javashell.video.egressors.LocalWindowEgressor;
import com.javashell.video.ingestors.NDI5Ingestor;
import com.javashell.video.ingestors.QOYStreamIngestor;

public class MainTest {
	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException, URISyntaxException, InterruptedException {
		JFrame testFrame = new JFrame("Test");
		JNodeFlowPane flowPane = new JNodeFlowPane();
		testFrame.setContentPane(flowPane);

		String ndiName = "";
		for (String arg : args) {
			ndiName += arg + " ";
		}
		ndiName = ndiName.stripTrailing();
		Dimension resolution = new Dimension(1280, 720);
		NDI5Ingestor ingest = new NDI5Ingestor(resolution, ndiName);

		Camera IP2M_841 = Camera.getCamera("IP2M-841");
		QOYStreamIngestor qoyv = new QOYStreamIngestor(resolution, "192.168.0.48", 4500, false);

		LocalWindowEgressor preview = new LocalWindowEgressor(resolution, false);

		FlowNode<VideoProcessor> ingressNode = new VideoFlowNode(ingest, null, null);
		FlowNode<VideoProcessor> previewNode = new VideoFlowNode(preview, null, null);
		FlowNode<VideoProcessor> amcNode = new VideoFlowNode(qoyv, null, null);

		jVidNodeComponent<VideoProcessor> test1 = new jVidNodeComponent<VideoProcessor>(flowPane, ingressNode);
		jVidNodeComponent<VideoProcessor> test2 = new jVidNodeComponent<VideoProcessor>(flowPane, previewNode);
		jVidNodeComponent<VideoProcessor> test3 = new jVidNodeComponent<VideoProcessor>(flowPane, amcNode);

		test1.setNodeType(NodeType.Transmitter);
		test2.setNodeType(NodeType.Receiver);
		test3.setNodeType(NodeType.Transmitter);

		test1.setNodeName("NDI");
		test2.setNodeName("Local Preview");
		test3.setNodeName("QOYV");

		test1.setLocation(10, 50);
		test2.setLocation(400, 25);
		test3.setLocation(300, 79);

		flowPane.add(test1);
		flowPane.add(test2);
		flowPane.add(test3);

		testFrame.setSize(500, 500);
		testFrame.setVisible(true);

		FlowController.registerFlowNode(ingressNode);
		FlowController.registerFlowNode(amcNode);

		ingest.open();
		//qoyv.open();
		preview.open();

		FlowController.startFlowControl();

		try {
			while (testFrame.isVisible()) {
				Thread.sleep(33);
				testFrame.repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
