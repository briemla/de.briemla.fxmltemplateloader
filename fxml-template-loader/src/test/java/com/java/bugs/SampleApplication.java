package com.java.bugs;

import de.briemla.fxmltemplateloader.FxmlTemplateLoader;
import de.briemla.fxmltemplateloader.template.ITemplate;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SampleApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ITemplate template = FxmlTemplateLoader.loadTemplate(getClass().getResource("Box.fxml"));

		Group box1 = (Group) template.create();
		setStuff(box1, "Third", 300, 50);

		Group box2 = (Group) template.create();
		setStuff(box2, "Second", 200, 50);

		Group box3 = (Group) template.create();
		setStuff(box3, "First", 100, 50);

		Group root = new Group();
		root.getChildren().addAll(box1, box2, box3);
		Scene scene = new Scene(root, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setStuff(Group box, String text, int x, int y) {
		Label label = (Label) box.getChildren().get(1);
		label.setText(text);
		box.setLayoutX(x);
		box.setLayoutY(y);
	}
}