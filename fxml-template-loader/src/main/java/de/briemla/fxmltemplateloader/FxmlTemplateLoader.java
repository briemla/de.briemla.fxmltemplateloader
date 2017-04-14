package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.from;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import de.briemla.fxmltemplateloader.parser.ImportCollection;
import de.briemla.fxmltemplateloader.parser.ImportFactory;
import de.briemla.fxmltemplateloader.parser.ParsedProperties;
import de.briemla.fxmltemplateloader.parser.Parser;
import de.briemla.fxmltemplateloader.parser.ValueResolver;
import de.briemla.fxmltemplateloader.template.BuilderTemplate;
import de.briemla.fxmltemplateloader.template.ConstructorTemplate;
import de.briemla.fxmltemplateloader.template.Controller;
import de.briemla.fxmltemplateloader.template.FxRootTemplate;
import de.briemla.fxmltemplateloader.template.IProperty;
import de.briemla.fxmltemplateloader.template.ITemplate;
import de.briemla.fxmltemplateloader.template.InstantiationTemplate;
import de.briemla.fxmltemplateloader.template.ListPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Property;
import de.briemla.fxmltemplateloader.template.RootTemplate;
import de.briemla.fxmltemplateloader.template.SingleElementPropertyTemplate;
import de.briemla.fxmltemplateloader.template.Template;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.LoadException;
import javafx.util.Builder;

public class FxmlTemplateLoader {

    private static final String FX_ROOT = "root";
    private static final String FX_NAMESPACE_PREFIX = "fx";
    private Template currentTemplate;
    private XMLEventReader eventReader;
    private ITemplate rootTemplate;
    private boolean isRootElementProcessed;
    private Object root;
	private final Parser parser;

    /**
     * Class to load FXML files. The class creates {@link ITemplate} which can be reused to speed up
     * generation of several objects from the same FXML file without reading it several times from
     * the file system.
     */
    public FxmlTemplateLoader() {
        super();
        ImportFactory factory = new ImportFactory(FxmlTemplateLoader.class.getClassLoader());
        ImportCollection imports = new ImportCollection(factory);
        JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
        ValueResolver valueResolver = new ValueResolver();
        parser = new Parser(factory, imports, builderFactory, valueResolver);
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
    	parser.setResourceBundle(bundle);
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
        parser.setClassLoader(classLoader);
    }

    public void setController(Object controller) {
        parser.setController(controller);
    }

    public void setLocation(URL location) {
    	parser.setLocation(location);
    }
    
    public void setRoot(Object root) {
    	this.root = root;
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
            if (parser.controller() != null) {
                return doLoadTemplate(resource).create(parser.controller());
            }
            return doLoadTemplate(resource).create();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new IOException("Could not instatiate Nodes.", exception);
        }
    }
    
    public static ITemplate loadTemplate(URL resource) throws IOException {
    	return new FxmlTemplateLoader().doLoadTemplate(resource);
    }
    
    private ITemplate doLoadTemplate(URL resource) throws IOException {
    	setLocation(resource);
    	try (InputStream xmlInput = resource.openStream()) {
    		return loadTemplate(xmlInput);
    	}
    }

	public <T> T load(InputStream inputStream) throws IOException {
		try {
			return loadTemplate(inputStream).create();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
			throw new IOException("Could not instatiate Nodes.", exception);
		}
	}

	public ITemplate loadTemplate(InputStream xmlInput)
			throws IOException {
		try {
			return doLoadTemplate(xmlInput);
		} catch (NoSuchMethodException | SecurityException exception) {
			throw new IOException("Could not find correct classes.", exception);
		} catch (XMLStreamException exception) {
			throw new IOException("Could not parse XML.", exception);
		} catch (IllegalArgumentException exception) {
    		throw new IOException("Something went wrong with the arguments.", exception);
    	}
	}

	private ITemplate doLoadTemplate(InputStream xmlInput)
			throws FactoryConfigurationError, XMLStreamException, NoSuchMethodException, LoadException {
		correctClassLoader();
		XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
		eventReader = xmlFactory.createXMLEventReader(from(xmlInput));
		ITemplate template = parseXml();
		if (null != root) {
			template.setRoot(root);
		}
		return template;
	}

    private void correctClassLoader() {
    	parser.correctClassLoader();
    }

    private ITemplate parseXml()
            throws XMLStreamException, NoSuchMethodException, SecurityException, LoadException {
        parser.newPropertiesParser();
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
            rootTemplate = wrap(fxRootTemplate, parser.controller());
            rootElementProcessed();
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
            rootTemplate = wrap(instantiationTemplate, parser.controller());
            rootElementProcessed();
        }
    }

    private void rootElementProcessed() {
        isRootElementProcessed = true;
    }

    private static ITemplate wrap(InstantiationTemplate instantiationTemplate,
            Controller controller) {
        return new RootTemplate(instantiationTemplate, controller);
    }

    private FxRootTemplate createFxRootTemplate(StartElement element) throws LoadException {
        ParsedProperties parse = parser.propertiesParser().parse(element, currentTemplate);
        if (parser.controller() != null && parse.controller() != null) {
            // TODO add file path and line number
            throw new LoadException("Controller value already specified.");
        }
        if (isRootElementProcessed && parse.controller() != null) {
            // TODO introduce file name and line number of fxml file.
            throw new LoadException("fx:controller can only be applied to root element.");
        }
        if (parse.controller() != null) {
            parser.setFxController(parse.controller());
        }
        return new FxRootTemplate(parse.rootType(), parse.properties());
    }

    // FIXME too long method. Can be simplified. Maybe move creation of Contructor/BuilderTemplate
    // into special Collection, which collects settable and
    // unsettable properties
    private InstantiationTemplate createInstatiationTemplate(StartElement element, String className)
            throws NoSuchMethodException, SecurityException, LoadException {
        ParsedProperties parse = parser.propertiesParser().parseClass(element, className, currentTemplate);
        if (parser.controller() != null && parse.controller() != null) {
            // TODO add file path and line number
            throw new LoadException("Controller value already specified.");
        }
        if (isRootElementProcessed && parse.controller() != null) {
            // TODO introduce file name and line number of fxml file.
            throw new LoadException("fx:controller can only be applied to root element.");
        }
        if (parse.controller() != null) {
            parser.setFxController(parse.controller());
        }

        PropertyCollection properties = parse.properties();
        Class<?> rootType = parse.rootType();

        if (!properties.hasUnsettable() && rootType.getConstructor() != null) {
            Constructor<?> constructor = rootType.getConstructor();
            return new ConstructorTemplate(currentTemplate, constructor, properties);
        }
        List<IProperty> unsettableConvertedProperties = new ArrayList<>();
        Builder<?> builder = parser.builderFactory().getBuilder(rootType);
        for (Property property : properties.unsettable()) {
            IProperty newPropertyTemplate = property.createTemplate(builder, parser.valueResolver());
            if (newPropertyTemplate != null) {
                unsettableConvertedProperties.add(newPropertyTemplate);
            }
        }
        return new BuilderTemplate(currentTemplate, properties, parser.builderFactory(),
                unsettableConvertedProperties, rootType);
    }

    private void processProcessingInstruction(ProcessingInstruction instruction) {
        parser.imports().add(instruction);
    }
}
