package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;

import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.video.VideoProcessor;
import com.javashell.video.digestors.AutoFramingDigestor;
import com.javashell.video.digestors.FaceSetPaintingDigestor;
import com.javashell.video.digestors.MatrixDigestor;
import com.javashell.video.digestors.MultiviewDigestor;

public class DigestNodeFactory {

	public static jVidNodeComponent<VideoProcessor> createAutoFramingDigestor(Dimension resolution,
			JNodeFlowPane flowPane) {
		AutoFramingDigestor autoFraming = new AutoFramingDigestor(resolution);
		FlowNode<VideoProcessor> autoFramingNode = new VideoFlowNode(autoFraming, null, null);
		jVidNodeComponent<VideoProcessor> autoFramingNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
				autoFramingNode);
		return autoFramingNodeComp;
	}

	public static jVidNodeComponent<VideoProcessor> createFaceSetPaintingDigestor(Dimension resolution,
			JNodeFlowPane flowPane) {
		FaceSetPaintingDigestor facePaint = new FaceSetPaintingDigestor(resolution);
		FlowNode<VideoProcessor> facePaintNode = new VideoFlowNode(facePaint, null, null);
		jVidNodeComponent<VideoProcessor> facePaintNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
				facePaintNode);
		return facePaintNodeComp;
	}

	public static jVidNodeComponent<VideoProcessor> createMatrixDigestor(Dimension resolution, JNodeFlowPane flowPane) {
		MatrixDigestor matrix = new MatrixDigestor();
		jVidNodeComponent<VideoProcessor> matrixNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, matrix);
		return matrixNodeComp;
	}

	public static jVidNodeComponent<VideoProcessor> createMultiviewDigestor(Dimension resolution, int viewports,
			int rows, int cols, JNodeFlowPane flowPane) {
		MultiviewDigestor multi = new MultiviewDigestor(resolution, viewports, rows, cols);
		FlowNode<VideoProcessor> multiNode = new VideoFlowNode(multi, null, null);
		jVidNodeComponent<VideoProcessor> multiNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, multiNode);
		return multiNodeComp;
	}

}
