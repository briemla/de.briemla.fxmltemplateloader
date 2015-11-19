package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.template.IProperty;
import de.briemla.fxmltemplateloader.template.TemplateRegistry;

public class PropertyCollection {

    private List<IProperty> properties;

    public PropertyCollection() {
        super();
        properties = new ArrayList<>();
    }

    public void add(IProperty property) {
        properties.add(property);
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
