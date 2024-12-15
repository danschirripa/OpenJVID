package com.javashell.openjvid.lua.swing;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;

public class LuaImageWrapper extends LuaUserdata {
	private BufferedImage image;
	private int width, height;
	private boolean isScaled = false;;
	private static final LuaObject luaImageWrapperMetatable = Lua.newTable();

	public LuaImageWrapper(BufferedImage image) {
		this.image = image;
		metatable = luaImageWrapperMetatable;
	}

	@Override
	public Object getUserdata() {
		return image;
	}

	@Override
	public String name() {
		return "Image";
	}

	public BufferedImage getImage() {
		if (isScaled) {
			return (BufferedImage) image.getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT);
		} else {
			return image;
		}
	}

	private void destroy() {
		image = null;
	}

	static {
		luaImageWrapperMetatable.rawSet("__name", Lua.newString("IMAGE"));
		luaImageWrapperMetatable.rawSet("__index", luaImageWrapperMetatable);

		LuaObject setSizeFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetSize", args, LuaType.USERDATA, LuaType.INTEGER, LuaType.INTEGER);
				LuaImageWrapper liw = (LuaImageWrapper) args[0];
				liw.width = args[1].getInt();
				liw.height = args[2].getInt();
			}

		});

		LuaObject getSizeFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetSize", args, LuaType.USERDATA);
				LuaImageWrapper liw = (LuaImageWrapper) args[0];
				LuaObject dimensionTable = Lua.newTable();
				dimensionTable.rawSet(0, liw.width);
				dimensionTable.rawSet(1, liw.height);
				return dimensionTable;
			}

		});

		LuaObject destroyImageFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("Destroy", args, LuaType.USERDATA);
				LuaImageWrapper liw = (LuaImageWrapper) args[0];
				liw.destroy();
			}

		});

		luaImageWrapperMetatable.rawSet("SetSize", setSizeFunction);
		luaImageWrapperMetatable.rawSet("GetSize", getSizeFunction);
		luaImageWrapperMetatable.rawSet("Destroy", destroyImageFunction);
	}

}
