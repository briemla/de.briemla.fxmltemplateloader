package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.fxml.LoadException;

public class StaticSingleElementPropertyTemplate extends Template implements IProperty {

    private final Method setter;
    private IProperty property;
    private final Class<?> staticPropertyClass;

    /**
     * Template to handle static properties of classes loaded from FXML.
     */
    public StaticSingleElementPropertyTemplate(Template parent, Method setter,
            Class<?> staticPropertyClass) {
        super(parent);
        this.staticPropertyClass = staticPropertyClass;
        this.setter = setter;
    }

    @Override
    public void apply(Object parent, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException, LoadException {
        setter.invoke(staticPropertyClass, parent, create(registry));
    }

    @Override
    public void prepare(IProperty value) {
        property = value;
        // TODO log warning when called more than once. Check behavior of FXMLLoader
    }

    @Override
    public <T> T create(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, LoadException {
        return property.create(registry);
    }

    @Override
    public Method findGetter(String propertyName) {
        throw new UnsupportedOperationException("Setter search not supported here.");
    }

    @Override
    public Method findSetter(String propertyName) {
        throw new UnsupportedOperationException("Setter search not supported here.");
    }
}
