package de.briemla.fxmltemplateloader;

public class FullQualifiedImport extends Import {

	private final String importQualifier;

	public FullQualifiedImport(String importQualifier) {
		super();
		this.importQualifier = importQualifier;
	}

	@Override
	boolean matches(String className) {
		return importQualifier.endsWith(className);
	}

	@Override
	protected Class<?> load(String className) throws ClassNotFoundException {
		return super.load(importQualifier);
	}

}
