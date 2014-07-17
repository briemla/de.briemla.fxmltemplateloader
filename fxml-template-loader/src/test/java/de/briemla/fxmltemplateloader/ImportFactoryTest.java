package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
		FullQualifiedImport expectedImport = new FullQualifiedImport("full.qualified.Import", CLASS_LOADER);
		ImportFactory factory = new ImportFactory(CLASS_LOADER);

		Import actualImport = factory.create("full.qualified.Import");

		assertThat(actualImport, is(equalTo(expectedImport)));
	}
}
