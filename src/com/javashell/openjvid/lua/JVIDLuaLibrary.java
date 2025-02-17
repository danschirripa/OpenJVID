package com.javashell.openjvid.lua;

import java.util.function.BiConsumer;

import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.LuaObject;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;

public enum JVIDLuaLibrary implements BiConsumer<Environment, LuaObject>, LuaMethod {
	;

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
