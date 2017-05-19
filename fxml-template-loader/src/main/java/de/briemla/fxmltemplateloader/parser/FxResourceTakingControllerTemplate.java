package de.briemla.fxmltemplateloader.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.template.Controller;
import de.briemla.fxmltemplateloader.template.FxControllerTemplate;

public class FxResourceTakingControllerTemplate extends FxControllerTemplate implements Controller {

	private final Method initialize;
	private final URL location;
	private final ResourceBundle resourceBundle;

	public FxResourceTakingControllerTemplate(Class<?> clazz, Method initialize, URL location,
			ResourceBundle resourceBundle) {
		super(clazz);
		this.initialize = initialize;
		this.location = location;
		this.resourceBundle = resourceBundle;
	}

	@Override
	public Object instance()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object instance = super.instance();
		initialize.invoke(instance, location, resourceBundle);
		return instance;
	}

}
