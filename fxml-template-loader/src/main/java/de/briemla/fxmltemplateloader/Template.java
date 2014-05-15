package de.briemla.fxmltemplateloader;

public class Template {

	private final ITemplate parent;

	public Template(ITemplate parent) {
		super();
		this.parent = parent;
	}

	ITemplate getParent() {
		return parent;
	}

}