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
import com.javashell.openjvid.configuration.jVidNodeComponentDescriptor;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.openjvid.ui.components.JackInputComponent.JackInputClient;
import com.javashell.video.VideoProcessor;
import com.javashell.video.camera.Camera;
import com.javashell.video.camera.extras.AmcrestCameraInterface;
import com.javashell.video.digestors.AudioInjectorDigestor;
import com.javashell.video.ingestors.FFMPEGIngestor;
import com.javashell.video.ingestors.NDI5Ingestor;
import com.javashell.video.ingestors.QOYStreamIngestor;

public class IngestNodeFactory {

	@TypeName(typeName = "NDI Ingest", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createNDI5Ingest(@Label(label = "NDI Name") String ndiName,
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		NDI5Ingestor ingest = new NDI5Ingestor(resolution, ndiName);
		FlowNode<VideoProcessor> ingressNode = new VideoFlowNode(ingest, null, null);
		jVidNodeComponent<VideoProcessor> ingestNode = new jVidNodeComponent<VideoProcessor>(flowPane, ingressNode);
		FlowController.registerFlowNode(ingressNode);
		ingestNode.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>("NDI Ingest",
				ndiName, resolution);

		ingestNode.setNodeName(ndiName);
		ingestNode.setNodeComponentDescriptor(desc);

		ingest.open();

		return ingestNode;
	}

	@TypeName(typeName = "Amcrest Ingest", nodeType = NodeType.Transmitter)
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

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Amcrest Ingest", resolution, user, pass, ip, bitrate, camType);

		amcNodeComp.setNodeName(camType + ": " + ip);
		amcNodeComp.setNodeComponentDescriptor(desc);

		amc.open();

		return amcNodeComp;
	}

	@TypeName(typeName = "QOYV Ingest", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createQOYVStreamIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "IP") String ip,
			@Label(label = "Port") int port, JNodeFlowPane flowPane) {

		var isMulticast = false;
		QOYStreamIngestor qoyv = new QOYStreamIngestor(resolution, ip, port, isMulticast);
		FlowNode<VideoProcessor> qoyvNode = new VideoFlowNode(qoyv, null, null);
		jVidNodeComponent<VideoProcessor> qoyvNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, qoyvNode);
		FlowController.registerFlowNode(qoyvNode);
		qoyvNodeComp.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"QOYV Ingest", resolution, ip, port);

		qoyvNodeComp.setNodeName("QOYV: " + ip);
		qoyvNodeComp.setNodeComponentDescriptor(desc);

		qoyv.open();

		return qoyvNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (URL)", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "URL") URL videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"FFmpeg Ingest (URL)", resolution, videoInput);

		ffmpegNodeComp.setNodeComponentDescriptor(desc);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (File)", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "File Path") File videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"FFmpeg Ingest (File)", resolution, videoInput);

		ffmpegNodeComp.setNodeName(videoInput.getName());
		ffmpegNodeComp.setNodeComponentDescriptor(desc);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (Video Device)", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "Device Path") File videoInput,
			@Label(label = "Codec") String codec, @Label(label = "Framerate") int frameRate, JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		ffmpeg.setFrameRate(frameRate);
		ffmpeg.setOption("input_format", codec);
		ffmpeg.setOption("video_size", resolution.width + "x" + resolution.height);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"FFmpeg Ingest (Video Device)", resolution, videoInput, codec, frameRate);

		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);

		ffmpegNodeComp.setNodeName(videoInput.getName());
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);
		ffmpegNodeComp.setNodeComponentDescriptor(desc);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest (String)", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "FFmpeg Input String") String videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		ffmpegNodeComp.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"FFmpeg Ingest (String)", resolution, videoInput);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	@TypeName(typeName = "Jack Client", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createJackAudioClient(
			@Label(label = "Source Name") JackInputClient sourceName, JNodeFlowPane flowPane) {

		if (FlowController.jackManager.getRegisteredClients().containsKey(sourceName.clientName)) {

			AudioInjectorDigestor ajd = new AudioInjectorDigestor("OpenJVID - " + sourceName.clientName,
					FlowController.jackManager.getRegisteredClients().get(sourceName.clientName),
					new Dimension(1920, 1080));

			FlowNode<VideoProcessor> ajdNode = new VideoFlowNode(ajd, null, null);
			jVidNodeComponent<VideoProcessor> ajdNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ajdNode);

			jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
					"Jack Client", sourceName);

			ajdNodeComp.setNodeType(NodeType.Transmitter);
			ajdNodeComp.setNodeName(sourceName.clientName);
			ajdNodeComp.setNodeComponentDescriptor(desc);

			ajd.open();

			try {
				ajd.connectTo(sourceName.clientName);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ajdNodeComp;
		}

		return null;
	}

}
