package de.briemla.fxmltemplateloader.util;

/**
 * This class has no real impact on code behaviour. It can be used to improve
 * the readability of code.
 *
 * @author lars
 *
 */
public abstract class CodeSugar {

	public static <T> T from(T t) {
		return t;
	}

	public static <T> T and(T t) {
		return t;
	}

	public static <T> T to(T t) {
		return t;
	}
}
