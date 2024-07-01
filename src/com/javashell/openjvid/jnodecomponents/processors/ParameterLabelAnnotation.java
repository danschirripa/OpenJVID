package com.javashell.openjvid.jnodecomponents.processors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ParameterLabelAnnotation {
	
	@Retention(value = RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface Label {
		String label();
	}
}
