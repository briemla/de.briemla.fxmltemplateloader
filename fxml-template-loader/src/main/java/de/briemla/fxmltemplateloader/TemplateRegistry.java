package de.briemla.fxmltemplateloader;

import java.lang.reflect.Field;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.LoadException;

public class TemplateRegistry {

	private final HashMap<Object, Object> elements;

	public TemplateRegistry() {
		super();
		this.elements = new HashMap<>();
	}

	public void add(Object id, Object object) {
		elements.put(id, object);
	}

	public void link(Object controller) throws LoadException, IllegalArgumentException, IllegalAccessException {
		for (Field field : controller.getClass().getDeclaredFields()) {
			if (field.getAnnotation(FXML.class) == null) {
				continue;
			}
			link(controller, field);
		}
	}

	private void link(Object controller, Field field) throws IllegalAccessException, LoadException {
		if (elements.containsKey(field.getName())) {
			Object value = elements.get(field.getName());
			field.set(controller, value);
			return;
		}
		throw new LoadException("Could not link annotated member: " + field.getName());
	}
}
