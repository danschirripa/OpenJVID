package com.javashell.openjvid.lua;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.javashell.flow.FlowController;
import com.javashell.jnodegraph.JNodeFlowPane;
import com.javashell.openjvid.MainFrame;
import com.javashell.openjvid.lua.utilities.LuaUUID;
import com.javashell.video.VideoProcessor;

public class JVIDLuaLibrary {
	private static MainFrame mf;
	private static JNodeFlowPane flowPane;

	public JVIDLuaLibrary(MainFrame mf, JNodeFlowPane flowPane) {
		this.mf = mf;
		this.flowPane = flowPane;
	}

	public enum Lib implements BiConsumer<Environment, LuaObject>, LuaMethod {
		ListNodes {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				var nodes = mf.getNodeComponents();
				LuaObject table = Lua.newTable();

				for (var key : nodes.keySet()) {
					LuaObject nodeTable = Lua.newTable();

					var node = nodes.get(key);

					nodeTable.rawSet("Name", node.getNodeName());
					nodeTable.rawSet("UUID", new LuaUUID(key));
					nodeTable.rawSet("Type", node.getNodeType().name());
					nodeTable.rawSet("Class", node.getNode().retrieveNodeContents().getClass().toString());

					table.rawSet(node.getNodeName(), nodeTable);
				}
				return table;
			}
		},
		ListLinkages {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				var linkages = flowPane.getLinkages();

				LuaObject table = Lua.newTable();

				for (var key : linkages.keySet()) {
					LuaObject linkageTable = Lua.newTable();
					int index = 0;
					for (var link : linkages.get(key)) {
						linkageTable.rawSet(index, link.getNode().getUUID().toString());
					}
					table.rawSet(key.getUUID().toString(), linkageTable);
				}

				return table;
			}
		},
		BreakLinkage {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				Lua.checkArgs("BreakLinkage", args, LuaType.USERDATA, LuaType.USERDATA);

				LuaUUID uuid = (LuaUUID) args[0];
				LuaUUID uuid2 = (LuaUUID) args[1];

				var originComp = mf.getNodeComponents().get(uuid.getUUID());
				if (originComp == null)
					return Lua.FALSE;

				var nodeComp = mf.getNodeComponents().get(uuid2.getUUID());
				if (nodeComp == null)
					return Lua.FALSE;

				var linkages = flowPane.getLinkages().get(originComp);
				if (linkages == null)
					return Lua.FALSE;

				for (var linkage : linkages) {
					if (linkage.getNode() == nodeComp) {
						flowPane.removeLinkage(linkage);
						return Lua.TRUE;
					}
				}

				return Lua.FALSE;
			}
		},
		CreateLinkage {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				Lua.checkArgs("CreateLinkage", args, LuaType.USERDATA, LuaType.USERDATA);

				LuaUUID uuid = (LuaUUID) args[0];
				LuaUUID uuid2 = (LuaUUID) args[1];

				var originComp = mf.getNodeComponents().get(uuid.getUUID());
				if (originComp == null)
					return Lua.FALSE;

				var nodeComp = mf.getNodeComponents().get(uuid2.getUUID());
				if (nodeComp == null)
					return Lua.FALSE;

				try {
					flowPane.createLinkage(originComp, nodeComp);
				} catch (Exception e) {
					return Lua.FALSE;
				}

				return Lua.TRUE;
			}
		},
		GetNodeProperties {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				Lua.checkArgs("GetNodeProperties", args, LuaType.USERDATA);

				LuaUUID uuid = (LuaUUID) args[0];
				var comp = mf.getNodeComponents().get(uuid.getUUID());
				var propertyTable = ((VideoProcessor) comp.getNode().retrieveNodeContents()).getPropertyTable();

				LuaObject table = Lua.newTable();
				for (var key : propertyTable.keySet()) {
					var value = propertyTable.get(key);
					try {
						table.rawSet(key, Lua.newLuaObject(value));
					} catch (LuaException e) {
						table.rawSet(key, value.toString());
					}
				}

				return table;
			}
		},
		SetNodeProperties {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				Lua.checkArgs("SetNodeProperties", args, LuaType.USERDATA, LuaType.TABLE);

				LuaUUID uuid = (LuaUUID) args[0];
				var comp = mf.getNodeComponents().get(uuid.getUUID());

				HashMap<String, Object> propertyTable = new HashMap<String, Object>();

				var properties = args[1].getEntries();
				for (Entry<LuaObject, LuaObject> entry : properties) {
					propertyTable.put(entry.getKey().getString(), entry.getKey().getUserdata());
				}

				((VideoProcessor) comp.getNode().retrieveNodeContents()).setPropertyTable(propertyTable);

				return Lua.TRUE;
			}
		},
		Pause {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				FlowController.pauseFlow();
				return Lua.TRUE;
			}
		},
		Resume {
			public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
				FlowController.resumeFlow();
				return Lua.TRUE;
			}
		};

		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return null;
		}

		@Override
		public void accept(Environment t, LuaObject table) {
			String name = toString();
			if (name != null && !name.trim().isEmpty())
				table.rawSet(name, Lua.newMethod(this));
		}
	}
}
