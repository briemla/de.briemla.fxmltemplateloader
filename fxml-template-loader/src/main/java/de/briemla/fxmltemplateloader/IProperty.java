package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

public interface IProperty {

	<T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	void apply(Object parent, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			InstantiationException;

}