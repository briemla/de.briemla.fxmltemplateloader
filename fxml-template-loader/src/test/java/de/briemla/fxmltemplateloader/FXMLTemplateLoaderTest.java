package de.briemla.fxmltemplateloader;

import java.io.IOException;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.junit.Test;

public class FXMLTemplateLoaderTest {

	/**
	 * Fails if cast to {@link VBox} does not match
	 *
	 * @throws Exception
	 */
	@Test
	public void loadSimpleVBoxRootWithSingleFullQualifiedImport() throws Exception {
		VBox vbox = load("SimpleVBoxRootWithSingleFullQualifiedImport.fxml");
	}

	/**
	 * Fails if cast to {@link HBox} does not match
	 *
	 * @throws Exception
	 */
	@Test
	public void loadSimpleHBoxRootWithSingleFullQualifiedImport() throws Exception {
		HBox hbox = load("SimpleHBoxRootWithSingleFullQualifiedImport.fxml");
	}

	private <T> T load(String fileName) throws IOException {
		return FXMLTemplateLoader.load(FXMLTemplateLoaderTest.class.getResource(fileName));
	}
}
