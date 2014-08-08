package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

import javafx.fxml.LoadException;

interface ITemplate {

	<T> T create() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException;

	<T> T create(Object controller) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, LoadException;

}