package de.briemla.fxmltemplateloader.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.xml.stream.events.ProcessingInstruction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.fxml.LoadException;

public class ImportCollectionTest {

    private static final String className = "single.import";
	private static final Class<?> loadedClass = Object.class;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	private ImportFactory factory;
	private ImportCollection importCollection;
	private ProcessingInstruction importInstruction;
    
    @Before
    public void initialize() {
    	factory = mock(ImportFactory.class);
    	importCollection = new ImportCollection(factory);
    	importInstruction = mock(ProcessingInstruction.class);
    }

    @Test
    public void findClassWithoutAddedImports() throws LoadException {
        verifyZeroInteractions(factory);

        thrown.expect(LoadException.class);
        thrown.expectMessage("No matching import available");

        importCollection.findClass("TestClass");
    }

    @Test
    public void findClassWithIncorrectProcessingInstruction() throws LoadException {
        configureIncorrectImportInstruction();
        verify(importInstruction).getTarget();
        verifyZeroInteractions(factory);

        thrown.expect(LoadException.class);
        thrown.expectMessage("No matching import available");
        importCollection.findClass("TestClass");
    }

	private void configureIncorrectImportInstruction() {
		configure("someOtherThanImport", "withIncorrectData");
	}

	private void configure(String instruction, String data) {
		when(importInstruction.getTarget()).thenReturn(instruction);
		when(importInstruction.getData()).thenReturn(data);
        importCollection.add(importInstruction);
	}

    @Test
    public void findClassWithSingleAddedImport() throws LoadException, ClassNotFoundException {
        Import mockedImport = createValidClass();
        configureValidImport();

        Class<?> foundClass = importCollection.findClass(className);
        
        assertThat(foundClass, is(equalTo(loadedClass)));

        verify(factory).create(className);
        verify(mockedImport).matches(className);
        verify(mockedImport).load(className);
        verify(importInstruction).getTarget();
        verify(importInstruction).getData();
    }

	private Import createValidClass() throws ClassNotFoundException {
		Import mockedImport = mock(Import.class);
        when(mockedImport.matches(className)).thenReturn(true);
        doReturn(loadedClass).when(mockedImport).load(className);
        when(factory.create(className)).thenReturn(mockedImport);
		return mockedImport;
	}

	private void configureValidImport() {
		configure("import", className);
	}

    @Test
    public void findClassAfterClear() throws Exception {
        Import mockedImport = createValidClass();
        configureValidImport();

        verifyZeroInteractions(mockedImport);
        importCollection.clear();

        thrown.expect(LoadException.class);
        thrown.expectMessage("No matching import available");

        importCollection.findClass("TestClass");
    }
    
    @Test
	public void loadsClassWithMissingImport() throws Exception {
    	String fullQualifiedClass = "full.qualified.Import";
        DeclaredClass declaredClass = mock(DeclaredClass.class);

        when(factory.canLoad(fullQualifiedClass)).thenReturn(true);
        doReturn(loadedClass).when(declaredClass).load();
		when(factory.createClass(fullQualifiedClass)).thenReturn(declaredClass);

		importCollection.findClass(fullQualifiedClass);
        
        verify(factory).canLoad(fullQualifiedClass);
	}    
    
}
