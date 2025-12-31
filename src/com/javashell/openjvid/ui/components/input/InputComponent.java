package com.javashell.openjvid.ui.components.input;

import javax.swing.JPanel;

public abstract class InputComponent<T> {

	public abstract JPanel getPanel(String label);

	public abstract T getParameter();

	public abstract T attemptCreation(Object... params);

}
