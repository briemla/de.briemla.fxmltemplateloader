package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PropertyTemplate implements IProperty {

	private final Method method;
	private final IValue value;

	PropertyTemplate(Method method, IValue value) {
		this.method = method;
		this.value = value;
	}

	@Override
	public void apply(Object parent, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(parent, value.create());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) value.create();
	}

}
