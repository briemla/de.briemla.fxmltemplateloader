package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.LoadException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.briemla.fxmltemplateloader.processinginstruction.correct.ProcessingInstructionTestClass;

public class FXMLTemplateLoaderTest {

	private static final String FXML_FILE_EXTENSION = ".fxml";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Fails if cast to {@link VBox} does not match
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Test
	public void loadSimpleVBoxRootWithSingleFullQualifiedImport() throws Exception {
		VBox vbox = load("SimpleVBoxRootWithSingleFullQualifiedImport");
	}

	/**
	 * Fails if cast to {@link HBox} does not match
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Test
	public void loadSimpleHBoxRootWithSingleFullQualifiedImport() throws Exception {
		HBox hbox = load("SimpleHBoxRootWithSingleFullQualifiedImport");
	}

	@Test
	public void loadVBoxRootWithPropertiesAndSingleFullQualifiedImport() throws Exception {
		VBox vbox = load("VBoxRootWithPropertiesAndSingleFullQualifiedImport");

		assertThat(vbox.getSpacing(), is(equalTo(200.0d)));
		assertThat(vbox.getId(), is(equalTo("thisIsAnId")));
		assertThat(vbox.isVisible(), is(false));
		assertThat(vbox.getAlignment(), is(equalTo(Pos.BOTTOM_RIGHT)));
	}

	@Test
	public void loadHBoxRootWithPropertiesAndSingleFullQualifiedImport() throws Exception {
		HBox hbox = load("HBoxRootWithPropertiesAndSingleFullQualifiedImport");

		assertThat(hbox.getSpacing(), is(equalTo(30.0d)));
		assertThat(hbox.getId(), is(equalTo("diNaSiSiht")));
		assertThat(hbox.isVisible(), is(false));
		assertThat(hbox.getAlignment(), is(equalTo(Pos.CENTER_RIGHT)));
	}

	@SuppressWarnings("unused")
	@Test
	public void loadHBoxRootWithSeveralFullQualifiedImports() throws IOException {
		HBox hbox = load("HBoxRootWithSeveralFullQualifiedImports");
	}

	@SuppressWarnings("unused")
	@Test
	public void loadVBoxRootWithSeveralFullQualifiedImports() throws IOException {
		VBox vbox = load("VBoxRootWithSeveralFullQualifiedImports");
	}

	@SuppressWarnings("unused")
	@Test
	public void loadHBoxRootWithWildcardImport() throws IOException {
		HBox hbox = load("HBoxRootWithWildcardImport");
	}

	@SuppressWarnings("unused")
	@Test
	public void loadVBoxRootWithWildcardImport() throws IOException {
		VBox vbox = load("VBoxRootWithWildcardImport");
	}

	@SuppressWarnings("unused")
	@Test
	public void onlyLoadImportProcessingInstructions() throws IOException {
		ProcessingInstructionTestClass testClass = load("ProcessingInstructionTest");
	}

	@Test
	public void loadNestedElement() throws Exception {
		VBox root = load("VBoxRootWithNestedVBox");

		assertThat("Number of children", root.getChildren().size(), is(equalTo(1)));
		assertThat("Parent id", root.getId(), is(equalTo("parent")));

		Node child = root.getChildren().get(0);
		assertThat("Child id", child.getId(), is(equalTo("child")));
	}

	@Test
	public void loadNestedProperties() throws IOException {
		Insets expectedNestedPadding = new Insets(12.0, 10.0, 31.0, 23.0);
		Insets expectedNestedOpaqueInsets = new Insets(54.0, 80.0, 46.0, 42.0);
		Insets expectedPadding = new Insets(43.0, 38.0, 1536.0, 7635.0);
		Insets expectedOpaqueInsets = new Insets(756318.0, 913.0, 654.0, 684.0);

		VBox root = load("VBoxWithNestedProperty");

		Insets padding = root.getPadding();
		assertThat(padding, is(equalTo(expectedPadding)));

		Insets opaqueInsets = root.getOpaqueInsets();
		assertThat(opaqueInsets, is(equalTo(expectedOpaqueInsets)));

		VBox nestedBox = (VBox) root.getChildren().get(0);
		Insets nestedPadding = nestedBox.getPadding();
		assertThat(nestedPadding, is(equalTo(expectedNestedPadding)));

		Insets nestedOpaqueInsets = nestedBox.getOpaqueInsets();
		assertThat(nestedOpaqueInsets, is(equalTo(expectedNestedOpaqueInsets)));
	}

	@Test
	public void loadLocalizedGermanResources() throws Exception {
		VBox germanBox = loadWithResources("VBoxWithLocalizedText", Locale.GERMAN);
		Text germanText = (Text) germanBox.getChildren().get(0);

		assertThat(germanText.getText(), is(equalTo("German Hallo")));
	}

	@Test
	public void loadLocalizedEnglishResources() throws Exception {
		VBox germanBox = loadWithResources("VBoxWithLocalizedText", Locale.ENGLISH);
		Text germanText = (Text) germanBox.getChildren().get(0);

		assertThat(germanText.getText(), is(equalTo("English Hello")));
	}

	@Test
	public void loadLocalizedResourcesWithMissingBundle() throws Exception {
		thrown.expect(LoadException.class);
		thrown.expectMessage("No resources specified.");
		load("VBoxWithLocalizedText");
	}

	@Test
	public void loadLocalizedResourcesWithMissingResourceKey() throws Exception {
		thrown.expect(LoadException.class);
		thrown.expectMessage("Resource \"missingKey\" not found.");
		loadWithResources("VBoxWithMissingLocalizedText", Locale.GERMAN);
	}

	@Test
	public void loadMissingImport() throws IOException {
		thrown.expect(LoadException.class);
		load("VBoxRootWithMissingImport");
	}

	@Test
	public void loadNestedElementWithFxId() throws Exception {
		FieldController controller = new FieldController();
		VBox root = loadWithController("VBoxRootWithNestedVBoxWithFxId", controller);

		assertThat("Number of children", root.getChildren().size(), is(equalTo(1)));
		assertThat("Parent id", root.getId(), is(equalTo("parent")));

		Node child = root.getChildren().get(0);
		assertThat("Child id", child.getId(), is(equalTo("child")));

		assertThat(controller.getTestId(), is(sameInstance(root)));
		assertThat(controller.getChildId(), is(sameInstance(child)));
	}

	@Test
	public void fullDummyClass() throws Exception {
		FullDummyClass dummyClass = load("FullDummyClass");

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
		String fxmlName = fileName + FXML_FILE_EXTENSION;
		return FXMLTemplateLoader.load(FXMLTemplateLoaderTest.class.getResource(fxmlName));
	}

	private static <T> T loadWithResources(String fileName, Locale locale) throws IOException {
		String bundlePath = FXMLTemplateLoaderTest.class.getPackage().getName() + "." + fileName;
		ResourceBundle bundle = ResourceBundle.getBundle(bundlePath, locale);
		String fxmlName = fileName + FXML_FILE_EXTENSION;
		return FXMLTemplateLoader.load(FXMLTemplateLoaderTest.class.getResource(fxmlName), bundle);
	}

	private static <T> T loadWithController(String fileName, Object controller) throws IOException {
		String fxmlName = fileName + FXML_FILE_EXTENSION;
		FXMLTemplateLoader fxmlTemplateLoader = new FXMLTemplateLoader();
		fxmlTemplateLoader.setController(controller);
		return fxmlTemplateLoader.doLoad(FXMLTemplateLoaderTest.class.getResource(fxmlName));
	}
}
