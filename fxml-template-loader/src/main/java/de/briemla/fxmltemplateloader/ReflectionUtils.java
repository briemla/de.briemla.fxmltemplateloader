package de.briemla.fxmltemplateloader;

import java.lang.reflect.Method;

public class ReflectionUtils {

	public static Method findGetter(Class<?> clazz, String propertyName) {
		String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
		for (Method method : clazz.getMethods()) {
			if (getterName.equals(method.getName()) && method.getParameterCount() == 0) {
				return method;
			}
		}
		throw new IllegalStateException("Could not find getter without parameters for property: " + propertyName);
	}

}
