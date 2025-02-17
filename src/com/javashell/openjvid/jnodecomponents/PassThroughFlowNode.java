package com.javashell.openjvid.jnodecomponents;

import com.javashell.flow.VideoFlowNode;

public class PassThroughFlowNode extends VideoFlowNode {

	public PassThroughFlowNode() {
		super(new PassThroughDigestor(), null, null);
	}

}
