package de.briemla.fxmltemplateloader;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.LoadException;

import javax.xml.stream.events.ProcessingInstruction;

public class ImportCollection {

	private static final String IMPORT = "import";

	private final List<Import> imports;
	private final ImportFactory factory;

	public ImportCollection(ImportFactory factory) {
		super();
		this.factory = factory;
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
		imports.add(factory.create(importClassifier));
	}
}
