package com.javashell.openjvid.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.javashell.openjvid.lua.JavashellGenericLuaLibrary;
import com.javashell.openjvid.lua.LuaDesktopLibrary;

public class LuaCodeEditorFrame extends JFrame {
	private TextEditorPane area;
	private RTextScrollPane editorScroller;
	private File luaFile;
	private String luaScript;
	private final String name;

	public LuaCodeEditorFrame(File luaCode) {
		this.luaFile = luaCode;
		name = luaFile.getName();
		create();
	}

	public LuaCodeEditorFrame(String script, String name) {
		this.name = name;
		luaScript = script;
		create();
	}

	public void create() {
		setTitle(name);

		area = new TextEditorPane();
		JPanel editor = new JPanel(new BorderLayout());

		if (luaFile != null) {
			try {
				area.load(FileLocation.create(luaFile));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else {
			area.setText(luaScript);
		}
		area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
		area.setCodeFoldingEnabled(true);
		area.setCaretPosition(0);

		try {
			Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
			theme.apply(area);
		} catch (IOException ioe) { // Never happens
			ioe.printStackTrace();
		}

		area.setBracketMatchingEnabled(true);
		area.setMarkOccurrences(true);

		editorScroller = new RTextScrollPane(area);

		CompletionProvider cp = createCompletionProvider();

		AutoCompletion ac = new AutoCompletion(cp);
		ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(true);

		ac.setAutoCompleteEnabled(true);
		ac.setAutoActivationEnabled(true);
		ac.setAutoCompleteSingleChoices(false);
		ac.setAutoActivationDelay(800);
		ac.install(area);

		setSize(600, 600);
		editorScroller.setPreferredSize(new Dimension(600, 600));
		editor.setPreferredSize(new Dimension(600, 600));

		editor.add(editorScroller);
		setContentPane(editor);
		// setJMenuBar(createMenuBar());
		pack();
		setVisible(true);
	}

	public String getScriptText() {
		return area.getText();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem loadItem = new JMenuItem("Load");

		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		loadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		fileMenu.add(saveItem);
		fileMenu.add(loadItem);

		menu.add(fileMenu);
		return menu;
	}

	private CompletionProvider createCompletionProvider() {
		DefaultCompletionProvider provider = new DefaultCompletionProvider();

		provider.addCompletion(new BasicCompletion(provider, "function"));
		provider.addCompletion(new BasicCompletion(provider, "end"));
		provider.addCompletion(new BasicCompletion(provider, "--[[]]--"));

		provider.addCompletion(new ShorthandCompletion(provider, "initargs",
				"--[[\n" + " Pack varargs into more standard format, extract this component\n" + "]]--\n"
						+ "args = table.pack(...);\n" + "\n" + "-- \n" + "Node = args[1];\n"
						+ "NodeName = Node:GetNodeName();"));

		provider.setAutoActivationRules(true, null);

		JavashellGenericLuaLibrary.updateCompletions(provider);
		LuaDesktopLibrary.updateCompletions(provider);

		return provider;

	}

}