package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ProxyBuilderPropertyTemplate implements IProperty {

	private final Method method;
	private final String propertyName;
	private final Object value;

	public ProxyBuilderPropertyTemplate(Method method, String propertyName, String value) {
		super();
		this.method = method;
		this.propertyName = propertyName;
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (T) value;
	}

	@Override
	public void apply(Object parent, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			InstantiationException {
		method.invoke(parent, propertyName, value);
	}

}
