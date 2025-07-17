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
import com.javashell.openjvid.ui.components.input.JackInputComponent.JackInputClient;
import com.javashell.openjvid.ui.components.input.gstreamer.VideoInputDeviceInputComponent;
import com.javashell.video.VideoProcessor;
import com.javashell.video.camera.Camera;
import com.javashell.video.camera.extras.AmcrestCameraInterface;
import com.javashell.video.digestors.AudioInjectorDigestor;
import com.javashell.video.ingestors.FFMPEGIngestor;
import com.javashell.video.ingestors.GStreamerIngestor;
import com.javashell.video.ingestors.LocalScreenIngestor;
import com.javashell.video.ingestors.NDI5Ingestor;
import com.javashell.video.ingestors.QOYStreamIngestor_V2;
import com.javashell.video.ingestors.RawAudioIngestor;

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
		QOYStreamIngestor_V2 qoyv = new QOYStreamIngestor_V2(resolution, ip, port, isMulticast);
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

	@TypeName(typeName = "GStreamer Ingest", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createGstreamerIngest(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "GStreamer String") String gstString,
			JNodeFlowPane flowPane) {
		GStreamerIngestor gst = new GStreamerIngestor(resolution, gstString);
		FlowNode<VideoProcessor> gstNode = new VideoFlowNode(gst, null, null);
		jVidNodeComponent<VideoProcessor> gstNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, gstNode);
		FlowController.registerFlowNode(gstNode);
		gstNodeComp.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"GStreamer Ingest", resolution, gstString);

		gstNodeComp.setNodeComponentDescriptor(desc);

		gst.open();

		return gstNodeComp;
	}

	@TypeName(typeName = "GStreamer Ingest Basic", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createGstreamerIngest2(
			@Label(label = "Resolution") Dimension resolution,
			@Label(label = "GStreamer String") VideoInputDeviceInputComponent.VideoInputClient client,
			JNodeFlowPane flowPane) {

		return null;
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
		ffmpegNodeComp.setNodeComponentDescriptor(desc);

		ffmpeg.open();

		return ffmpegNodeComp;
	}

	// Hidden until ffmpeg wayland capture support is implemented
	@TypeName(typeName = "Screencapture", nodeType = NodeType.Transmitter, isShown = false)
	public static jVidNodeComponent<VideoProcessor> createScreenCap(Dimension resolution, JNodeFlowPane flowPane) {
		LocalScreenIngestor ingest = new LocalScreenIngestor(resolution);
		FlowNode<VideoProcessor> ingestNode = new VideoFlowNode(ingest, null, null);
		jVidNodeComponent<VideoProcessor> ingestNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ingestNode);
		FlowController.registerFlowNode(ingestNode);
		ingestNodeComp.setNodeType(NodeType.Transmitter);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Screencapture", resolution);

		ingestNodeComp.setNodeComponentDescriptor(desc);
		ingest.open();
		return ingestNodeComp;
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
					"Jack Client - String", sourceName.clientName);

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

	@TypeName(typeName = "Jack Client - String", nodeType = NodeType.Transmitter, isShown = false)
	public static jVidNodeComponent<VideoProcessor> createJackAudioClient(
			@Label(label = "Source Name") String sourceName, JNodeFlowPane flowPane) {

		if (FlowController.jackManager.getRegisteredClients().containsKey(sourceName)) {

			AudioInjectorDigestor ajd = new AudioInjectorDigestor("OpenJVID - " + sourceName,
					FlowController.jackManager.getRegisteredClients().get(sourceName), new Dimension(1920, 1080));

			FlowNode<VideoProcessor> ajdNode = new VideoFlowNode(ajd, null, null);
			jVidNodeComponent<VideoProcessor> ajdNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ajdNode);

			jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
					"Jack Client - String", sourceName);

			ajdNodeComp.setNodeType(NodeType.Transmitter);
			ajdNodeComp.setNodeName(sourceName);
			ajdNodeComp.setNodeComponentDescriptor(desc);

			ajd.open();

			try {
				ajd.connectTo(sourceName);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ajdNodeComp;
		}

		return null;
	}

	@TypeName(typeName = "Raw Audio Ingestor", nodeType = NodeType.Transmitter)
	public static jVidNodeComponent<VideoProcessor> createRawAudioIngestor(@Label(label = "Channels") int channels,
			@Label(label = "Port") int port, @Label(label = "Source IP") String ipAddress, JNodeFlowPane flowPane) {
		RawAudioIngestor raw = new RawAudioIngestor(new Dimension(1920, 1080), channels, port, ipAddress);
		FlowNode<VideoProcessor> rawNode = new VideoFlowNode(raw, null, null);
		jVidNodeComponent<VideoProcessor> rawNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, rawNode);
		rawNodeComp.setNodeType(NodeType.Transmitter);
		rawNodeComp.setNodeName(ipAddress + ":" + port);

		raw.open();

		return rawNodeComp;
	}

}
