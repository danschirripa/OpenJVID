package com.javashell.openjvid.lua;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.javashell.openjvid.lua.utilities.LuaUUID;

public enum JavashellGenericLuaLibrary implements BiConsumer<Environment, LuaObject>, LuaMethod {

	RandomUUID {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			return new LuaUUID();
		}
	},
	UUIDFromString {
		public LuaObject call(LuaInterpreter interp, LuaObject[] args) {
			Lua.checkArgs("UUID", args, LuaType.STRING);
			return new LuaUUID(UUID.fromString(args[0].getString()));
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

	public static void updateCompletions(DefaultCompletionProvider cp) {

		cp.addCompletion(new BasicCompletion(cp, "Generic.RandomUUID()"));
		cp.addCompletion(new BasicCompletion(cp, "Generic.UUIDFromString(UUIDString)"));

	}

}
