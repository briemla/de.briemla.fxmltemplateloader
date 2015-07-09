package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

import java.lang.reflect.Method;
import java.util.AbstractMap;

import de.briemla.fxmltemplateloader.util.ReflectionUtils;
import javafx.fxml.LoadException;
import javafx.util.Builder;

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
		if (builder == null) {
			throw new LoadException("Builder is not allowed to be null");
		}
		if (builder instanceof AbstractMap) {
			// FIXME builder method should only be searched once.
			// FIXME rename
			Method defaultJavaFxBuilderMethod = AbstractMap.class.getMethod("put", Object.class, Object.class);
			IValue convertedValue = valueResolver.resolve(value, to(String.class));
			return new ProxyBuilderPropertyTemplate(defaultJavaFxBuilderMethod, name, convertedValue);
		}
		if (ReflectionUtils.hasBuilderMethod(builder.getClass(), name)) {
			Method method = ReflectionUtils.findBuilderMethod(builder.getClass(), name);
			Class<?> type = ReflectionUtils.extractType(method);
			IValue convertedValue = valueResolver.resolve(value, to(type));

			return new PropertyTemplate(method, convertedValue);
		}
		throw new LoadException("Could not create IProperty");
	}

	@Override
	public String toString() {
		return "Property [name=" + name + ", value=" + value + "]";
	}

}
