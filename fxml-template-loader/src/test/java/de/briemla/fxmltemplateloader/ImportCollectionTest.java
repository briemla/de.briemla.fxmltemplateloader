package de.briemla.fxmltemplateloader;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import javafx.fxml.LoadException;

import javax.xml.stream.events.ProcessingInstruction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ImportCollectionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void findClassWithoutAddedImports() throws LoadException {
		ImportFactory factory = mock(ImportFactory.class);
		ImportCollection importCollection = new ImportCollection(factory);
		verifyNoMoreInteractions(factory);

		thrown.expect(LoadException.class);
		thrown.expectMessage("No matching import available");

		importCollection.findClass("TestClass");
	}

	@Test
	public void findClassWithIncorrectProcessingInstruction() throws LoadException {
		ImportFactory factory = mock(ImportFactory.class);
		ImportCollection importCollection = new ImportCollection(factory);
		ProcessingInstruction importInstruction = mock(ProcessingInstruction.class);
		when(importInstruction.getTarget()).thenReturn("someOtherThanImport");
		importCollection.add(importInstruction);
		verify(importInstruction).getTarget();
		verifyNoMoreInteractions(factory);

		thrown.expect(LoadException.class);
		thrown.expectMessage("No matching import available");
		importCollection.findClass("TestClass");
	}

	@Test
	public void findClassWithSindleAddedImport() throws LoadException, ClassNotFoundException {
		ImportFactory factory = mock(ImportFactory.class);
		ImportCollection importCollection = new ImportCollection(factory);
		ProcessingInstruction importInstruction = mock(ProcessingInstruction.class);
		when(importInstruction.getTarget()).thenReturn("import");
		when(importInstruction.getData()).thenReturn("single.import");
		Import mockedImport = mock(Import.class);
		when(mockedImport.matches("single.import")).thenReturn(true);
		when(mockedImport.load("single.import")).thenReturn(any());
		when(factory.create("single.import")).thenReturn(mockedImport);

		importCollection.add(importInstruction);
		importCollection.findClass("single.import");

		verify(factory);
	}
}
