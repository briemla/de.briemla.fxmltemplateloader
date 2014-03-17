package de.briemla.fxmltemplateloader;

public class Template {

	private final Class<?> clazz;

	public Template(Class<?> clazz) {
		super();
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() throws InstantiationException, IllegalAccessException {
		return (T) clazz.newInstance();
	}

}
