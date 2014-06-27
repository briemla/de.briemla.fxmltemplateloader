package de.briemla.fxmltemplateloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

class ConstructorTemplate extends InstantiationTemplate {

	private final Constructor<?> constructor;

	ConstructorTemplate(Constructor<?> constructor, List<IProperty> properties) {
		super(properties);
		this.constructor = constructor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = constructor.newInstance();
		applyProperties(newInstance);
		return (T) newInstance;
	}

	@Override
	protected Class<?> instanceType() {
		return constructor.getDeclaringClass();
	}

}
