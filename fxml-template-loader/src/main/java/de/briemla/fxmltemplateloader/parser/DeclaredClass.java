package de.briemla.fxmltemplateloader.parser;

public class DeclaredClass {

	private String className;
	private ClassLoader classLoader;

	public DeclaredClass(String className, ClassLoader classLoader) {
		super();
		this.className = className;
		this.classLoader = classLoader;
	}

	public Class<?> load() throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}
	
	

}
