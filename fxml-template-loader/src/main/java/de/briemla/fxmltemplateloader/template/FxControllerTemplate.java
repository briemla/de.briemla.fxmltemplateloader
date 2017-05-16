package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;

public class FxControllerTemplate implements Controller {

    private Class<?> controllerClass;

    public FxControllerTemplate(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    @Override
    public Object instance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return controllerClass.newInstance();
    }

}
