package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.value.IValue;

public class StaticPropertyTemplate implements IProperty {

    private final Class<?> staticPropertyClass;
    private final Method method;
    private final IValue value;

    /**
     * Property to handle static properties of classes loaded from FXML.
     */
    public StaticPropertyTemplate(Class<?> staticPropertyClass, Method method, IValue value) {
        super();
        this.staticPropertyClass = staticPropertyClass;
        this.method = method;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, LoadException {
        return (T) value.create(registry);
    }

    @Override
    public void apply(Object parent, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException, LoadException {
        method.invoke(staticPropertyClass, parent, value.create(registry));
    }

}
