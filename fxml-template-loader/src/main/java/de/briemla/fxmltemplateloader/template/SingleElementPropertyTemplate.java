package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.briemla.fxmltemplateloader.IProperty;
import javafx.fxml.LoadException;

public class SingleElementPropertyTemplate extends Template implements IProperty {

    private final Method setter;
    private IProperty property;

    public SingleElementPropertyTemplate(Template parent, Method setter) {
        super(parent);
        this.setter = setter;
    }

    @Override
    public void apply(Object parent, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, LoadException {
        Object value = create(registry);
        setter.invoke(parent, value);
    }

    @Override
    public void prepare(IProperty value) {
        property = value;
        // TODO log warning when called more than once. Check behavior of FXMLLoader
    }

    @Override
    public <T> T create(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException {
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
