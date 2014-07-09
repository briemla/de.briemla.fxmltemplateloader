package de.briemla.fxmltemplateloader;

abstract class Import {

	public Import() {
		super();
	}

	abstract boolean matches(String className);

	protected Class<?> load(String importQualifier) throws ClassNotFoundException {
		ClassLoader classLoader = Import.class.getClassLoader();
		return classLoader.loadClass(importQualifier);
	}

	protected abstract boolean isWildcard();

}
