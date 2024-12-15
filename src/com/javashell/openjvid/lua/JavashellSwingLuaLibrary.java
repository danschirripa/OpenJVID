package com.javashell.openjvid.lua;

import com.hk.lua.LuaLibrary;

public class JavashellSwingLuaLibrary {
	public static final LuaLibrary<LuaDesktopLibrary> DESKTOP = new LuaLibrary<>("Desktop", LuaDesktopLibrary.class);
}
