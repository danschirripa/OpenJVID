package com.javashell.openjvid.lua;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.hk.lua.Lua;
import com.hk.lua.LuaFactory;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaLibraryPackage;
import com.hk.lua.LuaObject;
import com.javashell.openjvid.lua.exceptions.LuaLibraryLoadException;

public class LuaManager {
	private static Hashtable<String, LuaLibrary<?>> luaLibs = new Hashtable<String, LuaLibrary<?>>();

	public static void registerHook(String hookQualifier, LuaLibrary<?> lib) throws LuaLibraryLoadException {
		if (luaLibs.containsKey(hookQualifier) || luaLibs.contains(lib)) {
			throw new LuaLibraryLoadException();
		}
		luaLibs.put(hookQualifier, lib);
	}

	public static void deregisterHook(String hookQualifier) {
		luaLibs.remove(hookQualifier);
	}

	public static LuaFactory injectAllHooks(LuaFactory factory) {
		Enumeration<LuaLibrary<?>> libsEnum = luaLibs.elements();
		while (libsEnum.hasMoreElements()) {
			factory.addLibrary(libsEnum.nextElement());
		}
		return factory;
	}

	public static LuaFactory injectHook(String hookQualifier, LuaFactory factory) throws LuaLibraryLoadException {
		if (luaLibs.containsKey(hookQualifier)) {
			factory.addLibrary(luaLibs.get(hookQualifier));
			return factory;
		}
		throw new LuaLibraryLoadException();
	}

	public static LuaFactory injectHooks(LuaFactory factory, String... qualifiers) throws LuaLibraryLoadException {
		for (String hook : qualifiers) {
			if (luaLibs.containsKey(hook)) {
				factory.addLibrary(luaLibs.get(hook));
			} else {
				throw new LuaLibraryLoadException();
			}
		}
		return factory;
	}

	public static LuaFactory injectHooks(LuaFactory factory, boolean ignoreMissing, String... qualifiers) {
		for (String hook : qualifiers) {
			if (luaLibs.containsKey(hook))
				factory.addLibrary(luaLibs.get(hook));
		}
		return factory;
	}

	public static LuaObject execLuaFile(File luaFile) throws IOException {
		LuaFactory factory = Lua.factory(luaFile);
		Lua.importStandard(factory);
		LuaManager.injectAllHooks(factory);
		factory.compile();
		LuaInterpreter interp = factory.build();
		interp.setExtra(LuaLibraryPackage.EXKEY_PATH, luaFile.getParent() + "/?.lua");
		return interp.execute();
	}

	public static LuaObject execLuaString(String luaScript) throws IOException {
		LuaFactory factory = Lua.factory(luaScript);
		Lua.importStandard(factory);
		LuaManager.injectAllHooks(factory);
		factory.compile();
		LuaInterpreter interp = factory.build();
		return interp.execute();
	}

}
