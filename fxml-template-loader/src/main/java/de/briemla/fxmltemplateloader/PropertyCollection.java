package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;
import static de.briemla.fxmltemplateloader.util.ReflectionUtils.extractType;
import static de.briemla.fxmltemplateloader.util.ReflectionUtils.findSetter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.parser.ImportCollection;
import de.briemla.fxmltemplateloader.parser.ValueResolver;
import de.briemla.fxmltemplateloader.template.FxIdPropertyTemplate;
import de.briemla.fxmltemplateloader.template.IProperty;
import de.briemla.fxmltemplateloader.template.ListPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Property;
import de.briemla.fxmltemplateloader.template.PropertyTemplate;
import de.briemla.fxmltemplateloader.template.SingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.StaticPropertyTemplate;
import de.briemla.fxmltemplateloader.template.StaticSingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Template;
import de.briemla.fxmltemplateloader.template.TemplateRegistry;
import de.briemla.fxmltemplateloader.util.ReflectionUtils;
import de.briemla.fxmltemplateloader.value.BasicTypeValue;
import de.briemla.fxmltemplateloader.value.IValue;

public class PropertyCollection {

    private static final String FX_ID_PROPERTY = "id";
    private static final String FX_NAMESPACE_PREFIX = "fx";

    private final List<IProperty> properties;
    private final List<Property> unsettable;
    private final ValueResolver valueResolver;
    private final ImportCollection imports;

    /**
     * Contains a bunch of properties.
     *
     * @param valueResolver
     *            used to resolve {@link String}s from {@link FXML}
     * @param imports
     *            used to resolve {@link Class}es for properties
     */
    public PropertyCollection(ValueResolver valueResolver, ImportCollection imports) {
        super();
        this.valueResolver = valueResolver;
        this.imports = imports;
        properties = new ArrayList<>();
        unsettable = new ArrayList<>();
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
     */
    public void apply(Object object, TemplateRegistry registry)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException, LoadException {
        for (IProperty child : properties) {
            child.apply(object, registry);
        }
    }

    public void add(IProperty property) {
        properties.add(property);
    }

    /**
     * TODO: needs refactoring.
     */
    public boolean add(String propertyPrefix, String propertyName, String value, Template parent,
            Class<?> clazz) throws LoadException {

        // FIXME clean up this if statement, because it does not fit to the other properties.
        if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_ID_PROPERTY.equals(propertyName)) {
            properties.add(fxIdProperty(parent, clazz, propertyName, value));
            return true;
        }

        // FIXME clean up duplication
        if (ReflectionUtils.hasSetter(clazz, propertyName)) {
            properties.add(singleElement(parent, clazz, propertyName, value));
            return true;
        }

        if (ReflectionUtils.hasGetter(clazz, propertyName)) {
            Method getter = ReflectionUtils.findGetter(clazz, propertyName);
            if (List.class.isAssignableFrom(getter.getReturnType())) {
                properties.add(listProperty(parent, value, getter));
                return true;
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

                StaticSingleElementPropertyTemplate property = 
                        new StaticSingleElementPropertyTemplate(
                        parent, method, staticPropertyClass);
                property.prepare(
                        new StaticPropertyTemplate(staticPropertyClass, method, convertedValue));
                properties.add(property);
                return true;
            }
        }
        addUnsettable(new Property(propertyName, value));
        return false;
    }

    private ListPropertyTemplate listProperty(Template parent, String value, Method getter) {
        IValue convertedValue = new BasicTypeValue(value);

        ListPropertyTemplate property = new ListPropertyTemplate(parent, getter);
        property.prepare(new PropertyTemplate(getter, convertedValue));
        return property;
    }

    private FxIdPropertyTemplate fxIdProperty(Template parent, Class<?> rootType,
            String propertyName, String value) throws LoadException {
        IValue convertedValue = resolve(value, to(String.class));
        Method fxIdSetter = null;
        if (ReflectionUtils.hasSetter(rootType, propertyName)) {
            fxIdSetter = findSetter(rootType, propertyName);
        }
        FxIdPropertyTemplate property = new FxIdPropertyTemplate(parent, fxIdSetter,
                convertedValue);
        return property;
    }

    private SingleElementPropertyTemplate singleElement(Template parent, Class<?> clazz,
            String propertyName, String value) throws LoadException {
        Method method = findSetter(clazz, propertyName);
        Class<?> type = extractType(method);
        IValue convertedValue = resolve(value, to(type), "text".equals(propertyName));

        SingleElementPropertyTemplate property = new SingleElementPropertyTemplate(parent, method);
        property.prepare(new PropertyTemplate(method, convertedValue));
        return property;
    }

    private IValue resolve(String value, Class<?> type) throws LoadException {
        return resolve(value, type, false);
    }

    // FIXME find better solution than boolean flag
    private IValue resolve(String value, Class<?> type, boolean isTextProperty)
            throws LoadException {
        return valueResolver.resolve(value, type, isTextProperty);
    }

}
