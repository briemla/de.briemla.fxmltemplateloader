package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

class RootTemplate implements ITemplate {
	private final InstantiationTemplate template;

	public RootTemplate(InstantiationTemplate template) {
		this.template = template;
	}

	@Override
	public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return template.create();
	}
}