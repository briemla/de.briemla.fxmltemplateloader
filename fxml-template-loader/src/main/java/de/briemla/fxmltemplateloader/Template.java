package de.briemla.fxmltemplateloader;

import java.lang.reflect.Method;

public abstract class Template {

	public Template() {
		super();
	}

	protected abstract void addProperty(IProperty property);

	abstract Method findGetter(String propertyName);

	abstract Method findSetter(String propertyName);

}