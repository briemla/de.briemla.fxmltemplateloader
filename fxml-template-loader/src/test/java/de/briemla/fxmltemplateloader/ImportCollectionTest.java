package de.briemla.fxmltemplateloader;

import javafx.fxml.LoadException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ImportCollectionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void findClassWithoutAddedImports() throws LoadException {
		ImportCollection importCollection = new ImportCollection();

		thrown.expect(LoadException.class);
		thrown.expectMessage("No matching import available");

		importCollection.findClass("TestClass");
	}
}
