package de.briemla.fxmltemplateloader;

class ImportFactory {

	private static final String WILDCARD_MATCH = ".*";
	private final ClassLoader classLoader;

	public ImportFactory(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
	}

	public Import create(String importClassifier) {
		if (importClassifier.endsWith(WILDCARD_MATCH)) {
			return new WildcardImport(importClassifier, classLoader);
		}
		return new FullQualifiedImport(importClassifier, classLoader);
	}

}
