package de.briemla.fxmltemplateloader;

import java.util.HashMap;
import java.util.Map.Entry;

import javafx.event.Event;

public class TemplateRegistry {

	private final HashMap<Object, Object> elements;
	private final HashMap<Object, MethodHandlerStub<Event>> methods;

	public TemplateRegistry() {
		super();
		elements = new HashMap<>();
		methods = new HashMap<>();
	}

	public void register(Object id, Object object) {
		if (elements.containsKey(id)) {
			throw new RuntimeException("ID already registered. fx:id must be unique: " + id);
		}
		elements.put(id, object);
	}

	public void link(ControllerAccessor controller) {
		for (Object key : elements.keySet()) {
			Object value = elements.get(key);
			controller.linkField(key, value);
		}
		for (Entry<Object, MethodHandlerStub<Event>> entry : methods.entrySet()) {
			Object key = entry.getKey();
			MethodHandlerStub<Event> handler = entry.getValue();
			controller.linkMethodHandler(key, handler);
		}
	}

	public void addMethodStub(String value, MethodHandlerStub<Event> methodHandlerStub) {
		methods.put(value, methodHandlerStub);
	}

	public Object getFxElement(String id) {
		return elements.get(id);
	}
}