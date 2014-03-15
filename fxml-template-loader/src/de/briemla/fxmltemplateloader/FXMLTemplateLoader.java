package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.from;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
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
		} catch (InstantiationException | IllegalAccessException exception) {
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
		rootNode = new Template(clazz);
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
