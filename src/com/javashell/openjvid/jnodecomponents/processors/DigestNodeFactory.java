package com.javashell.openjvid.jnodecomponents.processors;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.javashell.audio.dsp.BasicReverb;
import com.javashell.audio.dsp.CombFilter;
import com.javashell.audio.dsp.CombFilter.CombFilterType;
import com.javashell.audio.dsp.GainFilter;
import com.javashell.flow.FlowNode;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.openjvid.configuration.jVidNodeComponentDescriptor;
import com.javashell.openjvid.jnodecomponents.DigitalSignalDigestor;
import com.javashell.openjvid.jnodecomponents.OpenJVIDPeripheral;
import com.javashell.openjvid.jnodecomponents.jVidMatrixNodeComponent;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent;
import com.javashell.openjvid.jnodecomponents.jVidScriptedComponent;
import com.javashell.openjvid.jnodecomponents.processors.ParameterLabelAnnotation.Label;
import com.javashell.openjvid.jnodecomponents.processors.TypeNameAnnotation.TypeName;
import com.javashell.openjvid.peripheral.PeripheralDescriptor;
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

	@TypeName(typeName = "AutoFraming Digest", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createAutoFramingDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		AutoFramingDigestor autoFraming = new AutoFramingDigestor(resolution);
		FlowNode<VideoProcessor> autoFramingNode = new VideoFlowNode(autoFraming, null, null);
		jVidNodeComponent<VideoProcessor> autoFramingNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
				autoFramingNode);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"AutoFraming Digest", resolution);

		autoFramingNodeComp.setNodeType(NodeType.Transceiver);
		autoFramingNodeComp.setNodeName("AutoFraming");
		autoFramingNodeComp.setNodeComponentDescriptor(desc);

		autoFraming.open();

		return autoFramingNodeComp;
	}

	@TypeName(typeName = "FacePaint Digest", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createFaceSetPaintingDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		FaceSetPaintingDigestor facePaint = new FaceSetPaintingDigestor(resolution);
		FlowNode<VideoProcessor> facePaintNode = new VideoFlowNode(facePaint, null, null);
		jVidNodeComponent<VideoProcessor> facePaintNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
				facePaintNode);
		facePaintNodeComp.setNodeType(NodeType.Transceiver);
		facePaintNodeComp.setNodeName("FaceSet Painter");

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"FacePaint Digest", resolution);

		facePaintNodeComp.setNodeComponentDescriptor(desc);

		facePaint.open();

		return facePaintNodeComp;
	}

	@TypeName(typeName = "Matrix Digest", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createMatrixDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		MatrixDigestor matrix = new MatrixDigestor();

		jVidMatrixNodeComponent matrixNodeComp = new jVidMatrixNodeComponent(flowPane, matrix);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Matrix Digest - Prepopulated", resolution);

		matrixNodeComp.setNodeType(NodeType.Transceiver);
		matrixNodeComp.setNodeName("Matrix");
		matrixNodeComp.setNodeComponentDescriptor(desc);

		return matrixNodeComp;
	}

	@TypeName(typeName = "Matrix Digest - Prepopulated", isShown = false, nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createPopulatedMatrixDigestor(
			@Label(label = "Resolution") Dimension resolution,
			HashMap<jVidNodeComponent<VideoProcessor>, HashSet<jVidNodeComponent<VideoProcessor>>> crosspoints,
			JNodeFlowPane flowPane) {
		MatrixDigestor matrix = new MatrixDigestor();

		jVidMatrixNodeComponent matrixNodeComp = new jVidMatrixNodeComponent(flowPane, matrix, crosspoints);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Matrix Digest - Prepopulated", resolution);

		matrixNodeComp.setNodeType(NodeType.Transceiver);
		matrixNodeComp.setNodeName("Matrix");
		matrixNodeComp.setNodeComponentDescriptor(desc);

		return matrixNodeComp;
	}

	@TypeName(typeName = "Multiview Digest", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createMultiviewDigestor(
			@Label(label = "Resolution") Dimension resolution, @Label(label = "Viewports") int viewports,
			@Label(label = "Rows") int rows, @Label(label = "Columns") int cols, JNodeFlowPane flowPane) {
		MultiviewDigestor multi = new MultiviewDigestor(resolution, viewports, rows, cols);
		FlowNode<VideoProcessor> multiNode = new VideoFlowNode(multi, null, null);
		jVidNodeComponent<VideoProcessor> multiNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, multiNode);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Multiview Digest", resolution, viewports, rows, cols);

		multiNodeComp.setNodeType(NodeType.Transceiver);
		multiNodeComp.setNodeName("Multiview");
		multiNodeComp.setNodeComponentDescriptor(desc);

		multi.open();

		return multiNodeComp;
	}

	@TypeName(typeName = "Scaling Digest", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createScalingDigestor(
			@Label(label = "Input Resolution") Dimension resolution,
			@Label(label = "Output Resolution") Dimension outputResolution, JNodeFlowPane flowPane) {
		ScalingDigestor scaler = new ScalingDigestor(resolution, outputResolution);
		FlowNode<VideoProcessor> scalerNode = new VideoFlowNode(scaler, null, null);
		jVidNodeComponent<VideoProcessor> scalerNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, scalerNode);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Scaling Digest", resolution, outputResolution);

		scalerNodeComp.setNodeType(NodeType.Transceiver);
		scalerNodeComp.setNodeName("Scaler");
		scalerNodeComp.setNodeComponentDescriptor(desc);

		scaler.open();

		return scalerNodeComp;
	}

	@TypeName(typeName = "Face Detector", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createFaceDetectorDigestor(
			@Label(label = "Resolution") Dimension resolution, JNodeFlowPane flowPane) {
		OpenCVDeepLearningFaceDetectorDigestor detector = new OpenCVDeepLearningFaceDetectorDigestor(resolution);
		FlowNode<VideoProcessor> detectNode = new VideoFlowNode(detector, null, null);
		jVidNodeComponent<VideoProcessor> detectNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, detectNode);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"Face Detector", resolution);

		detectNodeComp.setNodeType(NodeType.Transceiver);
		detectNodeComp.setNodeName("Face Detector");
		detectNodeComp.setNodeComponentDescriptor(desc);

		detector.open();

		return detectNodeComp;
	}

	@TypeName(typeName = "AudioInjector", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createAudioInjector(@Label(label = "Jack Name") String name,
			JNodeFlowPane flowPane) {
		AudioInjectorDigestor ajd = new AudioInjectorDigestor(name, new String[] { "1" }, new Dimension(1920, 1080));
		FlowNode<VideoProcessor> ajdNode = new VideoFlowNode(ajd, null, null);
		jVidNodeComponent<VideoProcessor> ajdNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ajdNode);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"AudioInjector", name);

		ajdNodeComp.setNodeType(NodeType.Transmitter);
		ajdNodeComp.setNodeName("Audio In: " + name);
		ajdNodeComp.setNodeComponentDescriptor(desc);

		ajd.open();

		return ajdNodeComp;
	}

	@TypeName(typeName = "AudioExtractor", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createAudioExtractor(@Label(label = "Jack Name") String name,
			JNodeFlowPane flowPane) {
		AudioExtractorDigestor ajd = new AudioExtractorDigestor(name, new String[] { "1" }, new Dimension(1920, 1080));
		FlowNode<VideoProcessor> ajdNode = new VideoFlowNode(ajd, null, null);
		jVidNodeComponent<VideoProcessor> ajdNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, ajdNode);

		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"AudioExtractor", name);

		ajdNodeComp.setNodeType(NodeType.Receiver);
		ajdNodeComp.setNodeName("Audio Out: " + name);
		ajdNodeComp.setNodeComponentDescriptor(desc);

		ajd.open();

		return ajdNodeComp;
	}

	@TypeName(typeName = "CombFilter", nodeType = NodeType.Transceiver)
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

	@TypeName(typeName = "Reverb", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createReverbComponent(@Label(label = "Delay") float delay,
			@Label(label = "Decay Factor") float decayFactor, JNodeFlowPane flowPane) {
		BasicReverb cf = new BasicReverb(2);
		DigitalSignalDigestor dsp = new DigitalSignalDigestor(cf, delay, decayFactor);

		FlowNode<VideoProcessor> dspNode = new VideoFlowNode(dsp, null, null);
		jVidNodeComponent<VideoProcessor> dspNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, dspNode);
		dspNodeComp.setNodeName(delay + "ms Reverb");

		dsp.open();

		return dspNodeComp;
	}

	@TypeName(typeName = "Gain", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createGainComponent(@Label(label = "Percentage") float perc,
			JNodeFlowPane flowPane) {
		GainFilter cf = new GainFilter();
		cf.setGain(perc);
		DigitalSignalDigestor dsp = new DigitalSignalDigestor(cf, 0, 0);

		FlowNode<VideoProcessor> dspNode = new VideoFlowNode(dsp, null, null);
		jVidNodeComponent<VideoProcessor> dspNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane, dspNode);
		dspNodeComp.setNodeName("Gain");

		dsp.open();

		return dspNodeComp;
	}

	@TypeName(typeName = "OpenJVID Peripheral", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createOpenJVIDPeripheral(
			@Label(label = "Peripheral") PeripheralDescriptor pd, JNodeFlowPane flowPane) {
		try {
			OpenJVIDPeripheral periph = new OpenJVIDPeripheral(pd);
			FlowNode<VideoProcessor> periphNode = new VideoFlowNode(periph, null, null);
			jVidNodeComponent<VideoProcessor> periphNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
					periphNode);

			jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
					"OpenJVID Peripheral", pd);

			periphNodeComp.setNodeType(NodeType.Transceiver);
			periphNodeComp.setNodeName(pd.getInetAddress().getCanonicalHostName());
			periphNodeComp.setNodeComponentDescriptor(desc);

			periph.open();

			return periphNodeComp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@TypeName(typeName = "OpenJVID Peripheral - Client", isShown = false, nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createOpenJVIDPeripheral(PeripheralDescriptor pd, UUID sessionID,
			int port, JNodeFlowPane flowPane) {
		try {
			OpenJVIDPeripheral periph = new OpenJVIDPeripheral(pd, sessionID, port);
			FlowNode<VideoProcessor> periphNode = new VideoFlowNode(periph, null, null);
			jVidNodeComponent<VideoProcessor> periphNodeComp = new jVidNodeComponent<VideoProcessor>(flowPane,
					periphNode);

			jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
					"OpenJVID Peripheral - Client", pd, sessionID, port);

			periphNodeComp.setNodeType(NodeType.Transceiver);
			periphNodeComp.setNodeName(pd.getInetAddress().getCanonicalHostName());
			periphNodeComp.setNodeComponentDescriptor(desc);

			periph.open();

			return periphNodeComp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@TypeName(typeName = "jVid Scripted Component", nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createScriptedComponent(
			@Label(label = "Script File") String scriptFilePath, @Label(label = "Name") String name,
			JNodeFlowPane flowPane) {

		jVidScriptedComponent comp;
		jVidNodeComponentDescriptor<VideoProcessor> desc;
		if (scriptFilePath.isEmpty()) {
			comp = new jVidScriptedComponent(flowPane);
			desc = new jVidNodeComponentDescriptor<VideoProcessor>("jVid Scripted Component - Script", "", name);
		} else {
			try {
				FileInputStream fin = new FileInputStream(scriptFilePath);
				byte[] script = fin.readAllBytes();
				String scriptString = new String(script);
				fin.close();

				comp = new jVidScriptedComponent(scriptString, flowPane);
				desc = new jVidNodeComponentDescriptor<VideoProcessor>("jVid Scripted Component - Script", scriptString,
						name);

			} catch (Exception e) {
				e.printStackTrace();
				comp = new jVidScriptedComponent(flowPane);
				desc = new jVidNodeComponentDescriptor<VideoProcessor>("jVid Scripted Component - Script", "", name);
			}
		}
		comp.setNodeName(name);
		comp.setNodeComponentDescriptor(desc);
		return comp;
	}

	@TypeName(typeName = "jVid Scripted Component - Script", isShown = false, nodeType = NodeType.Transceiver)
	public static jVidNodeComponent<VideoProcessor> createScriptedWithScriptComponent(
			@Label(label = "Script") String script, @Label(label = "Name") String name, JNodeFlowPane flowPane) {
		jVidScriptedComponent comp;
		jVidNodeComponentDescriptor<VideoProcessor> desc = new jVidNodeComponentDescriptor<VideoProcessor>(
				"jVid Scripted Component - Script", script, name);

		comp = new jVidScriptedComponent(script, flowPane);
		comp.setNodeName(name);
		comp.setNodeComponentDescriptor(desc);

		return comp;
	}

}
