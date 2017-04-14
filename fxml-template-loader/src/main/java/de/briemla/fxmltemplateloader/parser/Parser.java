package de.briemla.fxmltemplateloader.parser;

import static de.briemla.fxmltemplateloader.util.CodeSugar.from;

import java.io.InputStream;
import java.lang.reflect.Constructor;
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

import de.briemla.fxmltemplateloader.FxmlTemplateLoader;
import de.briemla.fxmltemplateloader.InstatiatedController;
import de.briemla.fxmltemplateloader.PropertyCollection;
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
import javafx.fxml.LoadException;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class Parser {
	
    private static final String FX_ROOT = "root";
    private static final String FX_NAMESPACE_PREFIX = "fx";

	private final ImportCollection imports;
	private final ImportFactory factory;
	private final BuilderFactory builderFactory;
	private final ValueResolver valueResolver;
	private PropertiesParser propertiesParser;
	private Controller controller;
	
    private Template currentTemplate;
    private XMLEventReader eventReader;
    private ITemplate rootTemplate;
    private boolean isRootElementProcessed;

	public Parser(ImportFactory factory, ImportCollection imports, BuilderFactory builderFactory,
			ValueResolver valueResolver) {
		super();
		this.factory = factory;
		this.imports = imports;
		this.builderFactory = builderFactory;
		this.valueResolver = valueResolver;
	}

	public void setClassLoader(ClassLoader classLoader) {
		factory.setClassLoader(classLoader);
		imports.clear();
		valueResolver.setClassLoader(classLoader);
	}

	public void setResourceBundle(ResourceBundle bundle) {
		valueResolver.setResourceBundle(bundle);
	}

	public void setLocation(URL location) {
		valueResolver.setLocation(location);
	}

	private PropertiesParser propertiesParser() {
		return propertiesParser;
	}

	private void newPropertiesParser() {
		propertiesParser = new PropertiesParser(valueResolver, imports);
	}

	public void setController(Object controller) {
		this.controller = new InstatiatedController(controller);
	}

	public Controller controller() {
		return controller;
	}

	public ITemplate doLoadTemplate(InputStream xmlInput)
			throws FactoryConfigurationError, XMLStreamException, NoSuchMethodException, LoadException {
		correctClassLoader();
		XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
		eventReader = xmlFactory.createXMLEventReader(from(xmlInput));
		return parseXml();
	}

	private void correctClassLoader() {
		if (factory.hasClassLoader() && valueResolver.hasClassLoader()) {
			return;
		}
		setClassLoader(FxmlTemplateLoader.class.getClassLoader());
	}

    private ITemplate parseXml()
            throws XMLStreamException, NoSuchMethodException, SecurityException, LoadException {
        newPropertiesParser();
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
            rootTemplate = wrap(fxRootTemplate, controller());
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
            rootTemplate = wrap(instantiationTemplate, controller());
            rootElementProcessed();
        }
    }

    private void rootElementProcessed() {
        isRootElementProcessed = true;
    }

	private static ITemplate wrap(InstantiationTemplate instantiationTemplate, Controller controller) {
        return new RootTemplate(instantiationTemplate, controller);
    }

    private FxRootTemplate createFxRootTemplate(StartElement element) throws LoadException {
        ParsedProperties parse = propertiesParser().parse(element, currentTemplate);
        if (controller != null && parse.controller() != null) {
            // TODO add file path and line number
            throw new LoadException("Controller value already specified.");
        }
        if (isRootElementProcessed && parse.controller() != null) {
            // TODO introduce file name and line number of fxml file.
            throw new LoadException("fx:controller can only be applied to root element.");
        }
        if (parse.controller() != null) {
            controller = parse.controller();
        }
        return new FxRootTemplate(parse.rootType(), parse.properties());
    }

    // FIXME too long method. Can be simplified. Maybe move creation of Contructor/BuilderTemplate
    // into special Collection, which collects settable and
    // unsettable properties
    private InstantiationTemplate createInstatiationTemplate(StartElement element, String className)
            throws NoSuchMethodException, SecurityException, LoadException {
        ParsedProperties parse = propertiesParser().parseClass(element, className, currentTemplate);
        if (controller != null && parse.controller() != null) {
            // TODO add file path and line number
            throw new LoadException("Controller value already specified.");
        }
        if (isRootElementProcessed && parse.controller() != null) {
            // TODO introduce file name and line number of fxml file.
            throw new LoadException("fx:controller can only be applied to root element.");
        }
        if (parse.controller() != null) {
            controller = parse.controller();
        }

        PropertyCollection properties = parse.properties();
        Class<?> rootType = parse.rootType();

        if (!properties.hasUnsettable() && rootType.getConstructor() != null) {
            Constructor<?> constructor = rootType.getConstructor();
            return new ConstructorTemplate(currentTemplate, constructor, properties);
        }
        List<IProperty> unsettableConvertedProperties = new ArrayList<>();
        Builder<?> builder = builderFactory.getBuilder(rootType);
        for (Property property : properties.unsettable()) {
            IProperty newPropertyTemplate = property.createTemplate(builder, valueResolver);
            if (newPropertyTemplate != null) {
                unsettableConvertedProperties.add(newPropertyTemplate);
            }
        }
        return new BuilderTemplate(currentTemplate, properties, builderFactory,
                unsettableConvertedProperties, rootType);
    }

    private void processProcessingInstruction(ProcessingInstruction instruction) {
        imports.add(instruction);
    }

}
