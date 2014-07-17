package de.briemla.fxmltemplateloader;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class FullQualifiedImportTest {

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(FullQualifiedImport.class).allFieldsShouldBeUsed().usingGetClass().verify();
	}
}
