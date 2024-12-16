package com.javashell.openjvid.lua.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JTextField;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.swing.LuaComponent;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public class LuaTextField extends LuaComponent {
	private JTextField field;
	private LuaTextField reference;

	public LuaTextField(LuaInterpreter interp) {
		super(interp);
		field = new JTextField();
		reference = this;
		appendMetatable();
		setJComponent(field);
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LuaObject eventTable = Lua.newTable();
				eventTable.rawSet("Source", reference);
				eventTable.rawSet("EventType", "TextEntry");
				eventTable.rawSet("Value", field.getText());
				eventCallback.call(interp, eventTable);
			}
		});
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
				LuaTextField ltf = (LuaTextField) args[0];
				ltf.field.setEditable(args[1].getBoolean());
			}

		});

		LuaObject getEditableFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetEditable", args, LuaType.USERDATA);
				LuaTextField ltf = (LuaTextField) args[0];
				return Lua.newBool(ltf.field.isEditable());
			}

		});

		LuaObject setColumnsFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetColumns", args, LuaType.USERDATA, LuaType.INTEGER);
				LuaTextField ltf = (LuaTextField) args[0];
				ltf.field.setColumns(args[1].getInt());
			}

		});

		LuaObject getColumnsFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetColumns", args, LuaType.USERDATA);
				LuaTextField ltf = (LuaTextField) args[0];
				return Lua.newNumber(ltf.field.getColumns());
			}

		});

		LuaObject setTextFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetText", args, LuaType.USERDATA, LuaType.STRING);
				LuaTextField ltf = (LuaTextField) args[0];
				ltf.field.setText(args[1].getString());
			}

		});

		LuaObject getTextFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetText", args, LuaType.USERDATA);
				LuaTextField ltf = (LuaTextField) args[0];
				return Lua.newString(ltf.field.getText());
			}

		});

		newMetatable.rawSet("__index", newMetatable);
		newMetatable.rawSet("__name", "TEXTFIELD");

		newMetatable.rawSet("SetEditable", setEditableFunction);
		newMetatable.rawSet("GetEditable", getEditableFunction);
		newMetatable.rawSet("SetColumns", setColumnsFunction);
		newMetatable.rawSet("GetColumns", getColumnsFunction);
		newMetatable.rawSet("SetText", setTextFunction);
		newMetatable.rawSet("GetText", getTextFunction);

		metatable = newMetatable;
	}

}
