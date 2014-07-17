package de.briemla.fxmltemplateloader;

class ImportFactory {

	private static final String WILDCARD_MATCH = ".*";
	private static final ClassLoader CLASS_LOADER = ImportFactory.class.getClassLoader();

	public Import create(String importClassifier) {
		if (importClassifier.endsWith(WILDCARD_MATCH)) {
			return new WildcardImport(importClassifier, CLASS_LOADER);
		}
		return new FullQualifiedImport(importClassifier, CLASS_LOADER);
	}

}
