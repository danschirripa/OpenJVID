package com.javashell.openjvid.lua.swing.components;

import java.util.function.Consumer;

import javax.swing.JLabel;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.LuaComponent;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public class LuaLabel extends LuaComponent {
	private JLabel label;

	public LuaLabel(LuaInterpreter interp) {
		super(interp);
		appendMetatable();
		label = new JLabel();
		setJComponent(label);
	}

	private void appendMetatable() {
		LuaObject setLabelTextFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetText", args, LuaType.USERDATA, LuaType.STRING);
				LuaLabel ll = (LuaLabel) args[0];
				ll.label.setText(args[1].getString());
			}

		});

		LuaObject getLabelTextFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetText", args, LuaType.USERDATA);
				LuaLabel ll = (LuaLabel) args[0];
				return Lua.newString(ll.label.getText());
			}

		});

		metatable.rawSet("SetText", setLabelTextFunction);
		metatable.rawSet("GetText", getLabelTextFunction);
	}

}
