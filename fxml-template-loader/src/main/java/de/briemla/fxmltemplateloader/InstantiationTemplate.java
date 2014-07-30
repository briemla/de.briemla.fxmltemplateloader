package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

abstract class InstantiationTemplate extends Template implements IInstantiationTemplate {

	private final List<IProperty> properties;

	InstantiationTemplate(Template parent, List<IProperty> properties) {
		super(parent);
		this.properties = properties;
	}

	@Override
	public void prepare(IProperty child) {
		// TODO Check out FXMLLoader and apply handling from FXMLLoader.
		// What has to be done when property already exists
		// if (properties.containsKey(propertyName)) {
		// throw new RuntimeException("Property already exists.");
		// }
		properties.add(child);
	}

	@Override
	public void apply(Object newInstance, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		throw new UnsupportedOperationException("You found a bug. Please call 911 to fix it!");
	}

	@Override
	public Method findGetter(String propertyName) {
		return ReflectionUtils.findGetter(instanceType(), propertyName);
	}

	@Override
	Method findSetter(String propertyName) {
		return ReflectionUtils.findSetter(instanceType(), propertyName);
	}

	protected abstract Class<?> instanceType();

	@Override
	@SuppressWarnings("unchecked")
	public <T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object newInstance = newInstance(registry);
		applyProperties(newInstance, registry);
		return (T) newInstance;
	}

	protected abstract Object newInstance(TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			InstantiationException;

	private void applyProperties(Object newInstance, TemplateRegistry registry) throws IllegalAccessException, InvocationTargetException,
	InstantiationException {
		for (IProperty child : properties) {
			child.apply(newInstance, registry);
		}
	}
}
