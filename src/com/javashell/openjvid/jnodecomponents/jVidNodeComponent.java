package com.javashell.openjvid.jnodecomponents;

import com.javashell.flow.FlowNode;
import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.video.ControlInterface;

public class jVidNodeComponent<T> extends JNodeComponent {
	private final FlowNode<T> node;
	private boolean hasControlInterface = false;

	public jVidNodeComponent(JNodeFlowPane flow, FlowNode<T> node) {
		super(flow);
		this.node = node;
		if (node.retrieveNodeContents() instanceof ControlInterface) {
			System.out.println("has control interface");
			hasControlInterface = true;
			addNodePoint(new jVidControlNodePoint(flow, this));
		}
	}

	@Override
	public void addOriginLinkage(JNodeComponent origin) throws IncorrectLinkageException {
		if (origin instanceof jVidNodeComponent) {
			jVidNodeComponent originNode = (jVidNodeComponent) origin;
			node.setIngestSourceNode(originNode.getNode());
			originNode.getNode().setEgressDestinationNode(node);
		} else {
			throw new IncorrectLinkageException();
		}
	}

	@Override
	public void addChildLinkage(JNodeComponent child) throws IncorrectLinkageException {
		if (child instanceof jVidNodeComponent) {
			jVidNodeComponent comp = (jVidNodeComponent) child;
			node.setEgressDestinationNode(comp.getNode());
		} else {
			throw new IncorrectLinkageException();
		}
	}

	@Override
	public void removeChildLinkage(JNodeComponent child) {
		node.setEgressDestinationNode(null);
	}

	@Override
	public void removeOriginLinkage(JNodeComponent origin) {
		node.setIngestSourceNode(null);
	}

	public FlowNode<T> getNode() {
		return node;
	}

	private class jVidControlNodePoint extends JNodeComponent.NodePoint {

		public jVidControlNodePoint(JNodeFlowPane flow, JNodeComponent parent) {
			super(flow, parent);
		}

		@Override
		public void addOriginLinkage(JNodeComponent origin) throws IncorrectLinkageException {
			if (origin instanceof jVidNodeComponent) {
				jVidNodeComponent<?> comp = (jVidNodeComponent<?>) origin;
				Object nodeContents = comp.getNode().retrieveNodeContents();
				if (nodeContents instanceof ControlInterface) {
					ControlInterface controlOrigin = (ControlInterface) nodeContents;
					controlOrigin.addSubscriber((ControlInterface) node.retrieveNodeContents());
				}
			}
		}

		@Override
		public void addChildLinkage(JNodeComponent child) throws IncorrectLinkageException {
			if (child instanceof jVidNodeComponent) {
				jVidNodeComponent<?> comp = (jVidNodeComponent<?>) child;
				if (comp.getNode().retrieveNodeContents() instanceof ControlInterface) {
					ControlInterface thisInterface = (ControlInterface) node.retrieveNodeContents();
					((ControlInterface) comp.getNode().retrieveNodeContents()).addSubscriber(thisInterface);
				}
			}
		}

		@Override
		public void removeChildLinkage(JNodeComponent child) {
		}

		@Override
		public void removeOriginLinkage(JNodeComponent origin) {
		}

	}

}
