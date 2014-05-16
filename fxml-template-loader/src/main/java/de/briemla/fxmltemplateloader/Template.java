package de.briemla.fxmltemplateloader;

public abstract class Template {

	private final Template parent;

	public Template(Template parent) {
		super();
		this.parent = parent;
	}

	Template getParent() {
		return parent;
	}

	protected abstract Class<?> getInstanceClass();

	protected abstract void addProperty(IProperty property);

}