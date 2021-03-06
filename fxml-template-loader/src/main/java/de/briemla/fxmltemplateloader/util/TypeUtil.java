package de.briemla.fxmltemplateloader.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class TypeUtil {

    /**
     * Convert value to type of given {@link Class}.
     *
     * @param value
     *            to convert
     * @param attributeType
     *            to convert value into
     * @return converted value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object convert(String value, Class<?> attributeType) {
        if (value == null) {
            return null;
        }
        if (BigInteger.class.equals(attributeType)) {
            return new BigInteger(value);
        }
        if (BigDecimal.class.equals(attributeType)) {
            return new BigDecimal(value);
        }
        if (Boolean.class.equals(attributeType) || boolean.class.equals(attributeType)) {
            return Boolean.parseBoolean(value);
        }
        if (Byte.class.equals(attributeType) || byte.class.equals(attributeType)) {
            return Byte.parseByte(value);
        }
        if (Character.class.equals(attributeType) || char.class.equals(attributeType)) {
            if (value.length() != 1) {
                throw new IllegalArgumentException(
                        "Attribute must be a character, but contains more than one character.");
            }
            return value.charAt(0);
        }
        if (Paint.class.equals(attributeType)) {
        	return Color.valueOf(value);
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
        if (Number.class.equals(attributeType)) {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
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
