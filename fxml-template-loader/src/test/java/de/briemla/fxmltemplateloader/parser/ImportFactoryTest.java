package de.briemla.fxmltemplateloader.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;

public class ImportFactoryTest {

	private static final ClassLoader CLASS_LOADER = ImportFactoryTest.class.getClassLoader();
	private Import wildcardImport;
	private ImportFactory factory;
	private FullQualifiedImport fullQualifiedImport;

	@Before
	public void initialize() {
		wildcardImport = new WildcardImport("wildcard.import.classifier.*", CLASS_LOADER);
		fullQualifiedImport = new FullQualifiedImport("full.qualified.Import", CLASS_LOADER);
		factory = new ImportFactory(CLASS_LOADER);
	}

	@Test
	public void createWildcard() {
		Import actualImport = factory.create("wildcard.import.classifier.*");

		assertThat(actualImport, is(equalTo(wildcardImport)));
	}

	@Test
	public void createWildcardWithTrailingWhitespace() {
		Import actualImport = factory.create("wildcard.import.classifier.* ");

		assertThat(actualImport, is(equalTo(wildcardImport)));
	}

	@Test
	public void createFullQualifiedImport() {
		Import actualImport = factory.create("full.qualified.Import");

		assertThat(actualImport, is(equalTo(fullQualifiedImport)));
	}

	@Test
	public void createFullQualifiedImportWithTrailingWhitespace() {
		Import actualImport = factory.create("full.qualified.Import ");

		assertThat(actualImport, is(equalTo(fullQualifiedImport)));
	}

	@Test
	public void setClassLoader() throws Exception {
		ClassLoader classLoaderBeforeChange = mock(ClassLoader.class);
		FullQualifiedImport expectedImportBeforeChange = new FullQualifiedImport("full.qualified.Import",
				classLoaderBeforeChange);

		ImportFactory importFactory = new ImportFactory(classLoaderBeforeChange);
		Import importBeforeChange = importFactory.create("full.qualified.Import");

		assertThat(importBeforeChange, is(equalTo(expectedImportBeforeChange)));

		ClassLoader classLoaderAfterChange = mock(ClassLoader.class);
		FullQualifiedImport expectedImportAfterChange = new FullQualifiedImport("full.qualified.Import",
				classLoaderAfterChange);
		importFactory.setClassLoader(classLoaderAfterChange);

		Import importAfterChange = importFactory.create("full.qualified.Import");
		assertThat(importAfterChange, is(equalTo(expectedImportAfterChange)));

		verifyZeroInteractions(classLoaderBeforeChange);
		verifyZeroInteractions(classLoaderAfterChange);
	}
}
