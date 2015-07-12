package de.briemla.fxmltemplateloader.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;

public class ImportFactoryTest {

    private static final ClassLoader CLASS_LOADER = ImportFactoryTest.class.getClassLoader();

    @Test
    public void createWildcard() {
        Import expectedImport = new WildcardImport("wildcard.import.classifier.*", CLASS_LOADER);
        ImportFactory factory = new ImportFactory(CLASS_LOADER);

        Import actualImport = factory.create("wildcard.import.classifier.*");

        assertThat(actualImport, is(equalTo(expectedImport)));
    }

    @Test
    public void createFullQualifiedImport() {
        FullQualifiedImport expectedImport = new FullQualifiedImport("full.qualified.Import",
                CLASS_LOADER);
        ImportFactory factory = new ImportFactory(CLASS_LOADER);

        Import actualImport = factory.create("full.qualified.Import");

        assertThat(actualImport, is(equalTo(expectedImport)));
    }

    @Test
    public void setClassLoader() throws Exception {
        ClassLoader classLoaderBeforeChange = mock(ClassLoader.class);
        FullQualifiedImport expectedImportBeforeChange = new FullQualifiedImport(
                "full.qualified.Import", classLoaderBeforeChange);

        ImportFactory importFactory = new ImportFactory(classLoaderBeforeChange);
        Import importBeforeChange = importFactory.create("full.qualified.Import");

        assertThat(importBeforeChange, is(equalTo(expectedImportBeforeChange)));

        ClassLoader classLoaderAfterChange = mock(ClassLoader.class);
        FullQualifiedImport expectedImportAfterChange = new FullQualifiedImport(
                "full.qualified.Import", classLoaderAfterChange);
        importFactory.setClassLoader(classLoaderAfterChange);

        Import importAfterChange = importFactory.create("full.qualified.Import");
        assertThat(importAfterChange, is(equalTo(expectedImportAfterChange)));

        verifyZeroInteractions(classLoaderBeforeChange);
        verifyZeroInteractions(classLoaderAfterChange);
    }
}
