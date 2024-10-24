package com.javashell.openjvid.jnodecomponents;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.javashell.audio.AudioProcessor;
import com.javashell.flow.FlowNode;
import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.configuration.jVidNodeComponentDescriptor;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.video.ControlInterface;

public class jVidNodeComponent<T> extends JNodeComponent {
	private final FlowNode<T> node;
	private boolean hasControlInterface = false, hasAudioProcessor = false;
	private jVidNodeComponentDescriptor<T> desc;

	public jVidNodeComponent(JNodeFlowPane flow, FlowNode<T> node) {
		super(flow);
		this.node = node;
		if (node.retrieveNodeContents() instanceof ControlInterface) {
			System.out.println("has control interface");
			hasControlInterface = true;
			addNodePoint(new jVidControlNodePoint(flow, this));
		}
		if (node.retrieveNodeContents() instanceof AudioProcessor) {
			System.out.println("has audio processor");
			hasAudioProcessor = true;
			addNodePoint(new jVidAudioNodePoint(flow, this));
		}
	}

	public void setNodeComponentDescriptor(jVidNodeComponentDescriptor<T> desc) {
		this.desc = desc;
	}

	public jVidNodeComponentDescriptor<T> getNodeComponentDescriptor() {
		return desc;
	}

	public void preSave() {

	}

	private JMenu createPopupMenu(JNodeFlowPane ha) {
		JMenu editMenu = new JMenu();

		JMenuItem editDeleteSelected = new JMenuItem("Delete");
		JMenuItem editSelectedProperties = new JMenuItem("Edit");
		editMenu.add(editDeleteSelected);
		editMenu.add(editSelectedProperties);

		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case MainFrameActionHandler.DELETE:
					break;
				case MainFrameActionHandler.EDITPROPS:
					break;
				}
			}
		};

		editDeleteSelected.setActionCommand(MainFrameActionHandler.DELETE);
		editDeleteSelected.addActionListener(menuListener);

		editSelectedProperties.setActionCommand(MainFrameActionHandler.EDITPROPS);
		editSelectedProperties.addActionListener(menuListener);
		return editMenu;
	}

	@Override
	public void addOriginLinkage(JNodeComponent origin, boolean cascade) throws IncorrectLinkageException {
		if (origin instanceof jVidNodeComponent) {
			jVidNodeComponent originNode = (jVidNodeComponent) origin;

			if (cascade)
				origin.addChildLinkage(this, false);

			node.setIngestSourceNode(originNode.getNode());
			originNode.getNode().setEgressDestinationNode(node);
		} else {
			throw new IncorrectLinkageException();
		}
	}

	@Override
	public void addChildLinkage(JNodeComponent child, boolean cascade) throws IncorrectLinkageException {
		if (child instanceof jVidNodeComponent) {
			jVidNodeComponent comp = (jVidNodeComponent) child;

			if (cascade)
				child.addOriginLinkage(this, false);
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
			setColor(Color.GRAY);
		}

		@Override
		public void addOriginLinkage(JNodeComponent origin, boolean cascade) throws IncorrectLinkageException {
			if (origin instanceof jVidNodeComponent<?>.jVidControlNodePoint) {
				jVidNodeComponent<?>.jVidControlNodePoint comp = (jVidNodeComponent<?>.jVidControlNodePoint) origin;
				Object nodeContents = ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents();
				if (nodeContents instanceof ControlInterface) {
					ControlInterface controlOrigin = (ControlInterface) nodeContents;
					controlOrigin.addSubscriber((ControlInterface) node.retrieveNodeContents());
				}
			}
		}

		@Override
		public void addChildLinkage(JNodeComponent child, boolean cascade) throws IncorrectLinkageException {
			if (child instanceof jVidNodeComponent<?>.jVidControlNodePoint) {
				jVidNodeComponent<?>.jVidControlNodePoint comp = (jVidNodeComponent<?>.jVidControlNodePoint) child;
				if (((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents() instanceof ControlInterface) {
					ControlInterface thisInterface = (ControlInterface) node.retrieveNodeContents();
					((ControlInterface) ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
							.retrieveNodeContents()).addSubscriber(thisInterface);
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

	private class jVidAudioNodePoint extends JNodeComponent.NodePoint {

		public jVidAudioNodePoint(JNodeFlowPane flow, JNodeComponent parent) {
			super(flow, parent);
			setColor(Color.CYAN);
		}

		@Override
		public void addOriginLinkage(JNodeComponent origin, boolean cascade) throws IncorrectLinkageException {
			if (origin instanceof jVidNodeComponent<?>.jVidAudioNodePoint) {
				jVidNodeComponent<?>.jVidAudioNodePoint comp = (jVidNodeComponent<?>.jVidAudioNodePoint) origin;
				Object nodeContents = ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents();
				if (nodeContents instanceof AudioProcessor) {
					AudioProcessor audioOrigin = (AudioProcessor) nodeContents;
					audioOrigin.addSubscriber((AudioProcessor) node.retrieveNodeContents());
				}
			}
		}

		@Override
		public void addChildLinkage(JNodeComponent child, boolean cascade) throws IncorrectLinkageException {
			if (child instanceof jVidNodeComponent<?>.jVidAudioNodePoint) {
				jVidNodeComponent<?>.jVidAudioNodePoint comp = (jVidNodeComponent<?>.jVidAudioNodePoint) child;
				if (((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents() instanceof AudioProcessor) {
					AudioProcessor thisInterface = (AudioProcessor) node.retrieveNodeContents();
					((AudioProcessor) ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
							.retrieveNodeContents()).addSubscriber(thisInterface);
				}
			}
		}

		@Override
		public void removeChildLinkage(JNodeComponent child) {
			if (child instanceof jVidNodeComponent<?>.jVidAudioNodePoint) {
				jVidNodeComponent<?>.jVidAudioNodePoint comp = (jVidNodeComponent<?>.jVidAudioNodePoint) child;
				if (((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents() instanceof AudioProcessor) {
					AudioProcessor thisInterface = (AudioProcessor) node.retrieveNodeContents();
					((AudioProcessor) ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
							.retrieveNodeContents()).removeSubscriber(thisInterface);
				}
			}
		}

		@Override
		public void removeOriginLinkage(JNodeComponent origin) {
			if (origin instanceof jVidNodeComponent<?>.jVidAudioNodePoint) {
				jVidNodeComponent<?>.jVidAudioNodePoint comp = (jVidNodeComponent<?>.jVidAudioNodePoint) origin;
				Object nodeContents = ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents();
				if (nodeContents instanceof AudioProcessor) {
					AudioProcessor audioOrigin = (AudioProcessor) nodeContents;
					audioOrigin.removeSubscriber((AudioProcessor) node.retrieveNodeContents());
				}
			}
		}

	}

}
