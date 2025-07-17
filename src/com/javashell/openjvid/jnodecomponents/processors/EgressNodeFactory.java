package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;

import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.openjvid.configuration.jVidNodeComponentDescriptor;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.video.VideoProcessor;
import com.javashell.video.egressors.LocalWindowEgressor;
import com.javashell.video.egressors.NDI5Egressor;
import com.javashell.video.egressors.PTZLocalWindowEgressor;
import com.javashell.video.egressors.QOYStreamEgressor_V2;
import com.javashell.video.egressors.RawAudioEgressor;
import com.javashell.video.egressors.experimental.FFMPEGStreamEgressor;

public class EgressNodeFactory {

	@TypeName(typeName = "Preview Frame", nodeType = NodeType.Receiver)
	public static jVidNodeComponent<VideoProcessor> createPreviewFrameEgress(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		LocalWindowEgressor prev = new LocalWindowEgressor(resolution);
		FlowNode<VideoProcessor> previewNode = new VideoFlowNode(prev, null, null);
		jVidNodeComponent<VideoProcessor> prevNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, previewNode);
		prevNodeComp.setNodeType(NodeType.Receiver);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Preview Frame", resolution);

		prevNodeComp.setNodeName("Preview Frame");
		prevNodeComp.setNodeComponentDescriptor(desc);

		prev.open();

		return prevNodeComp;
	}

	@TypeName(typeName = "PTZ Preview Frame", nodeType = NodeType.Receiver)
	public static jVidNodeComponent<VideoProcessor> createPTZPreviewFrameEgress(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		PTZLocalWindowEgressor prev = new PTZLocalWindowEgressor(resolution);
		FlowNode<VideoProcessor> previewNode = new VideoFlowNode(prev, null, null);
		jVidNodeComponent<VideoProcessor> prevNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, previewNode);
		prevNodeComp.setNodeType(NodeType.Receiver);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Preview Frame", resolution);

		prevNodeComp.setNodeName("Preview Frame");
		prevNodeComp.setNodeComponentDescriptor(desc);

		for (var point : prevNodeComp.getNodePoints()) {
			if (point instanceof jVidNodeComponent.jVidControlNodePoint) {
				point.setNodeType(NodeType.Transmitter);
			}
		}

		prev.open();

		return prevNodeComp;
	}

	@TypeName(typeName = "NDI Egress", nodeType = NodeType.Receiver)
	public static jVidNodeComponent<VideoProcessor> createNDIEgress(@Label(label = "Resolution") Dimension resolution,
			@Label(label = "NDI Name") String name, JNodeFlowPane flowPane) {
		NDI5Egressor ndi = new NDI5Egressor(resolution, name);
		FlowNode<VideoProcessor> ndiNode = new VideoFlowNode(ndi, null, null);
		jVidNodeComponent<VideoProcessor> ndiNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ndiNode);
		ndiNodeComp.setNodeType(NodeType.Receiver);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>("NDI Egress",
				resolution, name);

		ndiNodeComp.setNodeName(name);
		ndiNodeComp.setNodeComponentDescriptor(desc);

		ndi.open();

		return ndiNodeComp;
	}

	@TypeName(typeName = "QOYV Egress", nodeType = NodeType.Receiver)
	public static jVidNodeComponent<VideoProcessor> createQOYVEgress(@Label(label = "Resolution") Dimension resolution,
			@Label(label = "Key Frame Interval") int keyFrameInterval, JNodeFlowPane flowPane) {
		QOYStreamEgressor_V2 qoyv = new QOYStreamEgressor_V2(resolution, keyFrameInterval);
		FlowNode<VideoProcessor> qoyvNode = new VideoFlowNode(qoyv, null, null);
		jVidNodeComponent<VideoProcessor> qoyvNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, qoyvNode);
		qoyvNodeComp.setNodeType(NodeType.Receiver);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"QOYV Egress", resolution, keyFrameInterval);

		qoyvNodeComp.setNodeName("QOYV Egress");
		qoyvNodeComp.setNodeComponentDescriptor(desc);

		qoyv.open();

		return qoyvNodeComp;
	}

	@TypeName(typeName = "FFmpeg Egressor", nodeType = NodeType.Receiver)
	public static jVidNodeComponent<VideoProcessor> createFFMPEGStreamEgressor(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "Multicast Address") String mcastAddress,
			@Label(label = "Codec Type") FFMPEGStreamEgressor.VideoCodec codec,
			@Label(label = "Codec Name") String codecName, JNodeFlowPane flowPane) {
		FFMPEGStreamEgressor ffout = new FFMPEGStreamEgressor(resolution, mcastAddress, codec, codecName);
		FlowNode<VideoProcessor> ffoutNode = new VideoFlowNode(ffout, null, null);
		jVidNodeComponent<VideoProcessor> ffoutComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffoutNode);
		ffoutComp.setNodeType(NodeType.Receiver);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"FFmpeg Egressor", resolution, mcastAddress, codec, codecName);

		ffoutComp.setNodeName(mcastAddress);
		ffoutComp.setNodeComponentDescriptor(desc);

		ffout.open();

		return ffoutComp;
	}

	@TypeName(typeName = "Raw Audio Egressor", nodeType = NodeType.Receiver)
	public static jVidNodeComponent<VideoProcessor> createRawAudioEgressor(@Label(label = "Channels") int channels,
			@Label(label = "Port") int port, JNodeFlowPane flowPane) {
		RawAudioEgressor raw = new RawAudioEgressor(new Dimension(1920, 1080), channels, port);
		FlowNode<VideoProcessor> rawNode = new VideoFlowNode(raw, null, null);
		jVidNodeComponent<VideoProcessor> rawNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, rawNode);
		rawNodeComp.setNodeType(NodeType.Receiver);
		rawNodeComp.setNodeName("Audio Out: " + port);

		raw.open();

		return rawNodeComp;
	}
}
