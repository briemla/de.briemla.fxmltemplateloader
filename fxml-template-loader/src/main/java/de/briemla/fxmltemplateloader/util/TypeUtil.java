package de.briemla.fxmltemplateloader.util;

import java.lang.reflect.Method;

import javax.xml.stream.events.Attribute;

public class TypeUtil {

	public static Object convertToCorrectType(Method method, Attribute attribute) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			throw new RuntimeException("Incorrect number of arguments for setter found.");
		}
		Class<?> attributeType = parameterTypes[0];

		return convert(attribute.getValue(), attributeType);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object convert(String value, Class<?> attributeType) {
		if (Boolean.class.equals(attributeType) || boolean.class.equals(attributeType)) {
			return Boolean.parseBoolean(value);
		}
		if (Byte.class.equals(attributeType) || byte.class.equals(attributeType)) {
			return Byte.parseByte(value);
		}
		if (Character.class.equals(attributeType) || char.class.equals(attributeType)) {
			if (value.length() != 1) {
				throw new IllegalArgumentException("Attribute must be a character, but contains more than one character.");
			}
			return value.charAt(0);
		}
		if (Double.class.equals(attributeType) || double.class.equals(attributeType)) {
			return Double.parseDouble(value);
		}
		if (attributeType.isEnum()) {
			Class<Enum> enumType = (Class<Enum>) attributeType;
			return Enum.valueOf(enumType, value);
		}
		if (Float.class.equals(attributeType) || float.class.equals(attributeType)) {
			return Float.parseFloat(value);
		}
		if (Integer.class.equals(attributeType) || int.class.equals(attributeType)) {
			return Integer.parseInt(value);
		}
		if (Short.class.equals(attributeType) || short.class.equals(attributeType)) {
			return Short.parseShort(value);
		}
		if (String.class.equals(attributeType)) {
			return value;
		}
		throw new RuntimeException("Attribute type not supported.");
	}
}
