package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;

import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.video.VideoProcessor;
import com.javashell.video.digestors.AutoFramingDigestor;
import com.javashell.video.digestors.FaceSetPaintingDigestor;
import com.javashell.video.digestors.MatrixDigestor;
import com.javashell.video.digestors.MultiviewDigestor;
import com.javashell.video.digestors.OpenCVDeepLearningFaceDetectorDigestor;

public class DigestNodeFactory {

	@TypeName(typeName = "AutoFraming Digest")
	public static jVidNodeComponent<VideoProcessor> createAutoFramingDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		AutoFramingDigestor autoFraming = new AutoFramingDigestor(resolution);
		FlowNode<VideoProcessor> autoFramingNode = new VideoFlowNode(autoFraming, null, null);
		jVidNodeComponent<VideoProcessor> autoFramingNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
				autoFramingNode);

		autoFraming.open();

		return autoFramingNodeComp;
	}

	@TypeName(typeName = "FacePaint Digest")
	public static jVidNodeComponent<VideoProcessor> createFaceSetPaintingDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		FaceSetPaintingDigestor facePaint = new FaceSetPaintingDigestor(resolution);
		FlowNode<VideoProcessor> facePaintNode = new VideoFlowNode(facePaint, null, null);
		jVidNodeComponent<VideoProcessor> facePaintNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
				facePaintNode);

		facePaint.open();

		return facePaintNodeComp;
	}

	@TypeName(typeName = "Matrix Digest")
	public static jVidNodeComponent<VideoProcessor> createMatrixDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		MatrixDigestor matrix = new MatrixDigestor();
		jVidNodeComponent<VideoProcessor> matrixNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, matrix);

		return matrixNodeComp;
	}

	@TypeName(typeName = "Multiview Digest")
	public static jVidNodeComponent<VideoProcessor> createMultiviewDigestor(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "Viewports") int viewports,
			@Label(label = "Rows") int rows, @Label(label = "Columns") int cols, JNodeFlowPane flowPane) {
		MultiviewDigestor multi = new MultiviewDigestor(resolution, viewports, rows, cols);
		FlowNode<VideoProcessor> multiNode = new VideoFlowNode(multi, null, null);
		jVidNodeComponent<VideoProcessor> multiNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, multiNode);

		multi.open();

		return multiNodeComp;
	}

	@TypeName(typeName = "Face Detector")
	public static jVidNodeComponent<VideoProcessor> createFaceDetectorDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		OpenCVDeepLearningFaceDetectorDigestor detector = new OpenCVDeepLearningFaceDetectorDigestor(resolution);
		FlowNode<VideoProcessor> detectNode = new VideoFlowNode(detector, null, null);
		jVidNodeComponent<VideoProcessor> detectNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, detectNode);

		detector.open();

		return detectNodeComp;
	}

}
