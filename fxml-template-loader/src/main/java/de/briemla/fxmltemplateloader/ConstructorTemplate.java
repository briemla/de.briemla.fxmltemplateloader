package de.briemla.fxmltemplateloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

class ConstructorTemplate extends InstantiationTemplate {

	private final Constructor<?> constructor;

	ConstructorTemplate(Template parent, Constructor<?> constructor, List<IProperty> properties) {
		super(parent, properties);
		this.constructor = constructor;
	}

	protected Object newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return constructor.newInstance();
	}

	@Override
	protected Class<?> instanceType() {
		return constructor.getDeclaringClass();
	}

}
