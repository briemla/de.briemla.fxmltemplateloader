package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

interface ITemplate {

	<T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}