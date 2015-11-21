package de.briemla.fxmltemplateloader.template;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

import java.lang.reflect.InvocationTargetException;

import javafx.fxml.LoadException;

public class RootTemplate implements ITemplate {
    private final InstantiationTemplate template;
    private Object lastUsedController;
    private final Controller controller;

    public RootTemplate(InstantiationTemplate template, Controller controller) {
        this.template = template;
        this.controller = controller;
    }

    @Override
    public <T> T create() throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, LoadException {
        if (controller == null) {
            TemplateRegistry registry = new TemplateRegistry();
            return template.create(registry);
        }
        return create(controller);
    }

    @Override
    public <T> T create(Object controller) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, LoadException {
        lastUsedController = instanceOf(controller);
        TemplateRegistry registry = new TemplateRegistry();
        T newElement = template.create(registry);
        ControllerAccessor accessor = wrap(lastUsedController);
        registry.link(to(accessor));
        return newElement;
    }

    private Object instanceOf(Object controller)
            throws InstantiationException, IllegalAccessException {
        if (controller instanceof Controller) {
            return ((Controller) controller).instance();
        }
        return controller;
    }

    private static ControllerAccessor wrap(Object controller) {
        ControllerAccessor accessor = new ControllerAccessor();
        accessor.setController(controller);
        return accessor;
    }

    @Override
    public void setRoot(Object fxRoot) {
        if (template instanceof FxRootTemplate) {
            ((FxRootTemplate) template).setRoot(fxRoot);
            return;
        }
        throw new RuntimeException("Root element of FXML file is not a fx:root element.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getController() {
        return (T) lastUsedController;
    }
}