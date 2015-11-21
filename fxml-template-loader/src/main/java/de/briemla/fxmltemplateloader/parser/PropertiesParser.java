package de.briemla.fxmltemplateloader.parser;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;
import static de.briemla.fxmltemplateloader.util.ReflectionUtils.extractType;
import static de.briemla.fxmltemplateloader.util.ReflectionUtils.findSetter;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.PropertyCollection;
import de.briemla.fxmltemplateloader.template.Controller;
import de.briemla.fxmltemplateloader.template.FxControllerTemplate;
import de.briemla.fxmltemplateloader.template.FxIdPropertyTemplate;
import de.briemla.fxmltemplateloader.template.ListPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Property;
import de.briemla.fxmltemplateloader.template.PropertyTemplate;
import de.briemla.fxmltemplateloader.template.SingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.StaticPropertyTemplate;
import de.briemla.fxmltemplateloader.template.StaticSingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Template;
import de.briemla.fxmltemplateloader.util.ReflectionUtils;
import de.briemla.fxmltemplateloader.value.BasicTypeValue;
import de.briemla.fxmltemplateloader.value.IValue;

public class PropertiesParser {

    private static final String FX_ROOT = "root";
    private static final String FX_ID_PROPERTY = "id";
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
        PropertyCollection properties = new PropertyCollection();
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

            // FIXME clean up this if statement, because it does not fit to the other properties.
            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_ID_PROPERTY.equals(propertyName)) {
                IValue convertedValue = resolve(value, to(String.class));
                Method fxIdSetter = null;
                if (ReflectionUtils.hasSetter(rootType, propertyName)) {
                    fxIdSetter = findSetter(rootType, propertyName);
                }
                FxIdPropertyTemplate property = new FxIdPropertyTemplate(parent, fxIdSetter,
                        convertedValue);
                properties.add(property);
                continue;
            }
            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_CONTROLLER.equals(propertyName)) {
                Class<?> controllerClass = imports.findClass(value);
                controller = new FxControllerTemplate(controllerClass);
                continue;
            }
            // FIXME clean up duplication
            if (ReflectionUtils.hasSetter(rootType, propertyName)) {
                Method method = findSetter(rootType, propertyName);
                Class<?> type = extractType(method);
                IValue convertedValue = resolve(value, to(type));

                SingleElementPropertyTemplate property = new SingleElementPropertyTemplate(parent,
                        method);
                property.prepare(new PropertyTemplate(method, convertedValue));
                properties.add(property);
                continue;
            }

            if (ReflectionUtils.hasGetter(rootType, propertyName)) {
                Method getter = ReflectionUtils.findGetter(rootType, propertyName);
                if (List.class.isAssignableFrom(getter.getReturnType())) {
                    IValue convertedValue = new BasicTypeValue(value);

                    ListPropertyTemplate property = new ListPropertyTemplate(parent, getter);
                    property.prepare(new PropertyTemplate(getter, convertedValue));
                    properties.add(property);
                    continue;
                }
            }
            throw new LoadException(
                    "Property specified on fx:root element which can not be set by setter:"
                            + attributeName);
        }
        // return properties;
        return new ParsedProperties(properties, controller, rootType);
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

    private IValue resolve(String value, Class<?> type) throws LoadException {
        return resolve(value, type, false);
    }

    // FIXME find better solution than boolean flag
    private IValue resolve(String value, Class<?> type, boolean isTextProperty)
            throws LoadException {
        return valueResolver.resolve(value, type, isTextProperty);
    }

    public ParsedProperties parseClass(StartElement element, String className,
            Template currentTemplate) throws LoadException {
        Class<?> clazz = imports.findClass(className);
        PropertyCollection properties = new PropertyCollection();
        @SuppressWarnings("unchecked")
        Iterator<Attribute> attributes = element.getAttributes();
        Controller controller = null;
        while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            QName attributeName = attribute.getName();
            String propertyPrefix = attributeName.getPrefix();
            String propertyName = attributeName.getLocalPart();
            String value = attribute.getValue();

            // FIXME clean up this if statement, because it does not fit to the other properties.
            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_ID_PROPERTY.equals(propertyName)) {
                IValue convertedValue = resolve(value, to(String.class));
                // FIXME clean up null pointer
                Method fxIdSetter = null;
                if (ReflectionUtils.hasSetter(clazz, propertyName)) {
                    fxIdSetter = findSetter(clazz, propertyName);
                }
                FxIdPropertyTemplate property = new FxIdPropertyTemplate(currentTemplate,
                        fxIdSetter, convertedValue);
                properties.add(property);
                continue;
            }

            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_CONTROLLER.equals(propertyName)) {
                Class<?> controllerClass = imports.findClass(value);
                controller = new FxControllerTemplate(controllerClass);
                continue;
            }

            // FIXME clean up duplication
            if (ReflectionUtils.hasSetter(clazz, propertyName)) {
                Method method = findSetter(clazz, propertyName);
                Class<?> type = extractType(method);
                IValue convertedValue = resolve(value, to(type), "text".equals(propertyName));

                SingleElementPropertyTemplate property = new SingleElementPropertyTemplate(
                        currentTemplate, method);
                property.prepare(new PropertyTemplate(method, convertedValue));
                properties.add(property);
                continue;
            }

            if (ReflectionUtils.hasGetter(clazz, propertyName)) {
                Method getter = ReflectionUtils.findGetter(clazz, propertyName);
                if (List.class.isAssignableFrom(getter.getReturnType())) {
                    IValue convertedValue = new BasicTypeValue(value);

                    ListPropertyTemplate property = new ListPropertyTemplate(currentTemplate,
                            getter);
                    property.prepare(new PropertyTemplate(getter, convertedValue));
                    properties.add(property);
                    continue;
                }
            }
            if (propertyName.contains(".")) {
                int lastIndexOf = propertyName.lastIndexOf(".");
                String staticPropertyClassName = propertyName.substring(0, lastIndexOf);
                String staticPropertyName = propertyName.substring(lastIndexOf + 1);
                Class<?> staticPropertyClass = imports.findClass(staticPropertyClassName);
                if (ReflectionUtils.hasSetter(staticPropertyClass, staticPropertyName)) {
                    Method method = findSetter(staticPropertyClass, staticPropertyName);
                    Class<?> type = extractType(method);
                    IValue convertedValue = resolve(value, to(type));

                    StaticSingleElementPropertyTemplate property = new StaticSingleElementPropertyTemplate(
                            currentTemplate, method, staticPropertyClass);
                    property.prepare(new StaticPropertyTemplate(staticPropertyClass, method,
                            convertedValue));
                    properties.add(property);
                    continue;
                }
            }
            properties.addUnsettable(new Property(propertyName, value));
        }
        return new ParsedProperties(properties, controller, clazz);
    }
}
