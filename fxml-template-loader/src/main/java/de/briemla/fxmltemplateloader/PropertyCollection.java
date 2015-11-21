package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.template.IProperty;
import de.briemla.fxmltemplateloader.template.Property;
import de.briemla.fxmltemplateloader.template.TemplateRegistry;

public class PropertyCollection {

    private final List<IProperty> properties;
    private final List<Property> unsettable;

    public PropertyCollection() {
        super();
        properties = new ArrayList<>();
        unsettable = new ArrayList<>();
    }

    public void add(IProperty property) {
        properties.add(property);
    }

    public void addUnsettable(Property property) {
        unsettable.add(property);
    }

    public boolean hasUnsettable() {
        return !unsettable.isEmpty();
    }

    public Iterable<Property> unsettable() {
        return unsettable;
    }

    /**
     * Set properties on object.
     *
     * @param object
     *            to set properties on
     * @param registry
     *            to find other elements via fx:id
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws LoadException
     */
    public void apply(Object object, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException, LoadException {
        for (IProperty child : properties) {
            child.apply(object, registry);
        }
    }

}
