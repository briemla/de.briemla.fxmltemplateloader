package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ImportFactoryTest {

	@Test
	public void createWildcard() {
		Import expectedImport = new WildcardImport("wildcard.import.classifier.*");
		ImportFactory factory = new ImportFactory();

		Import actualImport = factory.create("wildcard.import.classifier.*");

		assertThat(actualImport, is(equalTo(expectedImport)));
	}
}
