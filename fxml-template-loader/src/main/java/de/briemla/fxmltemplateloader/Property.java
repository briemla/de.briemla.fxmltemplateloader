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

	// FIXME maybe introduce different Properties and throw LoadException in FXMLTemplateLoader with line information
	public IProperty createTemplate(Builder<?> builder, ValueResolver valueResolver) throws NoSuchMethodException, SecurityException, LoadException {
		if (builder instanceof ProxyBuilder) {
			// FIXME builder method should only be searched once.
			// FIXME rename
			Method defaultJavaFxBuilderMethod = ProxyBuilder.class.getMethod("put", String.class, Object.class);
			return new ProxyBuilderPropertyTemplate(defaultJavaFxBuilderMethod, name, value);
		}
		if (ReflectionUtils.hasBuilderMethod(builder.getClass(), name)) {
			Method method = ReflectionUtils.findBuilderMethod(builder.getClass(), name);
			Class<?> type = ReflectionUtils.extractType(method);
			IValue convertedValue = valueResolver.resolve(value, to(type));

			return new PropertyTemplate(method, convertedValue);
		}
		throw new LoadException("Could not create IProperty");
	}

}
