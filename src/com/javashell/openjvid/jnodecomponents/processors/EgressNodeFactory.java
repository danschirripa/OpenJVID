package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;

import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.video.VideoProcessor;
import com.javashell.video.egressors.LocalWindowEgressor;
import com.javashell.video.egressors.NDI5Egressor;
import com.javashell.video.egressors.QOYStreamEgressor;

public class EgressNodeFactory {

	@TypeName(typeName = "Preview Frame")
	public static jVidNodeComponent<VideoProcessor> createPreviewFrameEgress(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		LocalWindowEgressor prev = new LocalWindowEgressor(resolution);
		FlowNode<VideoProcessor> previewNode = new VideoFlowNode(prev, null, null);
		jVidNodeComponent<VideoProcessor> prevNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, previewNode);
		prevNodeComp.setNodeType(NodeType.Receiver);

		prevNodeComp.setNodeName("Preview Frame");

		prev.open();

		return prevNodeComp;
	}

	@TypeName(typeName = "NDI Egress")
	public static jVidNodeComponent<VideoProcessor> createNDIEgress(@Label(label = "Resolution") Dimension resolution,
			@Label(label = "NDI Name") String name, JNodeFlowPane flowPane) {
		NDI5Egressor ndi = new NDI5Egressor(resolution, name);
		FlowNode<VideoProcessor> ndiNode = new VideoFlowNode(ndi, null, null);
		jVidNodeComponent<VideoProcessor> ndiNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ndiNode);
		ndiNodeComp.setNodeType(NodeType.Receiver);

		ndiNodeComp.setNodeName(name);

		ndi.open();

		return ndiNodeComp;
	}

	@TypeName(typeName = "QOYV Egress")
	public static jVidNodeComponent<VideoProcessor> createQOYVEgress(@Label(label = "Resolution") Dimension resolution,
			@Label(label = "Key Frame Interval") int keyFrameInterval, JNodeFlowPane flowPane) {
		QOYStreamEgressor qoyv = new QOYStreamEgressor(resolution, keyFrameInterval);
		FlowNode<VideoProcessor> qoyvNode = new VideoFlowNode(qoyv, null, null);
		jVidNodeComponent<VideoProcessor> qoyvNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, qoyvNode);
		qoyvNodeComp.setNodeType(NodeType.Receiver);

		qoyvNodeComp.setNodeName("QOYV Egress");

		qoyv.open();

		return qoyvNodeComp;
	}
}
