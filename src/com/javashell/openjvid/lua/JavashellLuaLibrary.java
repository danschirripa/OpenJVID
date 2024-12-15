package com.javashell.openjvid.lua;

import com.hk.lua.LuaLibrary;
import com.javashell.openjvid.lua.utilities.LuaSocketHandlerLibrary;
import com.javashell.openjvid.lua.utilities.LuaTimerLibrary;

public class JavashellLuaLibrary {
	public static final LuaLibrary<LuaSocketHandlerLibrary> SOCKET = new LuaLibrary<>("Socket",
			LuaSocketHandlerLibrary.class);
	public static final LuaLibrary<LuaTimerLibrary> TIMER = new LuaLibrary<>("Timer", LuaTimerLibrary.class);
	public static final LuaLibrary<JavashellGenericLuaLibrary> JSH = new LuaLibrary<>("Generic",
			JavashellGenericLuaLibrary.class);
}
