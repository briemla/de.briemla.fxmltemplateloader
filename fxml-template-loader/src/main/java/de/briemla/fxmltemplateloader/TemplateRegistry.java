package de.briemla.fxmltemplateloader;

import java.util.HashMap;

public class TemplateRegistry {

	private final HashMap<Object, Object> elements;

	public TemplateRegistry() {
		super();
		elements = new HashMap<>();
	}

	public void add(Object id, Object object) {
		elements.put(id, object);
	}

	public void link(ControllerAccessor controller) {
		for (Object key : elements.keySet()) {
			Object value = elements.get(key);
			controller.linkField(key, value);
		}
	}

}