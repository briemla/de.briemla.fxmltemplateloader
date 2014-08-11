package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.fxml.LoadException;

class FxIdPropertyTemplate extends Template implements IProperty {

	private final IValue value;

	public FxIdPropertyTemplate(Template parent, IValue value) {
		super(parent);
		this.value = value;
	}

	@Override
	public void apply(Object parent, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	InstantiationException, LoadException {
		Object id = value.create(registry);
		registry.register(id, parent);
	}

	@Override
	protected void prepare(IProperty property) {
		// this.property = property;
		// TODO log warning when called more than once. Check behavior of FXMLLoader
		throw new RuntimeException("Should never be called. Call 911 to fix this bug.");
	}

	@Override
	public <T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	LoadException {
		throw new RuntimeException("Should never be called. Call 911 to fix this bug.");
	}

	@Override
	Method findGetter(String propertyName) {
		throw new UnsupportedOperationException("Setter search not supported here.");
	}

	@Override
	Method findSetter(String propertyName) {
		throw new UnsupportedOperationException("Setter search not supported here.");
	}

}
