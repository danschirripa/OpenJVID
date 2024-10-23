package com.javashell.openjvid.configuration;

import java.util.Hashtable;

public class jVidNodeComponentDescriptor<T> {
	private String contentClass;
	private Object[] initArgs;
	private Hashtable<Object, Object> extended;

	public jVidNodeComponentDescriptor(String content, Object... initArgs) {
		this.contentClass = content;
		this.initArgs = initArgs;
	}

	public String getContentClass() {
		return contentClass;
	}

	public Object[] getInitArgs() {
		return initArgs;
	}

	public void setInitArgs(Object[] args) {
		this.initArgs = args;
	}

	public Hashtable<Object, Object> extendedMappings() {
		return extended;
	}

}
