package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.from;
import static de.briemla.fxmltemplateloader.util.CodeSugar.to;
import static de.briemla.fxmltemplateloader.util.TypeUtil.convert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class FXMLTemplateLoader {

	private static final String WILDCARD_MATCH = ".*";
	private static final String IMPORT = "import";
	private final List<String> imports;
	private static Template currentTemplate;
	private XMLEventReader eventReader;
	private ITemplate rootTemplate;

	public FXMLTemplateLoader() {
		super();
		imports = new ArrayList<>();
	}

	public static <T> T load(URL resource) throws IOException {
		return new FXMLTemplateLoader().doLoad(resource);
	}

	private <T> T doLoad(URL resource) throws IOException {
		XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
		try (InputStream xmlInput = resource.openStream()) {
			eventReader = xmlFactory.createXMLEventReader(from(xmlInput));
			return parseXml().create();
		} catch (XMLStreamException exception) {
			throw new IOException("Could not parse XML", exception);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new IOException("Could not instatiate Nodes", exception);
		}
	}

	private ITemplate parseXml() throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isProcessingInstruction()) {
				processProcessingInstruction((ProcessingInstruction) event);
			}
			if (event.isStartElement()) {
				processStartElement(event.asStartElement());
			}
		}
		return rootTemplate;
	}

	private void processStartElement(StartElement element) {
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
				currentTemplate.addProperty(listProperty);
				currentTemplate = listProperty;
			}
			return;
		}

		Class<?> clazz = findClass(className);
		List<IProperty> properties = findProperties(element, clazz);
		InstantiationTemplate instantiationTemplate = new InstantiationTemplate(currentTemplate, clazz, properties);
		if (currentTemplate != null) {
			currentTemplate.addProperty(instantiationTemplate);
		}
		currentTemplate = instantiationTemplate;

		if (rootTemplate == null) {
			rootTemplate = instantiationTemplate;
		}
	}

	@SuppressWarnings({ "unchecked" })
	private static List<IProperty> findProperties(StartElement element, Class<?> clazz) {
		List<IProperty> properties = new ArrayList<>();
		Iterator<Attribute> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			String propertyName = attribute.getName().getLocalPart();
			Method method = ReflectionUtils.findSetter(clazz, propertyName);
			Class<?> type = extractType(method);
			String value = attribute.getValue();
			Object convertedValue = convert(value, to(type));

			IProperty property = new PropertyTemplate(method, convertedValue);
			properties.add(property);
		}
		return properties;
	}

	private static Class<?> extractType(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			throw new RuntimeException("Incorrect number of arguments for setter found.");
		}
		return parameterTypes[0];
	}

	private Class<?> findClass(String className) {
		for (String importQualifier : imports) {
			if (matches(className, importQualifier)) {
				return load(importQualifier);
			}
			if (isWildcard(importQualifier)) {
				return load(importQualifier, className);
			}
		}
		throw new RuntimeException("Could not find class for name: " + className);
	}

	private static boolean matches(String className, String importQualifier) {
		return importQualifier.endsWith(className);
	}

	private static Class<?> load(String importQualifier) {
		ClassLoader classLoader = FXMLTemplateLoader.class.getClassLoader();
		try {
			return classLoader.loadClass(importQualifier);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load import: " + importQualifier);
		}
	}

	private static boolean isWildcard(String importQualifier) {
		return importQualifier.endsWith(WILDCARD_MATCH);
	}

	private static Class<?> load(String importQualifier, String className) {
		int indexBeforeWildcard = importQualifier.length() - 1;
		String removedWildcard = importQualifier.substring(0, indexBeforeWildcard);
		String fullQualifiedImport = removedWildcard + className;
		return load(fullQualifiedImport);
	}

	private void processProcessingInstruction(ProcessingInstruction instruction) {
		if (!IMPORT.equals(instruction.getTarget())) {
			return;
		}
		imports.add(instruction.getData());
	}
}
