package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class InstantiationTemplate extends Template implements ITemplate, IProperty {

	private final Class<?> instanceClass;
	private final Map<String, IProperty> properties;

	InstantiationTemplate(Template parent, Class<?> instanceClass, Map<String, IProperty> properties) {
		super(parent);
		this.instanceClass = instanceClass;
		this.properties = properties;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = instanceClass.newInstance();
		for (IProperty child : properties.values()) {
			child.apply(newInstance);
		}
		return (T) newInstance;
	}

	@Override
	protected Class<?> getInstanceClass() {
		return instanceClass;
	}

	@Override
	protected void addProperty(String propertyName, IProperty child) {
		if (properties.containsKey(propertyName)) {
			// TODO Check out FXMLLoader and apply handling from FXMLLoader.
			throw new RuntimeException("Property already exists.");
		}
		properties.put(propertyName, child);
	}

	@Override
	public void apply(Object newInstance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		throw new UnsupportedOperationException();
	}

}
