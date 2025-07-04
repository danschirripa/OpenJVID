package com.javashell.openjvid.jnodecomponents;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.javashell.audio.AudioProcessor;
import com.javashell.flow.FlowNode;
import com.javashell.jnodegraph.JNodeComponent;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.jnodegraph.NodeType;
import com.javashell.jnodegraph.exceptions.IncorrectLinkageException;
import com.javashell.openjvid.configuration.jVidNodeComponentDescriptor;
import com.javashell.openjvid.handlers.MainFrameActionHandler;
import com.javashell.openjvid.ui.IconManager;
import com.javashell.video.ControlInterface;

public class jVidNodeComponent<T> extends JNodeComponent {
	private final FlowNode<T> node;
	private jVidNodeComponentDescriptor<T> desc;

	private static final HashMap<NodeType, Image> defaultIcons = new HashMap<NodeType, Image>();

	static {
		defaultIcons.put(NodeType.Receiver, IconManager.getSVGIcon("Transfer_long_left.svg", 100, 100).getImage());
		defaultIcons.put(NodeType.Transceiver, IconManager.getSVGIcon("Transger.svg", 100, 100).getImage());
		defaultIcons.put(NodeType.Transmitter, IconManager.getSVGIcon("Transfer_long_right.svg", 100, 100).getImage());
	}

	public jVidNodeComponent(JNodeFlowPane flow, FlowNode<T> node) {
		super(flow, FlowNode.class);
		this.node = node;
		if (node.retrieveNodeContents() instanceof ControlInterface) {
			System.out.println("has control interface");
			addNodePoint(new jVidControlNodePoint(flow, this, node));
		}
		if (node.retrieveNodeContents() instanceof AudioProcessor) {
			System.out.println("has audio processor");
			AudioProcessor audioProc = (AudioProcessor) node.retrieveNodeContents();
			for (int i = 0; i < audioProc.getAudioChannels(); i++)
				addNodePoint(new jVidAudioNodePoint(flow, this, i + 1));
		}
		this.setComponentPopupMenu(createPopupMenu(flow));
		this.setIcon(defaultIcons.get(getNodeType()));
	}

	@Override
	public void setNodeType(NodeType type) {
		super.setNodeType(type);
		this.setIcon(defaultIcons.get(type));
	}

	public void preSave() {

	}

	public void setNodeComponentDescriptor(jVidNodeComponentDescriptor<T> desc) {
		this.desc = desc;
	}

	public jVidNodeComponentDescriptor<T> getNodeComponentDescriptor() {
		return desc;
	}

	private JPopupMenu createPopupMenu(JNodeFlowPane ha) {
		JPopupMenu editMenu = new JPopupMenu();

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

	public static class jVidControlNodePoint extends JNodeComponent.NodePoint {
		private FlowNode<?> node;

		public jVidControlNodePoint(JNodeFlowPane flow, JNodeComponent parent, FlowNode<?> node) {
			super(flow, parent, jVidControlNodePoint.class);
			this.setNodeType(parent.getNodeType());
			this.node = node;
			setColor(Color.GRAY);
		}

		public jVidControlNodePoint(JNodeFlowPane flow, JNodeComponent parent, Class<?> objType, FlowNode<?> node) {
			super(flow, parent, objType);
			this.setNodeType(parent.getNodeType());
			this.node = node;
			setColor(Color.GRAY);
		}

		@Override
		public void addOriginLinkage(JNodeComponent origin, boolean cascade) throws IncorrectLinkageException {
			if (origin instanceof jVidNodeComponent.jVidControlNodePoint) {
				jVidNodeComponent.jVidControlNodePoint comp = (jVidNodeComponent.jVidControlNodePoint) origin;
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
			if (child instanceof jVidNodeComponent.jVidControlNodePoint) {
				jVidNodeComponent.jVidControlNodePoint comp = (jVidNodeComponent.jVidControlNodePoint) child;
				if (((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents() instanceof ControlInterface) {
					ControlInterface thisInterface = (ControlInterface) node.retrieveNodeContents();
					Object nodeContents = ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
							.retrieveNodeContents();
					if (nodeContents instanceof ControlInterface) {
						thisInterface.addSubscriber((ControlInterface) nodeContents);
					}
				}
			}
		}

		@Override
		public void removeChildLinkage(JNodeComponent child) {

		}

		@Override
		public void removeOriginLinkage(JNodeComponent origin) {
		}

		public FlowNode<?> getNode() {
			return node;
		}

	}

	public class jVidAudioNodePoint extends JNodeComponent.NodePoint {
		private int channel = 1;
		private JNodeComponent parent;

		public jVidAudioNodePoint(JNodeFlowPane flow, JNodeComponent parent, int channel) {
			super(flow, parent, jVidAudioNodePoint.class);
			setColor(Color.CYAN);
			this.channel = channel;
			this.parent = parent;
		}

		@Override
		public NodeType getNodeType() {
			return parent.getNodeType();
		}

		public int getChannel() {
			return channel;
		}

		// Add origin linkage, "this" node is the child in this scenario
		@Override
		public void addOriginLinkage(JNodeComponent origin, boolean cascade) throws IncorrectLinkageException {
			if (origin instanceof jVidNodeComponent<?>.jVidAudioNodePoint) {
				jVidNodeComponent<?>.jVidAudioNodePoint comp = (jVidNodeComponent<?>.jVidAudioNodePoint) origin;
				AudioProcessor thisContents = (AudioProcessor) node.retrieveNodeContents();
				Object nodeContents = ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents();

				if (nodeContents instanceof AudioProcessor) {
					AudioProcessor audioOrigin = (AudioProcessor) nodeContents;
					audioOrigin.addSubscriber(thisContents, channel, comp.channel);
					thisContents.addSubscription(audioOrigin, channel, comp.channel);
				}
			}
		}

		// Add child linkage, "this" node is the origin in this scenario
		@Override
		public void addChildLinkage(JNodeComponent child, boolean cascade) throws IncorrectLinkageException {
			if (child instanceof jVidNodeComponent<?>.jVidAudioNodePoint) {
				jVidNodeComponent<?>.jVidAudioNodePoint comp = (jVidNodeComponent<?>.jVidAudioNodePoint) child;
				Object childNode = ((jVidNodeComponent<?>) comp.getParentNodeComponent()).getNode()
						.retrieveNodeContents();
				if (childNode instanceof AudioProcessor) {
					AudioProcessor audioOrigin = (AudioProcessor) node.retrieveNodeContents();
					audioOrigin.addSubscriber((AudioProcessor) childNode, comp.channel, channel);
					((AudioProcessor) childNode).addSubscription(audioOrigin, comp.channel, channel);
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
							.retrieveNodeContents()).removeSubscriber(thisInterface, channel, comp.channel);
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
					audioOrigin.removeSubscriber((AudioProcessor) node.retrieveNodeContents(), comp.channel,
							this.channel);
				}
			}
		}

	}

}
