package de.briemla.fxmltemplateloader.parser;

import java.net.URL;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.FxmlTemplateLoader;
import javafx.util.BuilderFactory;

public class Parser {

	private final ImportCollection imports;
	private final ImportFactory factory;
	private final BuilderFactory builderFactory;
	private final ValueResolver valueResolver;

	public Parser(ImportFactory factory, ImportCollection imports, BuilderFactory builderFactory,
			ValueResolver valueResolver) {
		super();
		this.factory = factory;
		this.imports = imports;
		this.builderFactory = builderFactory;
		this.valueResolver = valueResolver;
	}

	public ImportCollection imports() {
		return imports;
	}

	public BuilderFactory builderFactory() {
		return builderFactory;
	}

	public ValueResolver valueResolver() {
		return valueResolver;
	}

	public void setClassLoader(ClassLoader classLoader) {
		factory.setClassLoader(classLoader);
		imports.clear();
		valueResolver.setClassLoader(classLoader);
	}

	public void correctClassLoader() {
		if (factory.hasClassLoader() && valueResolver.hasClassLoader()) {
			return;
		}
		setClassLoader(FxmlTemplateLoader.class.getClassLoader());
	}

	public void setResourceBundle(ResourceBundle bundle) {
		valueResolver.setResourceBundle(bundle);
	}

	public void setLocation(URL location) {
		valueResolver.setLocation(location);
	}

}
