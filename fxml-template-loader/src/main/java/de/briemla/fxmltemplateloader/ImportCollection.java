package de.briemla.fxmltemplateloader;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.LoadException;

public class ImportCollection {

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

	public void add(String data) {
		if (data.endsWith(WILDCARD_MATCH)) {
			imports.add(new WildcardImport(data));
		}
		imports.add(new FullQualifiedImport(data));
	}

}
