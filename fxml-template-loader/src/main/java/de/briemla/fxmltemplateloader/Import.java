package de.briemla.fxmltemplateloader;

abstract class Import {

	public Import() {
		super();
	}

	abstract boolean matches(String className);

	protected Class<?> load(String className) throws ClassNotFoundException {
		ClassLoader classLoader = Import.class.getClassLoader();
		return classLoader.loadClass(className);
	}

	protected abstract boolean isWildcard();

}
