package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.briemla.fxmltemplateloader.IValue;
import javafx.fxml.LoadException;

class ProxyBuilderPropertyTemplate implements IProperty {

	private final Method method;
	private final String propertyName;
	private final IValue value;

	public ProxyBuilderPropertyTemplate(Method method, String propertyName, IValue convertedValue) {
		super();
		this.method = method;
		this.propertyName = propertyName;
		value = convertedValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	        LoadException {
		return (T) value.create(registry);
	}

	@Override
	public void apply(Object parent, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	        InstantiationException, LoadException {
		method.invoke(parent, propertyName, create(registry));
	}

}
