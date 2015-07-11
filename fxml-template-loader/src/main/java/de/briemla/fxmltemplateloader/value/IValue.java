package de.briemla.fxmltemplateloader.value;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;

public interface IValue {

    Object create(TemplateRegistry registry) throws LoadException;

}