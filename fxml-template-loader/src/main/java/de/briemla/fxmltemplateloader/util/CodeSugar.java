package de.briemla.fxmltemplateloader.util;

/**
 * This class has no real impact on code behaviour. It can be used to improve the readability of
 * code.
 *
 * @author lars
 *
 */
public abstract class CodeSugar {

    public static <T> T from(T object) {
        return object;
    }

    public static <T> T and(T object) {
        return object;
    }

    public static <T> T to(T object) {
        return object;
    }
}
