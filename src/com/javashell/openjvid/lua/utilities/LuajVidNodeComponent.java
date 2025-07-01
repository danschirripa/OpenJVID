package com.javashell.openjvid.lua.utilities;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.Consumer;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;
import com.javashell.flow.VideoFlowNode;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.jnodecomponents.jVidNodeComponent.jVidControlNodePoint;
import com.javashell.openjvid.jnodecomponents.jVidScriptedComponent;
import com.javashell.video.ControlInterface;
import com.javashell.video.VideoDigestor;
import com.javashell.video.VideoProcessor;

public class LuajVidNodeComponent extends LuaUserdata {
	private static final LuaObject luajVidNodeMetatable = Lua.newTable();
	private jVidScriptedComponent node;
	private LuaInterpreter interp;
	private JNodeFlowPane flowPane;

	public LuajVidNodeComponent(LuaInterpreter interp, jVidScriptedComponent node, JNodeFlowPane flowPane) {
		this.node = node;
		this.interp = interp;
		this.flowPane = flowPane;
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
				Lua.checkArgs("FireControl", args, LuaType.USERDATA, LuaType.ANY);
				var comp = (LuajVidNodeComponent) args[0];

				var argument = args[1];
				Object toSend = null;

				switch (argument.type()) {
				case LuaType.STRING:
					toSend = argument.getString();
					break;
				case LuaType.NUMBER:
					toSend = argument.getInt();
					break;
				case LuaType.BOOLEAN:
					toSend = argument.getBoolean();
					break;
				default:
					toSend = argument;
					break;

				}

				comp.node.fireControl(toSend);
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

		LuaObject addControlPoint = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("AddControlPoint", args, LuaType.USERDATA, LuaType.STRING, LuaType.STRING);

				var comp = (LuajVidNodeComponent) args[0];
				var nameString = args[1].getString();
				var typeString = args[2].getString();

				Class<?> type = Object.class;

				if (typeString.equals("BOOLEAN")) {
					type = Boolean.class;
				} else if (typeString.equals("INTEGER")) {
					type = Integer.class;
				} else if (typeString.equals("STRING")) {
					type = String.class;
				}

				for (var point : comp.node.getNodePoints())
					if (point.getNodeName().equals(nameString)) {
						return Lua.NIL;
					}

				var newPoint = new jVidControlNodePoint(comp.flowPane, comp.node, type,
						new LuaControlNode(new VideoDigestor(new Dimension(1920, 1080)) {

							@Override
							public HashMap<String, Object> getPropertyTable() {
								return null;
							}

							@Override
							public void setPropertyTable(HashMap<String, Object> table) {

							}

							@Override
							public void setProperty(String key, Object value) {
							}

							@Override
							public Object getProperty(String key) {
								return null;
							}

							@Override
							public BufferedImage processFrame(BufferedImage frame) {
								return frame;
							}

							@Override
							public boolean open() {
								return true;
							}

							@Override
							public boolean close() {
								return true;
							}

						}));
				newPoint.setNodeName(nameString);

				comp.node.add(newPoint);

				return Lua.TRUE;
			}

		});

		LuaObject getControlPoints = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetControlPoints", args, LuaType.USERDATA);

				var comp = (LuajVidNodeComponent) args[0];
				var points = comp.node.getNodePoints();

				LuaObject pointTable = Lua.newTable();

				for (var point : points) {
					if (point instanceof jVidControlNodePoint) {
						pointTable.rawSet(point.getNodeName(), new LuaUUID(point.getUUID()));
					}
				}

				return pointTable;
			}

		});

		LuaObject setControlPointCallback = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("SetControlPointCallback", args, LuaType.USERDATA, LuaType.STRING, LuaType.USERDATA);

				var comp = (LuajVidNodeComponent) args[0];
				var points = comp.node.getNodePoints();

				var nodeName = args[1].getString();

				for (var point : points) {
					if (point instanceof jVidControlNodePoint && point.getNodeName().equals(nodeName)) {
						var cPoint = (jVidControlNodePoint) point;
						if (cPoint.getNode() instanceof LuaControlNode) {
							var node = (LuaControlNode) cPoint.getNode();
							node.setControlCallback(args[2]);
						}
					}
				}

				return Lua.TRUE;
			}

		});

		LuaObject fireControlFromControlPoint = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("FireControlFromPoint", args, LuaType.USERDATA, LuaType.STRING, LuaType.ANY);

				var comp = (LuajVidNodeComponent) args[0];
				var points = comp.node.getNodePoints();

				var nodeName = args[1].getString();

				var argument = args[2];
				Object toSend = null;

				switch (argument.type()) {
				case LuaType.STRING:
					toSend = argument.getString();
					break;
				case LuaType.NUMBER:
					toSend = argument.getInt();
					break;
				case LuaType.BOOLEAN:
					toSend = argument.getBoolean();
					break;
				default:
					toSend = argument;
					break;

				}

				for (var point : points) {
					if (point instanceof jVidControlNodePoint && point.getNodeName().equals(nodeName)) {
						var cPoint = (jVidControlNodePoint) point;
						if (cPoint.getNode() instanceof LuaControlNode) {
							var node = (LuaControlNode) cPoint.getNode();
							node.fireControl(toSend);
						}
					}
				}

				return Lua.TRUE;
			}

		});

		luajVidNodeMetatable.rawSet("SetFrameCallback", setFrameCallback);
		luajVidNodeMetatable.rawSet("SetControlCallback", setControlCallback);
		luajVidNodeMetatable.rawSet("FireControl", fireControl);
		luajVidNodeMetatable.rawSet("GetNodeName", getNodeName);
		luajVidNodeMetatable.rawSet("GetNodeType", getNodeType);
		luajVidNodeMetatable.rawSet("GetFrameSize", getFrameSize);
		luajVidNodeMetatable.rawSet("GetControlPoints", getControlPoints);
		luajVidNodeMetatable.rawSet("AddControlPoint", addControlPoint);
		luajVidNodeMetatable.rawSet("SetControlPointCallback", setControlPointCallback);
		luajVidNodeMetatable.rawSet("FireControlFromPoint", fireControlFromControlPoint);

		luajVidNodeMetatable.rawSet("BOOLEAN_TYPE", "BOOLEAN");
		luajVidNodeMetatable.rawSet("INTEGER_TYPE", "INTEGER");
		luajVidNodeMetatable.rawSet("STRING_TYPE", "STRING");
	}

	private static class LuaControlNode extends VideoFlowNode implements ControlInterface {

		public LuaControlNode(VideoProcessor content) {
			super(content, null, null);
		}

		@Override
		public boolean addSubscriber(ControlInterface cf) {
			return false;
		}

		@Override
		public void processControl(Object obj) {
		}

		@Override
		public void removeControlEgressor(ControlInterface cf) {
		}

		public void setControlCallback(LuaObject data) {

		}

		public void fireControl(Object obj) {

		}

	}

}
