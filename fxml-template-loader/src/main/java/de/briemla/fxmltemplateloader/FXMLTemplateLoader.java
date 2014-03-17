package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.from;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class FXMLTemplateLoader {

	private static final String WILDCARD_MATCH = "*";
	private static final String IMPORT = "import";
	private final HashSet<String> imports;
	private static Template rootNode;

	public FXMLTemplateLoader() {
		super();
		imports = new HashSet<>();
	}

	public static <T> T load(URL resource) throws IOException {
		return new FXMLTemplateLoader().doLoad(resource);
	}

	private <T> T doLoad(URL resource) throws IOException {
		XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
		try (InputStream xmlInput = resource.openStream()) {
			XMLEventReader eventReader = xmlFactory.createXMLEventReader(from(xmlInput));
			return parseXml(eventReader).create();
		} catch (XMLStreamException exception) {
			throw new IOException("Could not parse XML", exception);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new IOException("Could not instatiate Nodes", exception);
		}
	}

	private Template parseXml(XMLEventReader eventReader) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isProcessingInstruction()) {
				parseProcessingInstruction((ProcessingInstruction) event);
			}
			if (event.isStartElement()) {
				parseElement(event.asStartElement(), eventReader);
			}
		}
		return rootNode;
	}

	private void parseElement(StartElement element, XMLEventReader reader) {
		String className = element.getName().getLocalPart();
		Class<?> clazz = findClass(className);
		Map<Method, Object> properties = findProperties(element, clazz);
		rootNode = new Template(clazz, properties);
	}

	@SuppressWarnings({ "restriction", "unchecked" })
	private Map<Method, Object> findProperties(StartElement element, Class<?> clazz) {
		HashMap<Method, Object> properties = new HashMap<>();
		Iterator<Attribute> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			Method method = findSetter(clazz, attribute);
			Object value = convertToCorrectType(method, attribute);
			properties.put(method, value);
		}
		return properties;
	}

	private Object convertToCorrectType(Method method, Attribute attribute) {
		return Double.parseDouble(attribute.getValue().toString());
	}

	private Method findSetter(Class<?> clazz, Attribute attribute) {
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
			return load(importQualifier);
		}
		return null;
	}

	private Class<?> load(String importQualifier) {
		ClassLoader classLoader = FXMLTemplateLoaderTest.class.getClassLoader();
		try {
			return classLoader.loadClass(importQualifier);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load import");
		}
	}

	private void parseProcessingInstruction(ProcessingInstruction instruction) {
		// if (!IMPORT.equals(event.getTarget())) {
		// return;
		// }
		imports.add(instruction.getData());
	}
}
