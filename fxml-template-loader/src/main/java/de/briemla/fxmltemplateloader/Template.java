package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public class Template {

	private final Template parent;
	private final Class<?> clazz;
	private final Map<Method, Object> properties;

	public Template(Template parent, Class<?> clazz, Map<Method, Object> properties) {
		super();
		this.parent = parent;
		this.clazz = clazz;
		this.properties = properties;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = clazz.newInstance();
		for (Entry<Method, Object> property : properties.entrySet()) {
			Method method = property.getKey();
			Object value = property.getValue();
			method.invoke(newInstance, value);
		}
		return (T) newInstance;
	}

	public Template getParent() {
		return parent;
	}

}
