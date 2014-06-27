package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

abstract class InstantiationTemplate extends Template implements IInstantiationTemplate {

	private final List<IProperty> properties;

	InstantiationTemplate(List<IProperty> properties) {
		super();
		this.properties = properties;
	}

	@Override
	public void addProperty(IProperty child) {
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
	public Method findGetter(String propertyName) {
		return ReflectionUtils.findGetter(instanceType(), propertyName);
	}

	@Override
	Method findSetter(String propertyName) {
		return ReflectionUtils.findSetter(instanceType(), propertyName);
	}

	protected Class<?> instanceType() {
		return null;
	}

	protected void applyProperties(Object newInstance) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		for (IProperty child : properties) {
			child.apply(newInstance);
		}
	}

}
