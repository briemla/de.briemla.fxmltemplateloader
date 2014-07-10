package de.briemla.fxmltemplateloader;

class ImportFactory {

	private static final String WILDCARD_MATCH = ".*";

	public Import create(String importClassifier) {
		if (importClassifier.endsWith(WILDCARD_MATCH)) {
			return new WildcardImport(importClassifier);
		}
		return new FullQualifiedImport(importClassifier);
	}

}
