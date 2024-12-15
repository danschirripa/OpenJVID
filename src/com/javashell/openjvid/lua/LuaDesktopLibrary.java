package com.javashell.openjvid.lua;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.swing.LuaFrame;
import com.javashell.openjvid.lua.swing.LuaImageWrapper;
import com.javashell.openjvid.lua.swing.components.LuaBorder;
import com.javashell.openjvid.lua.swing.components.LuaCheckBox;
import com.javashell.openjvid.lua.swing.components.LuaComboBox;
import com.javashell.openjvid.lua.swing.components.LuaLabel;
import com.javashell.openjvid.lua.swing.components.LuaTextArea;
import com.javashell.openjvid.lua.swing.components.LuaTextField;
import com.javashell.openjvid.lua.swing.components.LuaToggleButton;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public enum LuaDesktopLibrary implements BiConsumer<Environment, LuaObject>, LuaMethod {
	CreateComponent {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaComponent(interp);
		}
	},
	CreateFrame {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaFrame(interp);
		}
	},
	CreateToggleButton {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaToggleButton(interp);
		}
	},
	CreateCheckBox {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaCheckBox(interp);
		}
	},
	CreateComboBox {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			Lua.checkArgs("CreateComboBox", args, LuaType.TABLE);
			final Set<Entry<LuaObject, LuaObject>> entries = args[0].getEntries();
			String[] newOptions = new String[entries.size()];
			final Iterator<Entry<LuaObject, LuaObject>> iter = entries.iterator();
			int i = 0;
			while (iter.hasNext()) {
				Entry<LuaObject, LuaObject> entry = iter.next();
				newOptions[i] = entry.getValue().getString();
				i++;
			}
			return new LuaComboBox(interp, newOptions);
		}
	},
	CreateLabel {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaLabel(interp);
		}
	},
	CreateTextField {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaTextField(interp);
		}
	},
	CreateTextArea {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaTextArea(interp);
		}
	},
	CreateLineBorder {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			Lua.checkArgs("CreateLineBorder", args, LuaType.STRING);

			Color color = null;
			try {
				Field colorField = Class.forName("java.awt.Color").getField(args[0].getString());
				color = (Color) colorField.get(null);
			} catch (Exception e) {
				color = Color.BLACK;
			}

			return new LuaBorder(BorderFactory.createLineBorder(color));
		}
	},
	CreateLoweredEtchedBorder {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		}
	},
	CreateRaisedEtchedBorder {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
	},
	CreateLoweredBevelBorder {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		}
	},
	CreateRaisedBevelBorder {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		}
	},
	GetImageFromBase64 {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			if (args[0].type() != LuaType.STRING)
				Lua.badArgument(0, "GetImageFromBase64", "Base64 Encoded String Expected");

			byte[] imageBytes = Base64.getDecoder().decode(args[0].getString().getBytes());

			try {
				return new LuaImageWrapper(ImageIO.read(new ByteArrayInputStream(imageBytes)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Lua.NIL;
		}
	},
	GetImageFromURL {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			if (args[0].type() != LuaType.STRING)
				Lua.badArgument(0, "GetImageFromURL", "URL String Expected");

			try {
				URL imageUrl = new URL(args[0].getString());
				return new LuaImageWrapper(ImageIO.read(imageUrl));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Lua.NIL;
		}
	},
	GetScreenCenter {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			LuaObject dimensionTable = Lua.newTable();
			Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
			dimensionTable.rawSet(0, center.x);
			dimensionTable.rawSet(1, center.y);
			return dimensionTable;
		}
	};

	@Override
	public LuaObject call(LuaInterpreter arg0, LuaObject[] arg1) {
		return null;
	}

	@Override
	public void accept(Environment t, LuaObject table) {
		String name = toString();
		if (name != null && !name.trim().isEmpty())
			table.rawSet(name, Lua.newMethod(this));
	}

}
