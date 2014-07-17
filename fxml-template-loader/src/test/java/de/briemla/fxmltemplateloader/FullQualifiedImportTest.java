package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class FullQualifiedImportTest {

	@Test
	public void matches() throws Exception {
		FullQualifiedImport fullQualifiedImport = new FullQualifiedImport("importQualifier");

		assertThat(fullQualifiedImport.matches("importQualifier"), is(equalTo(true)));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(FullQualifiedImport.class).allFieldsShouldBeUsed().usingGetClass().verify();
	}
}
