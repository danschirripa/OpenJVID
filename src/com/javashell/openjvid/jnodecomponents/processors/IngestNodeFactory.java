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
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.video.VideoProcessor;
import com.javashell.video.camera.Camera;
import com.javashell.video.camera.extras.AmcrestCameraInterface;
import com.javashell.video.ingestors.FFMPEGIngestor;
import com.javashell.video.ingestors.NDI5Ingestor;
import com.javashell.video.ingestors.QOYStreamIngestor;

public class IngestNodeFactory {

	@TypeName(typeName = "NDI Ingest")
	public static jVidNodeComponent<VideoProcessor> createNDI5Ingest(String ndiName, Dimension resolution,
			JNodeFlowPane flowPane) {
		NDI5Ingestor ingest = new NDI5Ingestor(resolution, ndiName);
		FlowNode<VideoProcessor> ingressNode = new VideoFlowNode(ingest, null, null);
		jVidNodeComponent<VideoProcessor> ingestNode = new jVidNodeComponent<VideoProcessor>(flowPane, ingressNode);
		FlowController.registerFlowNode(ingressNode);
		return ingestNode;
	}

	@TypeName(typeName = "Amcrest Ingest")
	public static jVidNodeComponent<VideoProcessor> createAmcrestIngest(Dimension resolution, String user, String pass,
			String ip, int bitrate, String camType, JNodeFlowPane flowPane)
			throws NoSuchAlgorithmException, IOException, URISyntaxException, InterruptedException {
		Camera cam = Camera.getCamera(camType);
		AmcrestCameraInterface amc = new AmcrestCameraInterface(resolution, user, pass, ip, bitrate, cam);
		FlowNode<VideoProcessor> amcNode = new VideoFlowNode(amc, null, null);
		jVidNodeComponent<VideoProcessor> amcNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, amcNode);
		FlowController.registerFlowNode(amcNode);
		return amcNodeComp;
	}

	@TypeName(typeName = "QOYV Ingest")
	public static jVidNodeComponent<VideoProcessor> createQOYVStreamIngest(Dimension resolution, String ip, int port,
			boolean isMulticast, JNodeFlowPane flowPane) {
		QOYStreamIngestor qoyv = new QOYStreamIngestor(resolution, ip, port, isMulticast);
		FlowNode<VideoProcessor> qoyvNode = new VideoFlowNode(qoyv, null, null);
		jVidNodeComponent<VideoProcessor> qoyvNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, qoyvNode);
		FlowController.registerFlowNode(qoyvNode);
		return qoyvNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest")
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(Dimension resolution, URL videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest")
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(Dimension resolution, File videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		return ffmpegNodeComp;
	}

	@TypeName(typeName = "FFmpeg Ingest")
	public static jVidNodeComponent<VideoProcessor> createFFMPEGIngest(Dimension resolution, String videoInput,
			JNodeFlowPane flowPane) {
		FFMPEGIngestor ffmpeg = new FFMPEGIngestor(resolution, videoInput);
		FlowNode<VideoProcessor> ffmpegNode = new VideoFlowNode(ffmpeg, null, null);
		jVidNodeComponent<VideoProcessor> ffmpegNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ffmpegNode);
		FlowController.registerFlowNode(ffmpegNode);
		return ffmpegNodeComp;
	}

}
