package de.briemla.fxmltemplateloader.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

// TODO clean up
public class ReflectionUtils {

    private static final String initialize = "initialize";

	/**
     * Convenience method to find getter for a given {@link Class} and property name.
     */
    public static Method findGetter(Class<?> clazz, String propertyName) {
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        for (Method method : clazz.getMethods()) {
            if (getterName.equals(method.getName()) && method.getParameterCount() == 0) {
                return method;
            }
        }
        throw new IllegalStateException(
                "Could not find getter without parameters for property: " + propertyName);
    }

    /**
     * Convenience method to check whether there is a getter for a given {@link Class} and property
     * name.
     */
    public static boolean hasGetter(Class<?> clazz, String propertyName) {
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        for (Method method : clazz.getMethods()) {
            if (getterName.equals(method.getName()) && method.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method to find setter for a given {@link Class} and property name.
     */
    public static Method findSetter(Class<?> clazz, String propertyName) {
        String setterName = setter(propertyName);
        for (Method method : clazz.getMethods()) {
            if (setterName.equals(method.getName())) {
                return method;
            }
        }
        throw new IllegalStateException("Could not find setter in clazz " + clazz.getName()
                + " for property: " + propertyName);
    }

    private static String setter(String propertyName) {
        return "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    /**
     * Convenience method to check whether there is a getter for a given {@link Class} and property
     * name.
     */
    public static boolean hasSetter(Class<?> clazz, String propertyName) {
        String setterName = setter(propertyName);
        for (Method method : clazz.getMethods()) {
            if (setterName.equals(method.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method to find builder method for a given {@link Class} and property name.
     */
    public static Method findBuilderMethod(Class<?> clazz, String propertyName) {
        for (Method method : clazz.getMethods()) {
            if (propertyName.equals(method.getName())) {
                return method;
            }
        }
        throw new IllegalStateException(
                "Could not find builder method for property: " + propertyName);
    }

    /**
     * Convenience method to check whether there is a getter for a given {@link Class} and property
     * name.
     */
    public static boolean hasBuilderMethod(Class<?> clazz, String propertyName) {
        for (Method method : clazz.getMethods()) {
            if (propertyName.equals(method.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method to extract the type of the value object for setter methods.
     */
    public static Class<?> extractType(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (Modifier.isStatic(method.getModifiers()) && parameterTypes.length == 2) {
            return parameterTypes[1];
        }
        if (parameterTypes.length == 1) {
            return parameterTypes[0];
        }
        throw new RuntimeException(
                "Incorrect number of arguments for setter found: " + parameterTypes.length);
    }

    /**
     * Convenience method to make a field accessible.
     */
    public static void makeAccessible(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static boolean isInitializable(Class<?> clazz) {
		for (Method method : clazz.getMethods()) {
            if (isAnInitializable(method)) {
                return true;
            }
        }
        return false;
	}
    
    public static Method findInitializeMethod(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (isAnInitializable(method)) {
                return method;
            }
        }
        throw new IllegalStateException(
                "Class is not initializable. Could not find initiliaze method in class: ");
    }

	private static boolean isAnInitializable(Method method) {
		return initialize.equals(method.getName()) && 0 == method.getParameterTypes().length;
	}
}
