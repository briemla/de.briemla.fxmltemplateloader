package de.briemla.fxmltemplateloader;

abstract class Import {

	private final ClassLoader classLoader;

	public Import(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
	}

	abstract boolean matches(String className);

	protected Class<?> load(String className) throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}

}
