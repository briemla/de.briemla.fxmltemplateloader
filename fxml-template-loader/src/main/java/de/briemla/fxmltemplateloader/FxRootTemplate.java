package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javafx.fxml.LoadException;

public class FxRootTemplate extends InstantiationTemplate implements ExistingRoot {

	private final Class<?> rootType;
	private Object root;

	public FxRootTemplate(Class<?> rootType, List<IProperty> properties) {
		super(null, properties);
		this.rootType = rootType;
	}

	@Override
	protected Class<?> instanceType() {
		return rootType;
	}

	@Override
	protected Object newInstance(TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	InstantiationException, LoadException {
		if (root == null) {
			throw new LoadException("Root element must be set when using fx:root.");
		}
		return root;
	}

	@Override
	public void setRoot(Object fxRoot) {
		root = fxRoot;
	}

}
