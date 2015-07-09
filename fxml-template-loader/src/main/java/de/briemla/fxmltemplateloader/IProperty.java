package de.briemla.fxmltemplateloader;

import java.lang.reflect.InvocationTargetException;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;
import javafx.fxml.LoadException;

public interface IProperty {

	<T> T create(TemplateRegistry registry) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	        LoadException;

	void apply(Object parent, TemplateRegistry registry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	        InstantiationException, LoadException;

}