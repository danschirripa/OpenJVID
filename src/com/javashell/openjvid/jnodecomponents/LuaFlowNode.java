package com.javashell.openjvid.jnodecomponents;

import com.javashell.flow.VideoFlowNode;

public class LuaFlowNode extends VideoFlowNode {

	public LuaFlowNode() {
		super(new LuaDigestor(), null, null);
	}

	public void setScriptedComponent(jVidScriptedComponent comp) {
		((LuaDigestor) this.retrieveNodeContents()).setScriptedComponent(comp);
	}

}
