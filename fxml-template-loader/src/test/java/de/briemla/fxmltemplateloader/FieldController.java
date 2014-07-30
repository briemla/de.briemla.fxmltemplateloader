package de.briemla.fxmltemplateloader;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class FieldController {

	@FXML
	private VBox testId;
	@FXML
	private VBox childId;

	public VBox getTestId() {
		return testId;
	}

	public VBox getChildId() {
		return childId;
	}
}
