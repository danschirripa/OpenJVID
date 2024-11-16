package com.javashell.openjvid.jnodecomponents;

import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.video.VideoProcessor;

public class EmulatedComponent extends jVidNodeComponent<VideoProcessor> {

	public EmulatedComponent(JNodeFlowPane flow) {
		super(flow, new EmulatedFlowNode());
	}

	private static class EmulatedFlowNode extends VideoFlowNode {

		public EmulatedFlowNode() {
			super(null, null, null);
		}

	}

}
