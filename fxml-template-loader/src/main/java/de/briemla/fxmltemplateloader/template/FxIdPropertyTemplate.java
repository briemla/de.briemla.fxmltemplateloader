package de.briemla.fxmltemplateloader.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.value.IValue;

public class FxIdPropertyTemplate extends Template implements IProperty {

    private final IValue value;
    private Method fxIdSetter;

    public FxIdPropertyTemplate(Template parent, Method fxIdSetter, IValue value) {
        super(parent);
        this.fxIdSetter = fxIdSetter;
        this.value = value;
    }

    @Override
    public void apply(Object parent, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException, LoadException {
        Object id = value.create(registry);
        setId(parent, id);
        registry.register(id, parent);
    }

    private void setId(Object parent, Object id)
            throws IllegalAccessException, InvocationTargetException {
        if (fxIdSetter == null) {
            return;
        }
        fxIdSetter.invoke(parent, id);
    }

    @Override
    public void prepare(IProperty property) {
        // this.property = property;
        // TODO log warning when called more than once. Check behavior of FXMLLoader
        throw new RuntimeException("Should never be called. Call 911 to fix this bug.");
    }

    @Override
    public <T> T create(TemplateRegistry registry)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, LoadException {
        throw new RuntimeException("Should never be called. Call 911 to fix this bug.");
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
