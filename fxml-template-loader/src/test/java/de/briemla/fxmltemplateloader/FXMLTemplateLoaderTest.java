package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javafx.geometry.Pos;
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

	@Test
	public void loadVBoxRootWithPropertiesAndSingleFullQualifiedImport() throws Exception {
		VBox vbox = load("VBoxRootWithPropertiesAndSingleFullQualifiedImport.fxml");

		assertThat(vbox.getSpacing(), is(equalTo(200.0d)));
		assertThat(vbox.getId(), is(equalTo("thisIsAnId")));
		assertThat(vbox.isVisible(), is(false));
		assertThat(vbox.getAlignment(), is(equalTo(Pos.BOTTOM_RIGHT)));
	}

	@Test
	public void loadHBoxRootWithPropertiesAndSingleFullQualifiedImport() throws Exception {
		HBox hbox = load("HBoxRootWithPropertiesAndSingleFullQualifiedImport.fxml");

		assertThat(hbox.getSpacing(), is(equalTo(30.0d)));
		assertThat(hbox.getId(), is(equalTo("diNaSiSiht")));
		assertThat(hbox.isVisible(), is(false));
		assertThat(hbox.getAlignment(), is(equalTo(Pos.CENTER_RIGHT)));
	}

	private <T> T load(String fileName) throws IOException {
		return FXMLTemplateLoader.load(FXMLTemplateLoaderTest.class.getResource(fileName));
	}
}
