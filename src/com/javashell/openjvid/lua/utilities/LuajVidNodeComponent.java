package com.javashell.openjvid.lua.utilities;

import java.util.function.Consumer;

import com.hk.lua.Lua;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.jnodecomponents.jVidScriptedComponent;
import com.javashell.openjvid.lua.swing.LuaComponent;
import com.javashell.openjvid.lua.swing.LuaComponent.LuaGraphicsWrapper;

public class LuajVidNodeComponent extends LuaUserdata {
	private static final LuaObject luajVidNodeMetatable = Lua.newTable();
	private jVidScriptedComponent node;
	private LuaInterpreter interp;

	public LuajVidNodeComponent(LuaInterpreter interp, jVidScriptedComponent node) {
		this.node = node;
		this.interp = interp;
		metatable = luajVidNodeMetatable;
	}

	@Override
	public String name() {
		return "JVIDNODE";
	}

	@Override
	public Object getUserdata() {
		return this;
	}

	public void setProcessFrameCallback(LuaObject func) {
		node.setProcessFrameLuaCallback(func);
	}

	public void setControlCallback(LuaObject func) {
		node.setControlCallback(func);
	}

	static {
		luajVidNodeMetatable.rawSet("__name", Lua.newString("JVIDNODE"));
		luajVidNodeMetatable.rawSet("__index", luajVidNodeMetatable);

		LuaObject setFrameCallback = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetFrameCallback", args, LuaType.USERDATA, LuaType.ANY);
				var comp = (LuajVidNodeComponent) args[0];
				comp.setProcessFrameCallback(args[1]);
			}

		});

		LuaObject setControlCallback = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetControlCallback", args, LuaType.USERDATA, LuaType.ANY);
				var comp = (LuajVidNodeComponent) args[0];
				comp.setControlCallback(args[1]);
			}

		});

		LuaObject fireControl = Lua.newFunc(new Consumer<LuaObject[]>() {

			public void accept(LuaObject[] args) {
				Lua.checkArgs("FireControl", args, LuaType.USERDATA, LuaType.USERDATA);
				var comp = (LuajVidNodeComponent) args[0];
				comp.node.fireControl(args[1].getUserdata());
			}

		});

		LuaObject getNodeName = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetNodeName", args, LuaType.USERDATA);

				var comp = (LuajVidNodeComponent) args[0];
				return Lua.newString(comp.node.getNodeName());
			}

		});

		LuaObject getNodeType = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetNodeType", args, LuaType.USERDATA);

				var comp = (LuajVidNodeComponent) args[0];
				return Lua.newString(comp.node.getNodeType().name());
			}

		});

		LuaObject getFrameSize = Lua.newMethod(new LuaMethod() {
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetFrameSize", args, LuaType.USERDATA);

				var comp = (LuajVidNodeComponent) args[0];
				LuaObject dimensionTable = Lua.newTable();
				dimensionTable.rawSet(0, comp.node.getWidth());
				dimensionTable.rawSet(1, comp.node.getHeight());
				return dimensionTable;
			}
		});

		luajVidNodeMetatable.rawSet("SetFrameCallback", setFrameCallback);
		luajVidNodeMetatable.rawSet("SetControlCallback", setControlCallback);
		luajVidNodeMetatable.rawSet("FireControl", fireControl);
		luajVidNodeMetatable.rawSet("GetNodeName", getNodeName);
		luajVidNodeMetatable.rawSet("GetNodeType", getNodeType);
		luajVidNodeMetatable.rawSet("GetFrameSize", getFrameSize);
	}

}
