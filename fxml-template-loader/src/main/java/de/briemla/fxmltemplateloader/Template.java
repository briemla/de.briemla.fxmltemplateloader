package de.briemla.fxmltemplateloader;

import java.lang.reflect.Method;

public abstract class Template {

	private final Template parent;

	public Template(Template parent) {
		super();
		this.parent = parent;
	}

	Template getParent() {
		return parent;
	}

	protected abstract void prepare(IProperty property);

	abstract Method findGetter(String propertyName);

	abstract Method findSetter(String propertyName);

}