package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;
import java.util.UUID;

import org.jaudiolibs.jnajack.Jack;

import com.javashell.audio.dsp.BasicReverb;
import com.javashell.audio.dsp.CombFilter;
import com.javashell.audio.dsp.CombFilter.CombFilterType;
import com.javashell.audio.dsp.GainFilter;
import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.jnodecomponents.DigitalSignalDigestor;
import com.javashell.openjvid.jnodecomponents.OpenJVIDPeripheral;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.openjvid.peripheral.PeripheralDescriptor;
import com.javashell.openjvid.ui.MatrixDigestorCrosspointDialog;
import com.javashell.video.VideoProcessor;
import com.javashell.video.digestors.AudioExtractorDigestor;
import com.javashell.video.digestors.AudioInjectorDigestor;
import com.javashell.video.digestors.AutoFramingDigestor;
import com.javashell.video.digestors.FaceSetPaintingDigestor;
import com.javashell.video.digestors.MatrixDigestor;
import com.javashell.video.digestors.MultiviewDigestor;
import com.javashell.video.digestors.OpenCVDeepLearningFaceDetectorDigestor;
import com.javashell.video.digestors.ScalingDigestor;

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

				if (cascade)
					origin.addChildLinkage(this, false);

				if (origin.getNodeType() == NodeType.Transmitter)
					crosspointDialog.addSource((jVidNodeComponent<VideoProcessor>) origin);
				else if (origin.getNodeType() == NodeType.Transceiver)
					crosspointDialog.addTransceiver((jVidNodeComponent<VideoProcessor>) origin);
			}

			@Override
			public void addChildLinkage(JNodeComponent child, boolean cascade) throws IncorrectLinkageException {
				System.out.println("OVERRIDE - ADD CHILD");

				if (cascade)
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

	@TypeName(typeName = "Scaling Digest")
	public static jVidNodeComponent<VideoProcessor> createScalingDigestor(
			@Label(label = "Input Resolution") Dimension resolution,
			@Label(label = "Output Resolution") Dimension outputResolution, JNodeFlowPane flowPane) {
		ScalingDigestor scaler = new ScalingDigestor(resolution, outputResolution);
		FlowNode<VideoProcessor> scalerNode = new VideoFlowNode(scaler, null, null);
		jVidNodeComponent<VideoProcessor> scalerNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, scalerNode);

		scalerNodeComp.setNodeType(NodeType.Transceiver);
		scalerNodeComp.setNodeName("Scaler");

		scaler.open();

		return scalerNodeComp;
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

	@TypeName(typeName = "AudioInjector")
	public static jVidNodeComponent<VideoProcessor> createAudioInjector(@Label(label = "Jack Name") String name,
			JNodeFlowPane flowPane) {
		AudioInjectorDigestor ajd = new AudioInjectorDigestor(name, new String[] { "1" }, new Dimension(1920, 1080));
		FlowNode<VideoProcessor> ajdNode = new VideoFlowNode(ajd, null, null);
		jVidNodeComponent<VideoProcessor> ajdNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ajdNode);

		ajdNodeComp.setNodeType(NodeType.Transmitter);
		ajdNodeComp.setNodeName("Audio In: " + name);

		ajd.open();

		return ajdNodeComp;
	}

	@TypeName(typeName = "AudioExtractor")
	public static jVidNodeComponent<VideoProcessor> createAudioExtractor(@Label(label = "Jack Name") String name,
			JNodeFlowPane flowPane) {
		AudioExtractorDigestor ajd = new AudioExtractorDigestor(name, new String[] { "1" }, new Dimension(1920, 1080));
		FlowNode<VideoProcessor> ajdNode = new VideoFlowNode(ajd, null, null);
		jVidNodeComponent<VideoProcessor> ajdNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ajdNode);

		ajdNodeComp.setNodeType(NodeType.Transceiver);
		ajdNodeComp.setNodeName("Audio Out: " + name);

		ajd.open();

		return ajdNodeComp;
	}

	@TypeName(typeName = "CombFilter")
	public static jVidNodeComponent<VideoProcessor> createCombFilterComponent(@Label(label = "Delay") float delay,
			@Label(label = "Decay Factor") float decayFactor, JNodeFlowPane flowPane) {
		CombFilter cf = new CombFilter(CombFilterType.FEED_FORWARD);
		DigitalSignalDigestor dsp = new DigitalSignalDigestor(cf, delay, decayFactor);

		FlowNode<VideoProcessor> dspNode = new VideoFlowNode(dsp, null, null);
		jVidNodeComponent<VideoProcessor> dspNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, dspNode);
		dspNodeComp.setNodeName("CombFilter");

		dsp.open();

		return dspNodeComp;
	}

	@TypeName(typeName = "Reverb")
	public static jVidNodeComponent<VideoProcessor> createReverbComponent(@Label(label = "Delay") float delay,
			@Label(label = "Decay Factor") float decayFactor, JNodeFlowPane flowPane) {
		BasicReverb cf = new BasicReverb(2);
		DigitalSignalDigestor dsp = new DigitalSignalDigestor(cf, delay, decayFactor);

		FlowNode<VideoProcessor> dspNode = new VideoFlowNode(dsp, null, null);
		jVidNodeComponent<VideoProcessor> dspNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, dspNode);
		dspNodeComp.setNodeName("Reverb");

		dsp.open();

		return dspNodeComp;
	}

	@TypeName(typeName = "Gain")
	public static jVidNodeComponent<VideoProcessor> createGainComponent(@Label(label = "Percentage") float perc,
			JNodeFlowPane flowPane) {
		GainFilter cf = new GainFilter();
		cf.setGain(perc);
		DigitalSignalDigestor dsp = new DigitalSignalDigestor(cf, 0, 0);

		FlowNode<VideoProcessor> dspNode = new VideoFlowNode(dsp, null, null);
		jVidNodeComponent<VideoProcessor> dspNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, dspNode);
		dspNodeComp.setNodeName("Reverb");

		dsp.open();

		return dspNodeComp;
	}

	@TypeName(typeName = "OpenJVID Peripheral")
	public static jVidNodeComponent<VideoProcessor> createOpenJVIDPeripheral(
			@Label(label = "Peripheral") PeripheralDescriptor pd, JNodeFlowPane flowPane) {
		try {
			OpenJVIDPeripheral periph = new OpenJVIDPeripheral(pd);
			FlowNode<VideoProcessor> periphNode = new VideoFlowNode(periph, null, null);
			jVidNodeComponent<VideoProcessor> periphNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
					periphNode);

			periphNodeComp.setNodeType(NodeType.Transceiver);
			periphNodeComp.setNodeName(pd.getInetAddress().getCanonicalHostName());

			periph.open();

			return periphNodeComp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static jVidNodeComponent<VideoProcessor> createOpenJVIDPeripheral(PeripheralDescriptor pd, UUID sessionID,
			int port, JNodeFlowPane flowPane) {
		try {
			OpenJVIDPeripheral periph = new OpenJVIDPeripheral(pd, sessionID, port);
			FlowNode<VideoProcessor> periphNode = new VideoFlowNode(periph, null, null);
			jVidNodeComponent<VideoProcessor> periphNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
					periphNode);

			periphNodeComp.setNodeType(NodeType.Transceiver);
			periphNodeComp.setNodeName(pd.getInetAddress().getCanonicalHostName());

			periph.open();

			return periphNodeComp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
