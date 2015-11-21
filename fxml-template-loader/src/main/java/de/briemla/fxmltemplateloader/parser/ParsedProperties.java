package de.briemla.fxmltemplateloader.parser;

import de.briemla.fxmltemplateloader.PropertyCollection;
import de.briemla.fxmltemplateloader.template.Controller;

public class ParsedProperties {

    private final PropertyCollection properties;
    private final Controller controller;
    private final Class<?> clazz;

    /**
     * Contains the result of properties parsed by {@link PropertiesParser}.
     *
     * @param properties
     *            collection of properties
     * @param controller
     *            object if one is specified inside the FXML file
     * @param clazz
     *            type of parent element
     */
    public ParsedProperties(PropertyCollection properties, Controller controller, Class<?> clazz) {
        this.properties = properties;
        this.controller = controller;
        this.clazz = clazz;
    }

    public PropertyCollection properties() {
        return properties;
    }

    public Class<?> rootType() {
        return clazz;
    }

    public Controller controller() {
        return controller;
    }

}
