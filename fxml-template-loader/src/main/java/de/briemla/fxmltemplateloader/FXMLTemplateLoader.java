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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	private Template rootTemplate;

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

	private Template parseXml() throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isProcessingInstruction()) {
				parseProcessingInstruction((ProcessingInstruction) event);
			}
			if (event.isStartElement()) {
				parseStartElement(event.asStartElement());
			}
		}
		return rootTemplate;
	}

	private void parseStartElement(StartElement element) {
		String className = element.getName().getLocalPart();
		Class<?> clazz = findClass(className);
		Map<Method, Object> properties = findProperties(element, clazz);
		currentTemplate = new Template(currentTemplate, clazz, properties);

		if (rootTemplate == null) {
			rootTemplate = currentTemplate;
		}
	}

	@SuppressWarnings({ "unchecked" })
	private static Map<Method, Object> findProperties(StartElement element, Class<?> clazz) {
		HashMap<Method, Object> properties = new HashMap<>();
		Iterator<Attribute> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			Method method = findSetter(clazz, attribute);
			Class<?> type = extractType(method);
			String value = attribute.getValue();
			Object convertedValue = convert(value, to(type));
			properties.put(method, convertedValue);
		}
		return properties;
	}

	private static Class<?> extractType(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			throw new RuntimeException("Incorrect number of arguments for setter found.");
		}
		Class<?> type = parameterTypes[0];
		return type;
	}

	private static Method findSetter(Class<?> clazz, Attribute attribute) {
		String propertyName = attribute.getName().getLocalPart();
		String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
		for (Method method : clazz.getMethods()) {
			if (setterName.equals(method.getName())) {
				return method;
			}
		}
		throw new IllegalStateException("Could not find setter for property: " + propertyName);
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

	private void parseProcessingInstruction(ProcessingInstruction instruction) {
		if (!IMPORT.equals(instruction.getTarget())) {
			return;
		}
		imports.add(instruction.getData());
	}
}
