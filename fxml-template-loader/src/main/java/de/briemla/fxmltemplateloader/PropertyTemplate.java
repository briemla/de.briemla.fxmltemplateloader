package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PropertyTemplate implements IProperty {

	private final Method method;
	private final Object value;

	PropertyTemplate(Method method, Object value) {
		this.method = method;
		this.value = value;
	}

	@Override
	public void apply(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(parent, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) value;
	}

}
