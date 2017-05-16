package de.briemla.fxmltemplateloader.parser;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.PropertyCollection;
import de.briemla.fxmltemplateloader.template.Controller;
import de.briemla.fxmltemplateloader.template.FxControllerTemplate;
import de.briemla.fxmltemplateloader.template.FxInitializableControllerTemplate;
import de.briemla.fxmltemplateloader.template.Template;
import de.briemla.fxmltemplateloader.util.ReflectionUtils;

public class PropertiesParser {

    private static final String FX_ROOT = "root";
    private static final String FX_NAMESPACE_PREFIX = "fx";
    private static final String FX_ROOT_TYPE_PROPERTY = "type";
    private static final String FX_CONTROLLER = "controller";
    private final ValueResolver valueResolver;
    private final ImportCollection imports;

    public PropertiesParser(ValueResolver valueResolver, ImportCollection imports) {
        this.valueResolver = valueResolver;
        this.imports = imports;
    }

    /**
     * Parses properties of classes given in FXML file. If the properties can not be parsed, a
     * {@link LoadException} is thrown.
     *
     * @param element
     *            whose properties should be parsed
     * @param parent
     *            to whom the properties should be added
     * @return ParsedProperties object containing all properties and other useful stuff. See
     *         {@link ParsedProperties} for more details.
     * @throws LoadException
     *             if the properties could not be parsed, a {@link LoadException} will be thrown
     */
    public ParsedProperties parse(StartElement element, Template parent) throws LoadException {
        PropertyCollection properties = new PropertyCollection(valueResolver, imports);
        Class<?> rootType = findTypeOfRoot(element);
        @SuppressWarnings("unchecked")
        Iterator<Attribute> attributes = element.getAttributes();
        Controller controller = null;
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            QName attributeName = attribute.getName();
            String propertyPrefix = attributeName.getPrefix();
            String propertyName = attributeName.getLocalPart();
            if (FX_ROOT_TYPE_PROPERTY.equals(propertyName) || FX_ROOT.equals(propertyName)) {
                continue;
            }
            String value = attribute.getValue();

            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_CONTROLLER.equals(propertyName)) {
                Class<?> controllerClass = imports.findClass(value);
                controller = newFxController(controllerClass);
                continue;
            }

            if (properties.add(propertyPrefix, propertyName, value, parent, rootType)) {
                continue;
            }
            // TODO check whether Builder is possible on fx:root or not.
            throw new LoadException(
                    "Property specified on fx:root element which can not be set by setter:"
                            + attributeName);
        }
        // return properties;
        return new ParsedProperties(properties, controller, rootType);
    }

	private Controller newFxController(Class<?> controllerClass) {
		if (ReflectionUtils.isInitializable(controllerClass)) {
			Method initializeMethod = ReflectionUtils.findInitializeMethod(controllerClass);
			return new FxInitializableControllerTemplate(controllerClass, initializeMethod);
		}
		return new FxControllerTemplate(controllerClass);
	}

    @SuppressWarnings("unchecked")
    private Class<?> findTypeOfRoot(StartElement element) throws LoadException {
        Iterator<Attribute> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (FX_ROOT_TYPE_PROPERTY.equals(attribute.getName().getLocalPart())) {
                return imports.findClass(attribute.getValue());
            }
        }
        throw new LoadException("Type attribute of fx:root element missing.");
    }

    /**
     * TODO: refactor.
     */
    public ParsedProperties parseClass(StartElement element, String className, Template parent)
            throws LoadException {
        Class<?> clazz = imports.findClass(className);
        PropertyCollection properties = new PropertyCollection(valueResolver, imports);
        @SuppressWarnings("unchecked")
        Iterator<Attribute> attributes = element.getAttributes();
        Controller controller = null;
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            QName attributeName = attribute.getName();
            String propertyPrefix = attributeName.getPrefix();
            String propertyName = attributeName.getLocalPart();
            String value = attribute.getValue();

            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_CONTROLLER.equals(propertyName)) {
                Class<?> controllerClass = imports.findClass(value);
                controller = newFxController(controllerClass);
                continue;
            }

            properties.add(propertyPrefix, propertyName, value, parent, clazz);
        }
        return new ParsedProperties(properties, controller, clazz);
    }
}
