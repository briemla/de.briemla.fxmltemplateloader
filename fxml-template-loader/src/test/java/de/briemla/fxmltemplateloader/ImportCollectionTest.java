package de.briemla.fxmltemplateloader;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
		verifyZeroInteractions(factory);

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
		verifyZeroInteractions(factory);

		thrown.expect(LoadException.class);
		thrown.expectMessage("No matching import available");
		importCollection.findClass("TestClass");
	}

	@Test
	public void findClassWithSindleAddedImport() throws LoadException, ClassNotFoundException {
		ImportFactory factory = mock(ImportFactory.class);
		ImportCollection importCollection = new ImportCollection(factory);
		ProcessingInstruction importInstruction = mock(ProcessingInstruction.class);
		Import mockedImport = mock(Import.class);

		when(importInstruction.getTarget()).thenReturn("import");
		when(importInstruction.getData()).thenReturn("single.import");
		when(mockedImport.matches("single.import")).thenReturn(true);
		Class<?> toBeReturned = Object.class;
		doReturn(toBeReturned).when(mockedImport).load("single.import");
		when(factory.create("single.import")).thenReturn(mockedImport);

		importCollection.add(importInstruction);
		importCollection.findClass("single.import");

		verify(factory).create("single.import");
		verify(mockedImport).matches("single.import");
		verify(mockedImport).load("single.import");
		verify(importInstruction).getTarget();
		verify(importInstruction).getData();
	}

	@Test
	public void findClassAfterClear() throws Exception {
		ImportFactory factory = mock(ImportFactory.class);
		ImportCollection importCollection = new ImportCollection(factory);
		ProcessingInstruction importInstruction = mock(ProcessingInstruction.class);
		Import mockedImport = mock(Import.class);

		when(importInstruction.getTarget()).thenReturn("import");
		when(importInstruction.getData()).thenReturn("single.import");
		Class<?> toBeReturned = Object.class;
		doReturn(toBeReturned).when(mockedImport).load("single.import");
		when(factory.create("single.import")).thenReturn(mockedImport);

		importCollection.add(importInstruction);

		verify(factory).create("single.import");
		verify(importInstruction).getTarget();
		verify(importInstruction).getData();
		verifyZeroInteractions(mockedImport);
		importCollection.clear();

		thrown.expect(LoadException.class);
		thrown.expectMessage("No matching import available");

		importCollection.findClass("TestClass");
	}
}
