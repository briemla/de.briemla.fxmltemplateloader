package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

public interface IProperty {

	<T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	void apply(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;

}