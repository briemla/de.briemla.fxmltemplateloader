package de.briemla.fxmltemplateloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;

class ConstructorTemplate extends InstantiationTemplate {

	private final Constructor<?> constructor;

	ConstructorTemplate(Template parent, Constructor<?> constructor, List<IProperty> properties) {
		super(parent, properties);
		this.constructor = constructor;
	}

	@Override
	protected Object newInstance(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return constructor.newInstance();
	}

	@Override
	protected Class<?> instanceType() {
		return constructor.getDeclaringClass();
	}

}
