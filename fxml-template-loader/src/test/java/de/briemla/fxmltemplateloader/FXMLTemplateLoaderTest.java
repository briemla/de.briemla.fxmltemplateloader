package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.junit.Test;

import de.briemla.fxmltemplateloader.processinginstruction.correct.ProcessingInstructionTestClass;

public class FXMLTemplateLoaderTest {

	/**
	 * Fails if cast to {@link VBox} does not match
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Test
	public void loadSimpleVBoxRootWithSingleFullQualifiedImport() throws Exception {
		VBox vbox = load("SimpleVBoxRootWithSingleFullQualifiedImport.fxml");
	}

	/**
	 * Fails if cast to {@link HBox} does not match
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	@Test
	public void loadHBoxRootWithSeveralFullQualifiedImports() throws IOException {
		HBox hbox = load("HBoxRootWithSeveralFullQualifiedImports.fxml");
	}

	@SuppressWarnings("unused")
	@Test
	public void loadVBoxRootWithSeveralFullQualifiedImports() throws IOException {
		VBox vbox = load("VBoxRootWithSeveralFullQualifiedImports.fxml");
	}

	@SuppressWarnings("unused")
	@Test
	public void loadHBoxRootWithWildcardImport() throws IOException {
		HBox hbox = load("HBoxRootWithWildcardImport.fxml");
	}

	@SuppressWarnings("unused")
	@Test
	public void loadVBoxRootWithWildcardImport() throws IOException {
		VBox vbox = load("VBoxRootWithWildcardImport.fxml");
	}

	@SuppressWarnings("unused")
	@Test
	public void onlyLoadImportProcessingInstructions() throws IOException {
		ProcessingInstructionTestClass testClass = load("ProcessingInstructionTest.fxml");
	}

	@Test
	public void loadNestedElement() throws Exception {
		VBox root = load("VBoxRootWithNestedVBox.fxml");

		assertThat("Number of children", root.getChildren().size(), is(equalTo(1)));
		assertThat("Parent id", root.getId(), is(equalTo("parent")));

		Node child = root.getChildren().get(0);
		assertThat("Child id", child.getId(), is(equalTo("child")));
	}

	@Test
	public void loadNestedProperties() throws IOException {
		VBox root = load("VBoxWithNestedProperty.fxml");

		VBox nestedBox = (VBox) root.getChildren().get(0);
		Insets padding = nestedBox.getPadding();

		Insets expectedPadding = new Insets(12.0, 10.0, 31.0, 23.0);
		assertThat(padding, is(equalTo(expectedPadding)));
	}

	@Test
	public void fullDummyClass() throws Exception {
		FullDummyClass dummyClass = load("FullDummyClass.fxml");

		assertThat(dummyClass.isBooleanMember(), is(true));
		assertThat(dummyClass.getByteMember(), is(equalTo((byte) 2)));
		assertThat(dummyClass.getCharMember(), is(equalTo('c')));
		assertThat(dummyClass.getDoubleMember(), is(equalTo(12.345d)));
		assertThat(dummyClass.getEnumMember(), is(equalTo(DummyEnum.DUMMY_1)));
		assertThat(dummyClass.getFloatMember(), is(equalTo(54.321f)));
		assertThat(dummyClass.getIntMember(), is(equalTo(1234567)));
		assertThat(dummyClass.getShortMember(), is(equalTo((short) 123)));
		assertThat(dummyClass.getStringMember(), is(equalTo("sadjlsad")));
	}

	private static <T> T load(String fileName) throws IOException {
		return FXMLTemplateLoader.load(FXMLTemplateLoaderTest.class.getResource(fileName));
	}
}
