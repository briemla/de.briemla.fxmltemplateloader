package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class InstantiationTemplate extends Template implements ITemplate, IProperty {

	private final Class<?> instanceClass;
	private final List<IProperty> properties;

	InstantiationTemplate(Template parent, Class<?> instanceClass, List<IProperty> properties) {
		super(parent);
		this.instanceClass = instanceClass;
		this.properties = properties;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = instanceClass.newInstance();
		for (IProperty child : properties) {
			child.apply(newInstance);
		}
		return (T) newInstance;
	}

	@Override
	protected void addProperty(IProperty child) {
		// TODO Check out FXMLLoader and apply handling from FXMLLoader.
		// What has to be done when property already exists
		// if (properties.containsKey(propertyName)) {
		// throw new RuntimeException("Property already exists.");
		// }
		properties.add(child);
	}

	@Override
	public void apply(Object newInstance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		throw new UnsupportedOperationException();
	}

	@Override
	Method findGetter(String propertyName) {
		return ReflectionUtils.findGetter(instanceClass, propertyName);
	}

}
