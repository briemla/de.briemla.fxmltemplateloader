package de.briemla.fxmltemplateloader.template;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;

import java.lang.reflect.Method;
import java.util.AbstractMap;

import javafx.fxml.LoadException;
import javafx.util.Builder;

import de.briemla.fxmltemplateloader.parser.ValueResolver;
import de.briemla.fxmltemplateloader.util.ReflectionUtils;
import de.briemla.fxmltemplateloader.value.IValue;

public class Property {

    private final String name;
    private final String value;

    /**
     * Property of FXML elements.
     */
    public Property(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    // FIXME maybe introduce different Properties and throw LoadException in FXMLTemplateLoader with
    // line information
    /**
     * Convert the value to a matching type and return an {@link IProperty} object containing the
     * information.
     *
     * @param builder
     *            to build more complex objects without a matching constructor
     * @param valueResolver
     *            to convert the {@link String} value into the correct type
     * @return converted value as {@link IProperty}
     * @throws LoadException
     *             if value can not be converted correctly
     */
    public IProperty createTemplate(Builder<?> builder, ValueResolver valueResolver)
            throws LoadException {
        if (builder == null) {
            throw new LoadException("Builder is not allowed to be null");
        }
        if (builder instanceof AbstractMap) {
            // FIXME builder method should only be searched once.
            // FIXME rename
            Method defaultJavaFxBuilderMethod;
            try {
                defaultJavaFxBuilderMethod = AbstractMap.class.getMethod("put", Object.class,
                        Object.class);
            } catch (NoSuchMethodException exception) {
                throw new LoadException(exception);
            }
            IValue convertedValue = valueResolver.resolve(value, to(String.class));
            return new ProxyBuilderPropertyTemplate(defaultJavaFxBuilderMethod, name,
                    convertedValue);
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
