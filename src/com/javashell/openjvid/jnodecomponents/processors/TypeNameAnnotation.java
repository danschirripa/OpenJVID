package com.javashell.openjvid.jnodecomponents.processors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TypeNameAnnotation {

	@Retention(value = RetentionPolicy.RUNTIME)
	public @interface TypeName {
		String typeName();

		boolean isShown() default true;
	}

}
