package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Template {

	private final Template parent;
	private final Class<?> clazz;
	private final Map<String, IProperty> properties;

	public Template(Template parent, Class<?> clazz, Map<String, IProperty> properties) {
		super();
		this.parent = parent;
		this.clazz = clazz;
		this.properties = properties;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = clazz.newInstance();
		for (IProperty property : properties.values()) {
			property.apply(newInstance);
		}
		return (T) newInstance;
	}

	public Template getParent() {
		return parent;
	}

}
