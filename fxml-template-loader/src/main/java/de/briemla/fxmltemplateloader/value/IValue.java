package de.briemla.fxmltemplateloader.value;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;
import javafx.fxml.LoadException;

public interface IValue {

	Object create(TemplateRegistry registry) throws LoadException;

}