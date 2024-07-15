package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;

import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.openjvid.ui.MatrixDigestorCrosspointDialog;
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
		autoFramingNodeComp.setNodeType(NodeType.Transceiver);
		autoFramingNodeComp.setNodeName("AutoFraming");

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
		facePaintNodeComp.setNodeType(NodeType.Transceiver);
		facePaintNodeComp.setNodeName("FaceSet Painter");

		facePaint.open();

		return facePaintNodeComp;
	}

	@TypeName(typeName = "Matrix Digest")
	public static jVidNodeComponent<VideoProcessor> createMatrixDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		MatrixDigestor matrix = new MatrixDigestor();

		final MatrixDigestorCrosspointDialog crosspointDialog = new MatrixDigestorCrosspointDialog();
		jVidNodeComponent<VideoProcessor> matrixNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, matrix) {

			private static final long serialVersionUID = 3800957082229059177L;

			@Override
			public void addOriginLinkage(JNodeComponent origin, boolean cascade) throws IncorrectLinkageException {
				System.out.println("OVERRIDE - ADD ORIGIN");
				
				if(cascade)
					origin.addChildLinkage(this, false);
				
				if (origin.getNodeType() == NodeType.Transmitter)
					crosspointDialog.addSource((jVidNodeComponent<VideoProcessor>) origin);
				else if (origin.getNodeType() == NodeType.Transceiver)
					crosspointDialog.addTransceiver((jVidNodeComponent<VideoProcessor>) origin);
			}

			@Override
			public void addChildLinkage(JNodeComponent child, boolean cascade) throws IncorrectLinkageException {
				System.out.println("OVERRIDE - ADD CHILD");
				
				if(cascade)
					child.addOriginLinkage(this, false);

				if (child.getNodeType() == NodeType.Receiver)
					crosspointDialog.addSink((jVidNodeComponent<VideoProcessor>) child);
				else if (child.getNodeType() == NodeType.Transceiver)
					crosspointDialog.addTransceiver((jVidNodeComponent<VideoProcessor>) child);
			}

			@Override
			public void removeChildLinkage(JNodeComponent child) {
				crosspointDialog.removeComponent((jVidNodeComponent<VideoProcessor>) child);
			}

			@Override
			public void removeOriginLinkage(JNodeComponent origin) {
				crosspointDialog.removeComponent((jVidNodeComponent<VideoProcessor>) origin);
			}
		};
		crosspointDialog.setMatrix(matrix);

		matrixNodeComp.setNodeType(NodeType.Transceiver);
		matrixNodeComp.setNodeName("Matrix");
		matrixNodeComp.setComponentPopupMenu(crosspointDialog.getCrosspointJMenu());

		return matrixNodeComp;
	}

	@TypeName(typeName = "Multiview Digest")
	public static jVidNodeComponent<VideoProcessor> createMultiviewDigestor(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "Viewports") int viewports,
			@Label(label = "Rows") int rows, @Label(label = "Columns") int cols, JNodeFlowPane flowPane) {
		MultiviewDigestor multi = new MultiviewDigestor(resolution, viewports, rows, cols);
		FlowNode<VideoProcessor> multiNode = new VideoFlowNode(multi, null, null);
		jVidNodeComponent<VideoProcessor> multiNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, multiNode);

		multiNodeComp.setNodeType(NodeType.Transceiver);
		multiNodeComp.setNodeName("Multiview");

		multi.open();

		return multiNodeComp;
	}

	@TypeName(typeName = "Face Detector")
	public static jVidNodeComponent<VideoProcessor> createFaceDetectorDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		OpenCVDeepLearningFaceDetectorDigestor detector = new OpenCVDeepLearningFaceDetectorDigestor(resolution);
		FlowNode<VideoProcessor> detectNode = new VideoFlowNode(detector, null, null);
		jVidNodeComponent<VideoProcessor> detectNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, detectNode);

		detectNodeComp.setNodeType(NodeType.Transceiver);
		detectNodeComp.setNodeName("Face Detector");

		detector.open();

		return detectNodeComp;
	}

}
