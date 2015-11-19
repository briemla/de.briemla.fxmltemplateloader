package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.from;
import static de.briemla.fxmltemplateloader.util.CodeSugar.to;
import static de.briemla.fxmltemplateloader.util.ReflectionUtils.extractType;
import static de.briemla.fxmltemplateloader.util.ReflectionUtils.findSetter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.LoadException;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

import de.briemla.fxmltemplateloader.parser.ImportCollection;
import de.briemla.fxmltemplateloader.parser.ImportFactory;
import de.briemla.fxmltemplateloader.parser.ValueResolver;
import de.briemla.fxmltemplateloader.template.BuilderTemplate;
import de.briemla.fxmltemplateloader.template.ConstructorTemplate;
import de.briemla.fxmltemplateloader.template.Controller;
import de.briemla.fxmltemplateloader.template.FxControllerTemplate;
import de.briemla.fxmltemplateloader.template.FxIdPropertyTemplate;
import de.briemla.fxmltemplateloader.template.FxRootTemplate;
import de.briemla.fxmltemplateloader.template.IProperty;
import de.briemla.fxmltemplateloader.template.ITemplate;
import de.briemla.fxmltemplateloader.template.InstantiationTemplate;
import de.briemla.fxmltemplateloader.template.ListPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Property;
import de.briemla.fxmltemplateloader.template.PropertyTemplate;
import de.briemla.fxmltemplateloader.template.RootTemplate;
import de.briemla.fxmltemplateloader.template.SingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.StaticPropertyTemplate;
import de.briemla.fxmltemplateloader.template.StaticSingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Template;
import de.briemla.fxmltemplateloader.util.ReflectionUtils;
import de.briemla.fxmltemplateloader.value.BasicTypeValue;
import de.briemla.fxmltemplateloader.value.IValue;

public class FxmlTemplateLoader {

    private static final String FX_ROOT = "root";
    private static final String FX_ID_PROPERTY = "id";
    private static final String FX_NAMESPACE_PREFIX = "fx";
    private static final String FX_ROOT_TYPE_PROPERTY = "type";
    private static final String FX_CONTROLLER = "controller";
    private Template currentTemplate;
    private final ImportFactory factory;
    private final ImportCollection imports;
    private final BuilderFactory builderFactory;
    private final ValueResolver valueResolver;
    private XMLEventReader eventReader;
    private ITemplate rootTemplate;
    private Controller controller;

    /**
     * Class to load FXML files. The class creates {@link ITemplate} which can be reused to speed up
     * generation of several objects from the same FXML file without reading it several times from
     * the file system.
     */
    public FxmlTemplateLoader() {
        super();
        factory = new ImportFactory(FxmlTemplateLoader.class.getClassLoader());
        imports = new ImportCollection(factory);
        builderFactory = new JavaFXBuilderFactory();
        valueResolver = new ValueResolver();
    }

    public static <T> T load(URL resource) throws IOException {
        return new FxmlTemplateLoader().doLoad(resource);
    }

    /**
     * Load the given resource as FXML and return the root element.
     *
     * @param resource
     *            {@link URL} to FXML file
     * @param bundle
     *            {@link ResourceBundle} to retrieve language specific texts from
     * @return root element created from FXML file
     * @throws IOException
     *             in case the file can not be loaded or can not be parsed
     */
    public static <T> T load(URL resource, ResourceBundle bundle) throws IOException {
        FxmlTemplateLoader fxmlTemplateLoader = new FxmlTemplateLoader();
        fxmlTemplateLoader.setResourceBundle(bundle);
        return fxmlTemplateLoader.doLoad(resource);
    }

    private void setResourceBundle(ResourceBundle bundle) {
        valueResolver.setResourceBundle(bundle);
    }

    /**
     * This method changes the classLoader on an necessary objects.
     *
     * @param classLoader
     *            new {@link ClassLoader} instance. Not allowed to be <code>null</code>
     */
    public void setClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException();
        }
        // FIXME maybe move this into ImportCollection
        factory.setClassLoader(classLoader);
        imports.clear();
        valueResolver.setClassLoader(classLoader);
    }

    public void setController(Object controller) {
        this.controller = new InstatiatedController(controller);
    }

    public void setLocation(URL location) {
        valueResolver.setLocation(location);
    }

    /**
     * Load the given resource as FXML and return the root element.
     *
     * @param resource
     *            {@link URL} to FXML file
     * @return root element created from FXML file
     * @throws IOException
     *             in case the file can not be loaded or can not be parsed
     */
    public <T> T doLoad(URL resource) throws IOException {
        try {
            if (controller != null) {
                return doLoadTemplate(resource).create(controller);
            }
            return doLoadTemplate(resource).create();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException exception) {
            throw new IOException("Could not instatiate Nodes.", exception);
        } catch (SecurityException exception) {
            throw new IOException("Could not find correct classes.", exception);
        }
    }

    public static ITemplate loadTemplate(URL resource) throws IOException {
        return new FxmlTemplateLoader().doLoadTemplate(resource);
    }

    private ITemplate doLoadTemplate(URL resource) throws IOException {
        correctClassLoader();
        setLocation(resource);
        XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
        try (InputStream xmlInput = resource.openStream()) {
            eventReader = xmlFactory.createXMLEventReader(from(xmlInput));
            return parseXml();
        } catch (XMLStreamException exception) {
            throw new IOException("Could not parse XML.", exception);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Something went wrong with the arguments.", exception);
        } catch (NoSuchMethodException | SecurityException exception) {
            throw new IOException("Could not find correct classes.", exception);
        }
    }

    private void correctClassLoader() {
        if (factory.hasClassLoader() && valueResolver.hasClassLoader()) {
            return;
        }
        setClassLoader(FxmlTemplateLoader.class.getClassLoader());
    }

    private ITemplate parseXml()
            throws XMLStreamException, NoSuchMethodException, SecurityException, LoadException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isProcessingInstruction()) {
                processProcessingInstruction((ProcessingInstruction) event);
            }
            if (event.isStartElement()) {
                processStartElement(event.asStartElement());
            }
            if (event.isEndElement()) {
                processEndElement();
            }
        }
        return rootTemplate;
    }

    private void processEndElement() {
        currentTemplate = currentTemplate.getParent();
    }

    // FIXME too long method. This method could be splitted into several smaller methods.
    private void processStartElement(StartElement element)
            throws NoSuchMethodException, SecurityException, LoadException {
        QName name = element.getName();
        if (FX_NAMESPACE_PREFIX.equals(name.getPrefix()) && FX_ROOT.equals(name.getLocalPart())) {
            if (rootTemplate != null) {
                throw new LoadException("fx:root element is not the first element.");
            }
            FxRootTemplate fxRootTemplate = createFxRootTemplate(element);
            currentTemplate = fxRootTemplate;
            rootTemplate = wrap(fxRootTemplate, controller);
            return;
        }
        String className = name.getLocalPart();

        int index = className.lastIndexOf('.');
        if (Character.isLowerCase(className.charAt(index + 1))) {
            String propertyName = className.substring(index + 1);
            Method getter = currentTemplate.findGetter(propertyName);
            Class<?> returnType = getter.getReturnType();
            if (returnType == null) {
                throw new RuntimeException(
                        "Found getter without return type for property: " + propertyName);
            }
            if (List.class.isAssignableFrom(returnType)) {
                ListPropertyTemplate listProperty = new ListPropertyTemplate(currentTemplate,
                        getter);
                currentTemplate.prepare(listProperty);
                currentTemplate = listProperty;
                return;
            }
            Method setter = currentTemplate.findSetter(propertyName);
            SingleElementPropertyTemplate singleElementProperty = new SingleElementPropertyTemplate(
                    currentTemplate, setter);
            currentTemplate.prepare(singleElementProperty);
            currentTemplate = singleElementProperty;
            return;
        }

        InstantiationTemplate instantiationTemplate = createInstatiationTemplate(element,
                className);
        if (currentTemplate != null) {
            currentTemplate.prepare(instantiationTemplate);
        }
        currentTemplate = instantiationTemplate;

        if (rootTemplate == null) {
            rootTemplate = wrap(instantiationTemplate, controller);
        }
    }

    private static ITemplate wrap(InstantiationTemplate instantiationTemplate,
            Controller controller) {
        return new RootTemplate(instantiationTemplate, controller);
    }

    @SuppressWarnings("unchecked")
    private FxRootTemplate createFxRootTemplate(StartElement element) throws LoadException {
        Class<?> rootType = findTypeOfRoot(element);
        List<IProperty> properties = new ArrayList<>();
        Iterator<Attribute> attributes = element.getAttributes();
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
                FxIdPropertyTemplate property = new FxIdPropertyTemplate(currentTemplate,
                        fxIdSetter, convertedValue);
                properties.add(property);
                continue;
            }
            if (FX_NAMESPACE_PREFIX.equals(propertyPrefix) && FX_CONTROLLER.equals(propertyName)) {
                if (controller != null) {
                    // TODO add file path and line number
                    throw new LoadException("Controller value already specified.");
                }
                Class<?> controllerClass = imports.findClass(value);
                controller = new FxControllerTemplate(controllerClass);
            }
            // FIXME clean up duplication
            if (ReflectionUtils.hasSetter(rootType, propertyName)) {
                Method method = findSetter(rootType, propertyName);
                Class<?> type = extractType(method);
                IValue convertedValue = resolve(value, to(type));

                SingleElementPropertyTemplate property = new SingleElementPropertyTemplate(
                        currentTemplate, method);
                property.prepare(new PropertyTemplate(method, convertedValue));
                properties.add(property);
                continue;
            }

            if (ReflectionUtils.hasGetter(rootType, propertyName)) {
                Method getter = ReflectionUtils.findGetter(rootType, propertyName);
                if (List.class.isAssignableFrom(getter.getReturnType())) {
                    IValue convertedValue = new BasicTypeValue(value);

                    ListPropertyTemplate property = new ListPropertyTemplate(currentTemplate,
                            getter);
                    property.prepare(new PropertyTemplate(getter, convertedValue));
                    properties.add(property);
                    continue;
                }
            }
            throw new LoadException(
                    "Property specified on fx:root element which can not be set by setter:"
                            + attributeName);
        }
        return new FxRootTemplate(rootType, properties);
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

    @SuppressWarnings("unchecked")
    // FIXME too long method. Can be simplified. Maybe move creation of Contructor/BuilderTemplate
    // into special Collection, which collects settable and
    // unsettable properties
    private InstantiationTemplate createInstatiationTemplate(StartElement element, String className)
            throws NoSuchMethodException, SecurityException, LoadException {
        Class<?> clazz = imports.findClass(className);
        List<IProperty> properties = new ArrayList<>();
        List<Property> unsettableProperties = new ArrayList<>();
        Iterator<Attribute> attributes = element.getAttributes();
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
                if (controller != null) {
                    // TODO add file path and line number
                    throw new LoadException("Controller value already specified.");
                }
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
            unsettableProperties.add(new Property(propertyName, value));
        }
        if (unsettableProperties.isEmpty() && clazz.getConstructor() != null) {
            Constructor<?> constructor = clazz.getConstructor();
            return new ConstructorTemplate(currentTemplate, constructor, properties);
        }
        List<IProperty> unsettableConvertedProperties = new ArrayList<>();
        Builder<?> builder = builderFactory.getBuilder(clazz);
        for (Property property : unsettableProperties) {
            IProperty newPropertyTemplate = property.createTemplate(builder, valueResolver);
            if (newPropertyTemplate != null) {
                unsettableConvertedProperties.add(newPropertyTemplate);
            }
        }
        return new BuilderTemplate(currentTemplate, properties, builderFactory,
                unsettableConvertedProperties, clazz);
    }

    private IValue resolve(String value, Class<?> type) throws LoadException {
        return resolve(value, type, false);
    }

    // FIXME find better solution than boolean flag
    private IValue resolve(String value, Class<?> type, boolean isTextProperty)
            throws LoadException {
        return valueResolver.resolve(value, type, isTextProperty);
    }

    private void processProcessingInstruction(ProcessingInstruction instruction) {
        imports.add(instruction);
    }
}