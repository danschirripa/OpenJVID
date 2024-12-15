package com.javashell.openjvid.lua.swing.components;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.JTextArea;

import com.hk.lua.Lua;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.LuaComponent;

public class LuaTextArea extends LuaComponent {
	private JTextArea area;
	private LuaTextArea reference;

	public LuaTextArea(LuaInterpreter interp) {
		super(interp);
		area = new JTextArea();
		area.setLineWrap(true);
		reference = this;
		appendMetatable();
	}

	private void appendMetatable() {
		LuaObject newMetatable = Lua.newTable();

		Set<Entry<LuaObject, LuaObject>> entries = metatable.getEntries();
		Iterator<Entry<LuaObject, LuaObject>> entryIterator = entries.iterator();
		while (entryIterator.hasNext()) {
			Entry<LuaObject, LuaObject> entry = entryIterator.next();
			newMetatable.rawSet(entry.getKey(), entry.getValue());
		}

		LuaObject setEditableFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetEditable", args, LuaType.USERDATA, LuaType.BOOLEAN);
				LuaTextArea lta = (LuaTextArea) args[0];
				lta.area.setEditable(args[1].getBoolean());
			}

		});

		LuaObject getEditableFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetEditable", args, LuaType.USERDATA);
				LuaTextArea lta = (LuaTextArea) args[0];
				return Lua.newBool(lta.area.isEditable());
			}

		});

		LuaObject setColumnsFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetColumns", args, LuaType.USERDATA, LuaType.INTEGER);
				LuaTextArea lta = (LuaTextArea) args[0];
				lta.area.setColumns(args[1].getInt());
			}

		});

		LuaObject getColumnsFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetColumns", args, LuaType.USERDATA);
				LuaTextArea lta = (LuaTextArea) args[0];
				return Lua.newNumber(lta.area.getColumns());
			}

		});

		LuaObject setRowsFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetColumns", args, LuaType.USERDATA, LuaType.INTEGER);
				LuaTextArea lta = (LuaTextArea) args[0];
				lta.area.setRows(args[1].getInt());
			}

		});

		LuaObject getRowsFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetColumns", args, LuaType.USERDATA);
				LuaTextArea lta = (LuaTextArea) args[0];
				return Lua.newNumber(lta.area.getRows());
			}

		});

		LuaObject setTextFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetText", args, LuaType.USERDATA, LuaType.STRING);
				LuaTextArea lta = (LuaTextArea) args[0];
				lta.area.setText(args[1].getString());
			}

		});

		LuaObject getTextFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetText", args, LuaType.USERDATA);
				LuaTextArea lta = (LuaTextArea) args[0];
				return Lua.newString(lta.area.getText());
			}

		});

		newMetatable.rawSet("__index", newMetatable);
		newMetatable.rawSet("__name", "TEXTAREA");

		newMetatable.rawSet("SetEditable", setEditableFunction);
		newMetatable.rawSet("GetEditable", getEditableFunction);
		newMetatable.rawSet("SetColumns", setColumnsFunction);
		newMetatable.rawSet("GetColumns", getColumnsFunction);
		newMetatable.rawSet("SetRows", setRowsFunction);
		newMetatable.rawSet("GetRows", getRowsFunction);
		newMetatable.rawSet("SetText", setTextFunction);
		newMetatable.rawSet("GetText", getTextFunction);

		metatable = newMetatable;
	}

}
