package com.javashell.openjvid.jnodecomponents;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.ui.MatrixDigestorCrosspointDialog;
import com.javashell.video.VideoProcessor;
import com.javashell.video.digestors.MatrixDigestor;

@SuppressWarnings("unchecked")
public class jVidMatrixNodeComponent extends jVidNodeComponent<VideoProcessor> {
	private static final long serialVersionUID = 3800957082229059177L;
	MatrixDigestorCrosspointDialog crosspointDialog;

	public jVidMatrixNodeComponent(JNodeFlowPane flow, MatrixDigestor node) {
		super(flow, node);

		crosspointDialog = new MatrixDigestorCrosspointDialog();
		crosspointDialog.setMatrix(node);
		setComponentPopupMenu(crosspointDialog.getCrosspointJMenu());

	}

	public jVidMatrixNodeComponent(JNodeFlowPane flow, MatrixDigestor node,
			HashMap<jVidNodeComponent<VideoProcessor>, HashSet<jVidNodeComponent<VideoProcessor>>> crosspoints) {
		this(flow, node);
		for (var source : crosspoints.keySet()) {
			for (var sink : crosspoints.get(source)) {
				node.createCrossPoint(source.getNode(), sink.getNode());
			}
		}
	}

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

	@Override
	public void preSave() {
		Hashtable<UUID, HashSet<UUID>> uuidCrosspoints = new Hashtable<UUID, HashSet<UUID>>();
		var crosspoints = crosspointDialog.getCrosspoints();
		for (var source : crosspoints.keySet()) {
			final HashSet<UUID> tieUUIDS = new HashSet<UUID>();
			var ties = crosspoints.get(source);
			for (var sink : ties) {
				tieUUIDS.add(sink.getUUID());
			}
			uuidCrosspoints.put(source.getUUID(), tieUUIDS);
		}

		Object[] initArgs = Arrays.copyOf(this.getNodeComponentDescriptor().getInitArgs(),
				this.getNodeComponentDescriptor().getInitArgs().length + 1);
		initArgs[initArgs.length - 1] = uuidCrosspoints;
		this.getNodeComponentDescriptor().setInitArgs(initArgs);
	}

}
