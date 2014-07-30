package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

import java.lang.reflect.InvocationTargetException;

import javafx.fxml.LoadException;

class RootTemplate implements ITemplate {
	private final InstantiationTemplate template;

	public RootTemplate(InstantiationTemplate template) {
		this.template = template;
	}

	@Override
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		TemplateRegistry registry = new TemplateRegistry();
		return template.create(registry);
	}

	@Override
	public <T> T create(Object controller) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	        LoadException {
		TemplateRegistry registry = new TemplateRegistry();
		T newElement = template.create(registry);
		ControllerAccessor accessor = wrap(controller);
		registry.link(to(accessor));
		return newElement;
	}

	private static ControllerAccessor wrap(Object controller) {
		ControllerAccessor accessor = new ControllerAccessor();
		accessor.setController(controller);
		return accessor;
	}
}