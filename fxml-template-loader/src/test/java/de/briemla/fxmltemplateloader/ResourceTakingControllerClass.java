package de.briemla.fxmltemplateloader;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class ResourceTakingControllerClass implements Initializable {

	private boolean initialized;

	public ResourceTakingControllerClass() {
		super();
		initialized = false;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialized = true;
	}

}
