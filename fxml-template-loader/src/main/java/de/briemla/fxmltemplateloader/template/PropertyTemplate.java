package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.briemla.fxmltemplateloader.value.IValue;
import javafx.fxml.LoadException;

public class PropertyTemplate implements IProperty {

    private final Method method;
    private final IValue value;

    public PropertyTemplate(Method method, IValue value) {
        this.method = method;
        this.value = value;
    }

    @Override
    public void apply(Object parent, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException {
        method.invoke(parent, value.create(registry));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException {
        return (T) value.create(registry);
    }

}
