package com.javashell.openjvid.lua.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.swing.LuaComponent;
import com.javashell.openjvid.lua.swing.LuaImageWrapper;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public class LuaToggleButton extends LuaComponent {
	private JToggleButton button;
	private LuaToggleButton reference;

	public LuaToggleButton(LuaInterpreter interp) {
		super(interp);
		reference = this;
		appendMetatable();
		button = new JToggleButton();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LuaObject eventTable = Lua.newTable();
				eventTable.rawSet("Source", reference);
				eventTable.rawSet("EventType", "ButtonToggle");
				eventTable.rawSet("Boolean", button.isSelected());
				eventCallback.call(interp, eventTable);
			}
		});
		setJComponent(button);
	}

	public LuaToggleButton(LuaInterpreter interp, JToggleButton button) {
		super(interp);
		reference = this;
		appendMetatable();
		this.button = button;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LuaObject eventTable = Lua.newTable();
				eventTable.rawSet("Source", reference);
				eventTable.rawSet("EventType", "ButtonToggle");
				eventTable.rawSet("Boolean", button.isSelected());
				eventCallback.call(interp, eventTable);
			}
		});
		setJComponent(button);
	}

	public String name() {
		return "LuaToggleButton";
	}

	public void appendMetatable() {
		LuaObject setSelectedFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetSelected", args, LuaType.USERDATA, LuaType.BOOLEAN);
				LuaToggleButton ltb = (LuaToggleButton) args[0];
				ltb.button.setSelected(args[1].getBoolean());
			}

		});

		LuaObject getSelectedFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetSelected", args, LuaType.USERDATA);
				return Lua.newBool(button.isSelected());
			}

		});

		LuaObject setIconFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetIcon", args, LuaType.USERDATA, LuaType.ANY);
				if (!(args[1] instanceof LuaImageWrapper))
					Lua.badArgument(1, "SetIcon", "LuaImageWrapper expected");
				LuaToggleButton ltb = (LuaToggleButton) args[0];
				LuaImageWrapper liw = (LuaImageWrapper) args[1];
				Icon icon = new ImageIcon(liw.getImage());
				ltb.button.setIcon(icon);
			}

		});

		LuaObject setPressedIconFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetPressedIcon", args, LuaType.USERDATA, LuaType.ANY);
				if (!(args[1] instanceof LuaImageWrapper))
					Lua.badArgument(1, "SetIcon", "LuaImageWrapper expected");
				LuaToggleButton ltb = (LuaToggleButton) args[0];
				LuaImageWrapper liw = (LuaImageWrapper) args[1];
				Icon icon = new ImageIcon(liw.getImage());
				ltb.button.setPressedIcon(icon);
			}

		});

		LuaObject setTextFunction = Lua.newFunc(new Consumer<LuaObject[]>() {
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetText", args, LuaType.USERDATA, LuaType.STRING);
				LuaToggleButton ltb = (LuaToggleButton) args[0];
				ltb.button.setText(args[1].getString());
			}
		});

		LuaObject getTextFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetText", args, LuaType.USERDATA);
				LuaToggleButton ltb = (LuaToggleButton) args[0];
				return Lua.newString(ltb.button.getText());
			}

		});

		LuaObject newMetatable = Lua.newTable();

		Set<Entry<LuaObject, LuaObject>> entries = metatable.getEntries();
		Iterator<Entry<LuaObject, LuaObject>> entryIterator = entries.iterator();
		while (entryIterator.hasNext()) {
			Entry<LuaObject, LuaObject> entry = entryIterator.next();
			newMetatable.rawSet(entry.getKey(), entry.getValue());
		}

		newMetatable.rawSet("SetSelected", setSelectedFunction);
		newMetatable.rawSet("GetSelected", getSelectedFunction);
		newMetatable.rawSet("SetIcon", setIconFunction);
		newMetatable.rawSet("SetPressedIcon", setPressedIconFunction);
		newMetatable.rawSet("SetText", setTextFunction);
		newMetatable.rawSet("GetText", getTextFunction);
		newMetatable.rawSet("__index", newMetatable);
		newMetatable.rawSet("__name", "TOGGLEBUTTON");
		metatable = newMetatable;
	}

}
