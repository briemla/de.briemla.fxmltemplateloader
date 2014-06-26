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

	public static Method findSetter(Class<?> clazz, String propertyName) {
		String setterName = setter(propertyName);
		for (Method method : clazz.getMethods()) {
			if (setterName.equals(method.getName())) {
				return method;
			}
		}
		throw new IllegalStateException("Could not find setter for property: " + propertyName);
	}

	private static String setter(String propertyName) {
		return "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
	}

	public static boolean hasSetter(Class<?> clazz, String propertyName) {
		String setterName = setter(propertyName);
		for (Method method : clazz.getMethods()) {
			if (setterName.equals(method.getName())) {
				return true;
			}
		}
		return false;
	}
}
