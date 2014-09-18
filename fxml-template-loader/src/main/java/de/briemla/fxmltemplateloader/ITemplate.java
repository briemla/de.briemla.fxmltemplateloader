package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

import javafx.fxml.LoadException;

public interface ITemplate extends ExistingRoot {

	<T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException;

	<T> T create(Object controller) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException;

}