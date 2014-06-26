package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SingleElementPropertyTemplate extends Template implements IProperty {

	private final Method setter;
	private IProperty property;

	public SingleElementPropertyTemplate(Template parent, Method setter) {
		super(parent);
		this.setter = setter;
	}

	@Override
	public void apply(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		setter.invoke(parent, create());
	}

	@Override
	protected void addProperty(IProperty property) {
		this.property = property;
	}

	@Override
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return property.create();
	}

	@Override
	Method findGetter(String propertyName) {
		throw new UnsupportedOperationException();
	}

	@Override
	Method findSetter(String propertyName) {
		throw new UnsupportedOperationException();
	}

}
