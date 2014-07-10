package de.briemla.fxmltemplateloader;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.LoadException;

import javax.xml.stream.events.ProcessingInstruction;

public class ImportCollection {

	private static final String IMPORT = "import";
	private static final String WILDCARD_MATCH = ".*";

	private final List<Import> imports;

	public ImportCollection() {
		super();
		imports = new ArrayList<>();
	}

	public Class<?> findClass(String className) throws LoadException {
		try {
			for (Import importQualifier : imports) {
				if (importQualifier.matches(className)) {
					return importQualifier.load(className);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new LoadException(e);
		}
		throw new LoadException("No matching import available for class with name: " + className);
	}

	public void add(ProcessingInstruction instruction) {
		if (!IMPORT.equals(instruction.getTarget())) {
			return;
		}
		String importClassifier = instruction.getData();
		imports.add(create(importClassifier));
	}

	private Import create(String importClassifier) {
		if (importClassifier.endsWith(WILDCARD_MATCH)) {
			return new WildcardImport(importClassifier);
		}
		return new FullQualifiedImport(importClassifier);
	}
}
