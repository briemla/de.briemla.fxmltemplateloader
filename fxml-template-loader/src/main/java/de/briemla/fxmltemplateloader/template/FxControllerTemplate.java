package de.briemla.fxmltemplateloader.template;

public class FxControllerTemplate implements Controller {

    private Class<?> controllerClass;

    public FxControllerTemplate(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    @Override
    public Object instance() throws InstantiationException, IllegalAccessException {
        return controllerClass.newInstance();
    }

}
