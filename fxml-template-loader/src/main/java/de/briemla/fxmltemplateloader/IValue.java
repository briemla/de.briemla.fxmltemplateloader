package de.briemla.fxmltemplateloader;

import javafx.fxml.LoadException;

public interface IValue {

	Object create(TemplateRegistry registry) throws LoadException;

}