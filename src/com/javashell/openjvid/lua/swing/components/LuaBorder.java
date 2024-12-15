package com.javashell.openjvid.lua.swing.components;

import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.hk.lua.Lua;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;

public class LuaBorder extends LuaUserdata {
	private Border border;
	private static final LuaObject luaBorderMetatable = Lua.newTable();

	public LuaBorder(Border border) {
		this.border = border;
		metatable = luaBorderMetatable;
	}

	public Border getBorder() {
		return border;
	}

	public void setBorderTitle(String title) {
		if (border instanceof TitledBorder) {
			if (title.equals(""))
				border = ((TitledBorder) border).getBorder();
			else
				((TitledBorder) border).setTitle(title);
			return;
		}
		border = BorderFactory.createTitledBorder(border, title);
	}

	@Override
	public Object getUserdata() {
		return this;
	}

	@Override
	public String name() {
		return "BORDER";
	}

	static {
		luaBorderMetatable.rawSet("__name", "BORDER");
		luaBorderMetatable.rawSet("__index", luaBorderMetatable);

		LuaObject setTitleFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetTitle", args, LuaType.USERDATA, LuaType.STRING);
				LuaBorder lb = (LuaBorder) args[0];
				lb.setBorderTitle(args[1].getString());
			}

		});

		luaBorderMetatable.rawSet("SetTitle", setTitleFunction);
	}

}
