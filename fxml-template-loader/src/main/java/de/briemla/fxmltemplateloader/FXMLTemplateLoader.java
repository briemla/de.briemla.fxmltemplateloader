package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.ReflectionUtils.extractType;
import static de.briemla.fxmltemplateloader.ReflectionUtils.findSetter;
import static de.briemla.fxmltemplateloader.util.CodeSugar.from;
import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

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

import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.LoadException;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class FXMLTemplateLoader {

	private static Template currentTemplate;
	private final ImportFactory factory;
	private final ImportCollection imports;
	private final BuilderFactory builderFactory;
	private final ValueResolver valueResolver;
	private XMLEventReader eventReader;
	private ITemplate rootTemplate;
	private Object controller;

	public FXMLTemplateLoader() {
		super();
		factory = new ImportFactory(FXMLTemplateLoader.class.getClassLoader());
		imports = new ImportCollection(factory);
		builderFactory = new JavaFXBuilderFactory();
		valueResolver = new ValueResolver();
	}

	public static <T> T load(URL resource) throws IOException {
		return new FXMLTemplateLoader().doLoad(resource);
	}

	public static <T> T load(URL resource, ResourceBundle bundle) throws IOException {
		FXMLTemplateLoader fxmlTemplateLoader = new FXMLTemplateLoader();
		fxmlTemplateLoader.setResourceBundle(bundle);
		return fxmlTemplateLoader.doLoad(resource);
	}

	private void setResourceBundle(ResourceBundle bundle) {
		valueResolver.setResourceBundle(bundle);
	}

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
		this.controller = controller;
	}

	public void setLocation(URL location) {
		valueResolver.setLocation(location);
	}

	public <T> T doLoad(URL resource) throws IOException {
		try {
			if (controller != null) {
				return doLoadTemplate(resource).create(controller);
			}
			return doLoadTemplate(resource).create();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new IOException("Could not instatiate Nodes.", exception);
		} catch (SecurityException exception) {
			throw new IOException("Could not find correct classes.", exception);
		}
	}

	public static ITemplate loadTemplate(URL resource) throws IOException {
		return new FXMLTemplateLoader().doLoadTemplate(resource);
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
		setClassLoader(FXMLTemplateLoader.class.getClassLoader());
	}

	private ITemplate parseXml() throws XMLStreamException, NoSuchMethodException, SecurityException, LoadException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isProcessingInstruction()) {
				processProcessingInstruction((ProcessingInstruction) event);
			}
			if (event.isStartElement()) {
				processStartElement(event.asStartElement());
			}
			if (event.isEndElement()) {
				processEndElement(event.asEndElement());
			}
		}
		return rootTemplate;
	}

	private void processEndElement(EndElement element) {
		currentTemplate = currentTemplate.getParent();
	}

	private void processStartElement(StartElement element) throws NoSuchMethodException, SecurityException, LoadException {
		String className = element.getName().getLocalPart();

		int index = className.lastIndexOf('.');
		if (Character.isLowerCase(className.charAt(index + 1))) {
			String propertyName = className.substring(index + 1);
			Method getter = currentTemplate.findGetter(propertyName);
			Class<?> returnType = getter.getReturnType();
			if (returnType == null) {
				throw new RuntimeException("Found getter without return type for property: " + propertyName);
			}
			if (List.class.isAssignableFrom(returnType)) {
				ListPropertyTemplate listProperty = new ListPropertyTemplate(currentTemplate, getter);
				currentTemplate.prepare(listProperty);
				currentTemplate = listProperty;
				return;
			}
			Method setter = currentTemplate.findSetter(propertyName);
			SingleElementPropertyTemplate singleElementProperty = new SingleElementPropertyTemplate(currentTemplate, setter);
			currentTemplate.prepare(singleElementProperty);
			currentTemplate = singleElementProperty;
			return;
		}

		InstantiationTemplate instantiationTemplate = createInstatiationTemplate(element, className);
		if (currentTemplate != null) {
			currentTemplate.prepare(instantiationTemplate);
		}
		currentTemplate = instantiationTemplate;

		if (rootTemplate == null) {
			rootTemplate = wrap(instantiationTemplate);
		}
	}

	private ITemplate wrap(InstantiationTemplate instantiationTemplate) {
		return new RootTemplate(instantiationTemplate);
	}

	@SuppressWarnings("unchecked")
	// FIXME too long method. Can be simplified. Maybe move creation of Contructor/BuilderTemplate into special Collection, which collects settable and
	// unsettable properties
	private InstantiationTemplate createInstatiationTemplate(StartElement element, String className) throws NoSuchMethodException, SecurityException,
	LoadException {
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
			if ("fx".equals(propertyPrefix) && "id".equals(propertyName)) {
				IValue convertedValue = resolve(value, to(String.class));
				FxIdPropertyTemplate property = new FxIdPropertyTemplate(currentTemplate, convertedValue);
				properties.add(property);
				continue;
			}

			if (ReflectionUtils.hasSetter(clazz, propertyName)) {
				Method method = findSetter(clazz, propertyName);
				Class<?> type = extractType(method);
				IValue convertedValue = resolve(value, to(type));

				SingleElementPropertyTemplate property = new SingleElementPropertyTemplate(currentTemplate, method);
				property.prepare(new PropertyTemplate(method, convertedValue));
				properties.add(property);
				continue;
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
		return new BuilderTemplate(currentTemplate, properties, builderFactory, unsettableConvertedProperties, clazz);
	}

	private IValue resolve(String value, Class<?> type) throws LoadException {
		return valueResolver.resolve(value, type);
	}

	private void processProcessingInstruction(ProcessingInstruction instruction) {
		imports.add(instruction);
	}
}
