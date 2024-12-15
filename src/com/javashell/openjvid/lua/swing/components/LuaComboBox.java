package com.javashell.openjvid.lua.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.javashell.openjvid.lua.LuaComponent;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public class LuaComboBox extends LuaComponent {
	private JComboBox<String> comboBox;
	private LuaComboBox reference;

	public LuaComboBox(LuaInterpreter interp, String[] options) {
		super(interp);
		appendMetatable();
		reference = this;
		comboBox = new JComboBox<String>(options);
		comboBox.setEditable(false);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LuaObject eventTable = Lua.newTable();
				eventTable.rawSet("Source", reference);
				eventTable.rawSet("EventType", "ComboBoxSelection");
				eventTable.rawSet("Value", Lua.newLuaObject((String[]) comboBox.getSelectedObjects()));
				eventCallback.call(interp, eventTable);
			}
		});
		setJComponent(comboBox);
	}

	public String name() {
		return "LuaComboBox";
	}

	private void appendMetatable() {
		LuaObject setOptionsFunction = Lua.newFunc(new Consumer<LuaObject[]>() {

			@Override
			public void accept(LuaObject[] args) {
				Lua.checkArgs("SetOptions", args, LuaType.USERDATA, LuaType.TABLE);
				LuaComboBox lcb = (LuaComboBox) args[0];
				final Set<Entry<LuaObject, LuaObject>> entries = args[1].getEntries();
				String[] newOptions = new String[entries.size()];
				final Iterator<Entry<LuaObject, LuaObject>> iter = entries.iterator();
				int i = 0;
				while (iter.hasNext()) {
					Entry<LuaObject, LuaObject> entry = iter.next();
					newOptions[i] = entry.getValue().getString();
					i++;
				}
				final ComboBoxModel<String> model = new DefaultComboBoxModel<String>(newOptions);
				lcb.comboBox.setModel(model);
			}

		});

		LuaObject getSelectedOptionsFunction = Lua.newMethod(new LuaMethod() {

			@Override
			public LuaObject call(LuaInterpreter arg0, LuaObject[] args) {
				Lua.checkArgs("GetSelected", args, LuaType.USERDATA, LuaType.TABLE);
				LuaComboBox lcb = (LuaComboBox) args[0];
				return Lua.newLuaObject((String[]) lcb.comboBox.getSelectedObjects());
			}

		});

		LuaObject newMetatable = Lua.newTable();

		Set<Entry<LuaObject, LuaObject>> entries = metatable.getEntries();
		Iterator<Entry<LuaObject, LuaObject>> entryIterator = entries.iterator();
		while (entryIterator.hasNext()) {
			Entry<LuaObject, LuaObject> entry = entryIterator.next();
			newMetatable.rawSet(entry.getKey(), entry.getValue());
		}

		newMetatable.rawSet("SetOption", setOptionsFunction);
		newMetatable.rawSet("GetSelected", getSelectedOptionsFunction);
		newMetatable.rawSet("__index", newMetatable);
		newMetatable.rawSet("__name", "CHECKBOX");
		metatable = newMetatable;
	}

}
