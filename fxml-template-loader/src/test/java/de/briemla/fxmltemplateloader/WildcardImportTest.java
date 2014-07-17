package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class WildcardImportTest {

	@Test
	public void load() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		Class<?> clazz = Object.class;
		doReturn(clazz).when(classLoader).loadClass("importQualifier.className");
		WildcardImport fullQualifiedImport = new WildcardImport("importQualifier.*", classLoader);

		Class<?> load = fullQualifiedImport.load("className");
		assertThat(load, is(equalTo(clazz)));

		verify(classLoader).loadClass("importQualifier.className");
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		ClassLoader classLoader1 = mock(ClassLoader.class);
		ClassLoader classLoader2 = mock(ClassLoader.class);
		EqualsVerifier.forClass(WildcardImport.class).withPrefabValues(ClassLoader.class, classLoader1, classLoader2)
		        .allFieldsShouldBeUsedExcept("classLoader").usingGetClass().verify();
	}

}
