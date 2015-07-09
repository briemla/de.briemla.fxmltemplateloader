package de.briemla.fxmltemplateloader.template;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

import java.lang.reflect.InvocationTargetException;

import javafx.fxml.LoadException;

public class RootTemplate implements ITemplate {
    private final InstantiationTemplate template;

    public RootTemplate(InstantiationTemplate template) {
        this.template = template;
    }

    @Override
    public <T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException {
        TemplateRegistry registry = new TemplateRegistry();
        return template.create(registry);
    }

    @Override
    public <T> T create(Object controller)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException {
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

    @Override
    public void setRoot(Object fxRoot) {
        if (template instanceof FxRootTemplate) {
            ((FxRootTemplate) template).setRoot(fxRoot);
            return;
        }
        throw new RuntimeException("Root element of FXML file is not a fx:root element.");
    }
}