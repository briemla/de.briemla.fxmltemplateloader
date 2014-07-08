package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

import java.lang.reflect.Method;

import javafx.fxml.LoadException;
import javafx.util.Builder;

import com.sun.javafx.fxml.builder.ProxyBuilder;

public class Property {

	private final String name;
	private final String value;

	public Property(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public IProperty createTemplate(Builder<?> builder, ValueResolver valueResolver) throws NoSuchMethodException, SecurityException, LoadException {
		IProperty newPropertyTemplate = null;
		if (builder instanceof ProxyBuilder) {
			// FIXME builder method should only be searched once.
			// FIXME rename
			Method defaultJavaFxBuilderMethod = ProxyBuilder.class.getMethod("put", String.class, Object.class);
			newPropertyTemplate = new ProxyBuilderPropertyTemplate(defaultJavaFxBuilderMethod, name, value);
		}
		if (ReflectionUtils.hasBuilderMethod(builder.getClass(), name)) {
			Method method = ReflectionUtils.findBuilderMethod(builder.getClass(), name);
			Class<?> type = ReflectionUtils.extractType(method);
			Object convertedValue = valueResolver.resolve(value, to(type));

			newPropertyTemplate = new PropertyTemplate(method, convertedValue);
		}
		return newPropertyTemplate;
	}

}
