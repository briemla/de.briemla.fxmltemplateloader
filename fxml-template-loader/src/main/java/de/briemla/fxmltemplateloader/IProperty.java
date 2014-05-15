package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

public interface IProperty extends ITemplate {

	void apply(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;

}