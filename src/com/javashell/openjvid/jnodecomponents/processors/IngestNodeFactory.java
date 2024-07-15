package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import com.javashell.flow.FlowController;
import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.video.VideoProcessor;
import com.javashell.video.camera.Camera;
import com.javashell.video.camera.extras.AmcrestCameraInterface;
import com.javashell.video.ingestors.FFMPEGIngestor;
import com.javashell.video.ingestors.NDI5Ingestor;
import com.javashell.video.ingestors.QOYStreamIngestor;

public class IngestNodeFactory {

	@TypeName(typeName = "NDI Ingest")
	public static jVidNodeComponent<VideoProcessor> createNDI5Ingest(@Label(label = "NDI Name") String ndiName,
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		NDI5Ingestor ingest = new NDI5Ingestor(resolution, ndiName);
		FlowNode<VideoProcessor> ingressNode = new VideoFlowNode(ingest, null, null);
		jVidNodeComponent<VideoProcessor> ingestNode = new jVidNodeComponent<VideoProcessor>(flowPane, ingressNode);
		FlowController.registerFlowNode(ingressNode);
		ingestNode.setNodeType(NodeType.Transmitter);

		ingestNode.setNodeName(ndiName);

		ingest.open();

		return ingestNode;
	}

	@TypeName(typeName = "Amcrest Ingest")
	public static jVidNodeComponent<VideoProcessor> createAmcrestIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "User") String user,
			@Label(label = "Password") String pass, @Label(label = "IP") String ip,
			@Label(label = "Bitrate") int bitrate, @Label(label = "CameraType") String camType, JNodeFlowPane flowPane)
			throws NoSuchAlgorithmException, IOException, URISyntaxException, InterruptedException {
		Camera cam = Camera.getCamera(camType);
		AmcrestCameraInterface amc = new AmcrestCameraInterface(resolution, user, pass, ip, bitrate, cam);
		FlowNode<VideoProcessor> amcNode = new VideoFlowNode(amc, null, null);
		jVidNodeComponent<VideoProcessor> amcNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, amcNode);
		FlowController.registerFlowNode(amcNode);
		amcNodeComp.setNodeType(NodeType.Transmitter);

		amcNodeComp.setNodeName(camType + ": " + ip);

		amc.open();

		return amcNodeComp;
	}

	@TypeName(typeName = "QOYV Ingest")
	public static jVidNodeComponent<VideoProcessor> createQOYVStreamIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "IP") String ip,
			@Label(label = "Port") int port, @Label(label = "Multicast") boolean isMulticast, JNodeFlowPane flowPane) {
		QOYStreamIngestor qoyv = new QOYStreamIngestor(resolution, ip, port, isMulticast);
		FlowNode<VideoProcessor> qoyvNode = new VideoFlowNode(qoyv, null, null);
		jVidNodeComponent<VideoProcessor> qoyvNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, qoyvNode);
		FlowController.registerFlowNode(qoyvNode);
		qoyvNodeComp.setNodeType(NodeType.Transmitter);

		qoyvNodeComp.setNodeName("QOYV: " + ip);

		qoyv.open();

		return qoyvNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (URL)")
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "URL") URL videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (File)")
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "File Path") File videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (String)")
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "FFmpeg Input String") String videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

}
