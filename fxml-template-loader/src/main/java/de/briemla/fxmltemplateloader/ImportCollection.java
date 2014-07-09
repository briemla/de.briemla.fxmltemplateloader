package de.briemla.fxmltemplateloader;

import java.util.ArrayList;
import java.util.List;

public class ImportCollection {

	private static final String WILDCARD_MATCH = ".*";

	private final List<Import> imports;

	public ImportCollection() {
		super();
		imports = new ArrayList<>();
	}

	public Class<?> findClass(String className) {
		for (Import importQualifier : imports) {
			if (importQualifier.matches(className)) {
				try {
					return importQualifier.load(className);
				} catch (ClassNotFoundException e) {
					break;
				}
			}
			if (importQualifier.isWildcard()) {
				try {
					return importQualifier.load(className);
				} catch (ClassNotFoundException e) {
					// continue loading, maybe there are other matching imports
					continue;
				}
			}
		}
		throw new RuntimeException("Could not find class for name: " + className);
	}

	public void add(String data) {
		if (data.endsWith(WILDCARD_MATCH)) {
			imports.add(new WildcardImport(data));
		}
		imports.add(new FullQualifiedImport(data));
	}

}
