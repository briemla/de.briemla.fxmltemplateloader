package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FxInitializableControllerTemplate extends FxControllerTemplate implements Controller {

	private final Method initiliaze;

	public FxInitializableControllerTemplate(Class<?> clazz, Method initiliaze) {
		super(clazz);
		this.initiliaze = initiliaze;
	}
	
	@Override
	public Object instance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object instance = super.instance();
		initiliaze.invoke(instance);
		return instance;
	}

}
