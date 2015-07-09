package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;
import javafx.fxml.LoadException;

public class StaticPropertyTemplate implements IProperty {

	private final Class<?> staticPropertyClass;
	private final Method method;
	private final IValue value;

	public StaticPropertyTemplate(Class<?> staticPropertyClass, Method method, IValue value) {
		super();
		this.staticPropertyClass = staticPropertyClass;
		this.method = method;
		this.value = value;
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
		method.invoke(staticPropertyClass, parent, value.create(registry));
	}

}
