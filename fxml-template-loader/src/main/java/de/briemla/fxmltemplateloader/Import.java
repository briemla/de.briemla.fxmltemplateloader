package de.briemla.fxmltemplateloader;

public class Import {

	private static final String WILDCARD_MATCH = ".*";
	private final String importQualifier;

	public Import(String importQualifier) {
		super();
		this.importQualifier = importQualifier;
	}

	boolean matches(String className) {
		return importQualifier.endsWith(className);
	}

	Class<?> load() throws ClassNotFoundException {
		return loadInternal(importQualifier);
	}

	private Class<?> loadInternal(String importQualifier2) throws ClassNotFoundException {
		ClassLoader classLoader = FXMLTemplateLoader.class.getClassLoader();
		return classLoader.loadClass(importQualifier2);
	}

	boolean isWildcard() {
		return importQualifier.endsWith(WILDCARD_MATCH);
	}

	Class<?> load(String className) throws ClassNotFoundException {
		int indexBeforeWildcard = importQualifier.length() - 1;
		String removedWildcard = importQualifier.substring(0, indexBeforeWildcard);
		String fullQualifiedImport = removedWildcard + className;
		return loadInternal(fullQualifiedImport);
	}

}
