package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.PrivilegedAction;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import de.briemla.fxmltemplateloader.value.MethodHandlerStub;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

/**
 * This class was copied from {@link FXMLLoader} and the method {@link #linkField(Object, Object)} was added. For more information see the original class. This
 * is a copy because I could not access the original class, but wanted to reuse the reflection mechanisms which were implemented there.
 *
 * @author Lars
 *
 */
@SuppressWarnings("restriction")
public class ControllerAccessor {
	private static final int PUBLIC = 1;
	private static final int PROTECTED = 2;
	private static final int PACKAGE = 4;
	private static final int PRIVATE = 8;
	private static final int INITIAL_CLASS_ACCESS = PUBLIC | PACKAGE | PRIVATE;
	private static final int INITIAL_MEMBER_ACCESS = PUBLIC | PROTECTED | PACKAGE | PRIVATE;

	private static final int METHODS = 0;
	private static final int FIELDS = 1;

	private Object controller;
	private ClassLoader callerClassLoader;

	private Map<String, Field> controllerFields;
	private Map<SupportedType, Map<String, Method>> controllerMethods;

	void setController(final Object controller) {
		if (this.controller != controller) {
			this.controller = controller;
			reset();
		}
	}

	void setCallerClass(final Class<?> callerClass) {
		final ClassLoader newCallerClassLoader = (callerClass != null) ? callerClass.getClassLoader() : null;
		if (callerClassLoader != newCallerClassLoader) {
			callerClassLoader = newCallerClassLoader;
			reset();
		}
	}

	void reset() {
		controllerFields = null;
		controllerMethods = null;
	}

	Map<String, Field> getControllerFields() {
		if (controllerFields == null) {
			controllerFields = new HashMap<>();

			if (callerClassLoader == null) {
				// allow null class loader only with full permission check
				checkAllPermissions();
			}

			addAccessibleMembers(controller.getClass(), INITIAL_CLASS_ACCESS, INITIAL_MEMBER_ACCESS, FIELDS);
		}

		return controllerFields;
	}

	Map<SupportedType, Map<String, Method>> getControllerMethods() {
		if (controllerMethods == null) {
			controllerMethods = new EnumMap<>(SupportedType.class);
			for (SupportedType t : SupportedType.values()) {
				controllerMethods.put(t, new HashMap<String, Method>());
			}

			if (callerClassLoader == null) {
				// allow null class loader only with full permission check
				checkAllPermissions();
			}

			addAccessibleMembers(controller.getClass(), INITIAL_CLASS_ACCESS, INITIAL_MEMBER_ACCESS, METHODS);
		}

		return controllerMethods;
	}

	private void addAccessibleMembers(final Class<?> type, final int prevAllowedClassAccess, final int prevAllowedMemberAccess, final int membersType) {
		if (type == Object.class) {
			return;
		}

		int allowedClassAccess = prevAllowedClassAccess;
		int allowedMemberAccess = prevAllowedMemberAccess;
		if ((callerClassLoader != null) && (type.getClassLoader() != callerClassLoader)) {
			// restrict further access
			allowedClassAccess &= PUBLIC;
			allowedMemberAccess &= PUBLIC;
		}

		final int classAccess = getAccess(type.getModifiers());
		if ((classAccess & allowedClassAccess) == 0) {
			// we are done
			return;
		}

		ReflectUtil.checkPackageAccess(type);

		addAccessibleMembers(type.getSuperclass(), allowedClassAccess, allowedMemberAccess & ~PRIVATE, membersType);

		final int finalAllowedMemberAccess = allowedMemberAccess;
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				if (membersType == FIELDS) {
					addAccessibleFields(type, finalAllowedMemberAccess);
				} else {
					addAccessibleMethods(type, finalAllowedMemberAccess);
				}

				return null;
			}
		});
	}

	private boolean isAccessibleToController(final Class<?> type, final int memberModifiers) {
		if (Modifier.isPublic(memberModifiers) || Modifier.isProtected(memberModifiers)) {
			return true;
		}

		return Reflection.verifyMemberAccess(controller.getClass(), type, controller, memberModifiers);
	}

	private void addAccessibleFields(final Class<?> type, final int allowedMemberAccess) {
		final boolean isPublicType = Modifier.isPublic(type.getModifiers());

		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
			final int memberModifiers = field.getModifiers();

			if (((memberModifiers & (Modifier.STATIC | Modifier.FINAL)) != 0) || ((getAccess(memberModifiers) & allowedMemberAccess) == 0)
					|| !isAccessibleToController(type, memberModifiers)) {
				continue;
			}

			if (!isPublicType || !Modifier.isPublic(memberModifiers)) {
				if (field.getAnnotation(FXML.class) == null) {
					// no fxml annotation on a non-public field
					continue;
				}

				// Ensure that the field is accessible
				field.setAccessible(true);
			}

			controllerFields.put(field.getName(), field);
		}
	}

	private void addAccessibleMethods(final Class<?> type, final int allowedMemberAccess) {
		final boolean isPublicType = Modifier.isPublic(type.getModifiers());

		final Method[] methods = type.getDeclaredMethods();
		for (final Method method : methods) {
			final int memberModifiers = method.getModifiers();

			if (((memberModifiers & (Modifier.STATIC | Modifier.NATIVE)) != 0) || ((getAccess(memberModifiers) & allowedMemberAccess) == 0)
					|| !isAccessibleToController(type, memberModifiers)) {
				continue;
			}

			if (!isPublicType || !Modifier.isPublic(memberModifiers)) {
				if (method.getAnnotation(FXML.class) == null) {
					// no fxml annotation on a non-public method
					continue;
				}

				// Ensure that the method is accessible
				method.setAccessible(true);
			}

			// Add this method to the map if:
			// a) it is the initialize() method, or
			// b) it takes a single event argument, or
			// c) it takes no arguments and a handler with this
			// name has not already been defined
			final String methodName = method.getName();
			final SupportedType convertedType;

			if ((convertedType = toSupportedType(method)) != null) {
				controllerMethods.get(convertedType).put(methodName, method);
			}
		}
	}

	private static int getAccess(final int fullModifiers) {
		final int untransformedAccess = fullModifiers & (Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC);

		switch (untransformedAccess) {
		case Modifier.PUBLIC:
			return PUBLIC;

		case Modifier.PROTECTED:
			return PROTECTED;

		case Modifier.PRIVATE:
			return PRIVATE;

		default:
			return PACKAGE;
		}
	}

	private static void checkAllPermissions() {
		final SecurityManager securityManager = System.getSecurityManager();
		if (securityManager != null) {
			securityManager.checkPermission(new AllPermission());
		}
	}

	private static enum SupportedType {
		PARAMETERLESS {

			@Override
			protected boolean methodIsOfType(Method m) {
				return m.getParameterCount() == 0;
			}

		},
		EVENT {

			@Override
			protected boolean methodIsOfType(Method m) {
				return m.getParameterCount() == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0]);
			}

		},
		LIST_CHANGE_LISTENER {

			@Override
			protected boolean methodIsOfType(Method m) {
				return m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(ListChangeListener.Change.class);
			}

		},
		MAP_CHANGE_LISTENER {

			@Override
			protected boolean methodIsOfType(Method m) {
				return m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(MapChangeListener.Change.class);
			}

		},
		SET_CHANGE_LISTENER {

			@Override
			protected boolean methodIsOfType(Method m) {
				return m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(SetChangeListener.Change.class);
			}

		},
		PROPERTY_CHANGE_LISTENER {

			@Override
			protected boolean methodIsOfType(Method m) {
				return m.getParameterCount() == 3 && ObservableValue.class.isAssignableFrom(m.getParameterTypes()[0])
				        && m.getParameterTypes()[1].equals(m.getParameterTypes()[2]);
			}

		};

		protected abstract boolean methodIsOfType(Method m);
	}

	private static SupportedType toSupportedType(Method m) {
		for (SupportedType t : SupportedType.values()) {
			if (t.methodIsOfType(m)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * This method was added by Lars. The rest of this class was copied from original class. For more information see: FXMLLoader#ControllerAccessor
	 *
	 * @param key
	 * @param value
	 */
	public void linkField(Object key, Object value) {
		Field field = getControllerFields().get(key);

		if (field != null) {
			try {
				field.set(controller, value);
			} catch (IllegalAccessException exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	public void linkMethodHandler(Object key, MethodHandlerStub<Event> handler) {
		Method method = getControllerMethods().get(SupportedType.EVENT).get(key);
		handler.bindTo(controller, method);
	}
}