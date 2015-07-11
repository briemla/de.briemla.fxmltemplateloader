package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.LoadException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.briemla.fxmltemplateloader.processinginstruction.correct.ProcessingInstructionTestClass;
import de.briemla.fxmltemplateloader.template.ITemplate;
import de.briemla.fxutils.FXUtils;

public class FxmlTemplateLoaderTest {

    private static final String FXML_FILE_EXTENSION = ".fxml";
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Fails if cast to {@link VBox} does not match.
     *
     * @throws Exception
     *             throw {@link Exception}s thrown by loading an FXML file
     */
    @SuppressWarnings("unused")
    @Test
    public void loadSimpleVBoxRootWithSingleFullQualifiedImport() throws Exception {
        VBox vbox = load("SimpleVBoxRootWithSingleFullQualifiedImport");
    }

    /**
     * Fails if cast to {@link HBox} does not match.
     *
     * @throws Exception
     *             throw {@link Exception}s thrown by loading an FXML file
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
        Insets expectedPadding = new Insets(43.0, 38.0, 1536.0, 7635.0);
        Insets expectedOpaqueInsets = new Insets(756318.0, 913.0, 654.0, 684.0);
        Insets expectedNestedPadding = new Insets(12.0, 10.0, 31.0, 23.0);
        Insets expectedNestedOpaqueInsets = new Insets(54.0, 80.0, 46.0, 42.0);

        VBox root = load("VBoxWithNestedProperty");
        VBox nestedBox = (VBox) root.getChildren().get(0);

        Insets padding = root.getPadding();
        assertThat(padding, is(equalTo(expectedPadding)));

        Insets opaqueInsets = root.getOpaqueInsets();
        assertThat(opaqueInsets, is(equalTo(expectedOpaqueInsets)));

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
    public void loadElementWithFxmlMethodHandle() throws Exception {
        MethodHandleController controller = new MethodHandleController();
        VBox root = loadWithController("VBoxRootWithFxmlMethodHandle", controller);

        assertThat("Parent id", root.getId(), is(equalTo("parent")));
        EventHandler<? super MouseEvent> eventHandler = root.onMouseClickedProperty().get();
        MouseEvent mouseEvent = mock(MouseEvent.class);
        eventHandler.handle(mouseEvent);

        assertThat(controller.isHandlerInvoked(), is(true));
    }

    /**
     * When there are no exceptions, the image has been successfully loaded and the mechanism works.
     *
     * @throws Exception
     *             throws {@link InterruptedException} when FXApplication thread could not be
     *             started and an {@link IOException} when FXML file could not be loaded.
     */
    @Test
    public void loadElementWithLocation() throws Exception {
        FXUtils.startFxApplicationThread();
        load("VBoxWithLocation");
    }

    @Test
    public void loadReferencedElement() throws Exception {
        VBox root = load("VBoxWithReferencedElement");

        ObservableList<Node> childNodes = root.getChildren();
        VBox firstChild = (VBox) childNodes.get(0);
        VBox secondChild = (VBox) childNodes.get(1);
        Insets firstPadding = firstChild.getPadding();
        Insets secondPadding = secondChild.getPadding();

        assertThat(firstPadding, is(sameInstance(secondPadding)));
    }

    @Test
    public void loadReferencedElementInstantiateTemplateTwice() throws Exception {
        URL resource = FxmlTemplateLoaderTest.class.getResource("VBoxWithReferencedElement.fxml");
        ITemplate template = FxmlTemplateLoader.loadTemplate(resource);
        VBox firstInstance = template.create();
        VBox secondInstance = template.create();

        Insets firstPadding = ((VBox) firstInstance.getChildren().get(0)).getPadding();
        Insets secondPadding = ((VBox) secondInstance.getChildren().get(0)).getPadding();

        assertThat(secondPadding, is(not(sameInstance(firstPadding))));
        assertThat(secondPadding, is(equalTo(firstPadding)));
    }

    @Test
    public void loadOneElementInListProperty() throws Exception {
        VBox root = load("VBoxWithOneElementInListProperty");

        assertThat(root.getStyleClass(), contains("test-class"));
    }

    @Test
    public void loadFxmlRoot() throws Exception {
        VBoxAsFxRoot fxRoot = new VBoxAsFxRoot();
        VBoxAsFxRoot returnedRoot = loadWithRoot("VBoxAsFxRoot", fxRoot);

        assertThat(returnedRoot, is(sameInstance(fxRoot)));
        assertThat("Child count", fxRoot.getChildren().size(), is(equalTo(1)));
        Node childBox = fxRoot.getChildren().get(0);
        assertThat(childBox.getBlendMode(), is(BlendMode.ADD));
    }

    @Test
    public void loadFxmlWithStaticProperty() throws Exception {
        AnchorPane parent = load("VBoxWithStaticProperty");
        Node child = parent.getChildren().get(0);
        Double childTopAnchor = AnchorPane.getTopAnchor(child);
        assertThat(childTopAnchor, is(equalTo(10.0)));
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
        return FxmlTemplateLoader.load(FxmlTemplateLoaderTest.class.getResource(fxmlName));
    }

    private static <T> T loadWithResources(String fileName, Locale locale) throws IOException {
        String bundlePath = FxmlTemplateLoaderTest.class.getPackage().getName() + "." + fileName;
        ResourceBundle bundle = ResourceBundle.getBundle(bundlePath, locale);
        String fxmlName = fileName + FXML_FILE_EXTENSION;
        return FxmlTemplateLoader.load(FxmlTemplateLoaderTest.class.getResource(fxmlName), bundle);
    }

    private static <T> T loadWithController(String fileName, Object controller) throws IOException {
        String fxmlName = fileName + FXML_FILE_EXTENSION;
        FxmlTemplateLoader fxmlTemplateLoader = new FxmlTemplateLoader();
        fxmlTemplateLoader.setController(controller);
        return fxmlTemplateLoader.doLoad(FxmlTemplateLoaderTest.class.getResource(fxmlName));
    }

    private static <T> T loadWithRoot(String fileName, Object fxRoot)
            throws IOException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String fxmlName = fileName + FXML_FILE_EXTENSION;
        ITemplate template = FxmlTemplateLoader
                .loadTemplate(FxmlTemplateLoaderTest.class.getResource(fxmlName));
        template.setRoot(fxRoot);
        return template.create();
    }

}
