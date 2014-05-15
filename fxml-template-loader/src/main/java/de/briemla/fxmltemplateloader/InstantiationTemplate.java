package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class InstantiationTemplate extends Template implements ITemplate {

	private final Class<?> clazz;
	private final Map<String, IProperty> properties;

	InstantiationTemplate(Template parent, Class<?> clazz, Map<String, IProperty> properties) {
		super(parent);
		this.clazz = clazz;
		this.properties = properties;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = clazz.newInstance();
		for (IProperty property : properties.values()) {
			property.apply(newInstance);
		}
		return (T) newInstance;
	}

}
