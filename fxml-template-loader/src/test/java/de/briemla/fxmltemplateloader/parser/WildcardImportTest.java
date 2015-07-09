package de.briemla.fxmltemplateloader.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

import de.briemla.fxmltemplateloader.parser.WildcardImport;

public class WildcardImportTest {

	@Test
	public void matches() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		Class<?> expectedClass = Object.class;
		doReturn(expectedClass).when(classLoader).loadClass("importQualifier.className");
		WildcardImport wildcardImport = new WildcardImport("importQualifier.*", classLoader);

		assertThat(wildcardImport.matches("className"), is(equalTo(true)));
		verify(classLoader).loadClass("importQualifier.className");
	}

	@Test
	public void matchesNull() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		WildcardImport wildcardImport = new WildcardImport("importQualifier.*", classLoader);

		assertThat(wildcardImport.matches(null), is(equalTo(false)));
		verifyZeroInteractions(classLoader);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void matchesNot() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		when(classLoader.loadClass("importQualifier.notMatchingClassName")).thenThrow(ClassNotFoundException.class);
		WildcardImport wildcardImport = new WildcardImport("importQualifier.*", classLoader);

		assertThat(wildcardImport.matches("notMatchingClassName"), is(equalTo(false)));
		verify(classLoader).loadClass("importQualifier.notMatchingClassName");
	}

	@Test
	public void load() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		Class<?> clazz = Object.class;
		doReturn(clazz).when(classLoader).loadClass("importQualifier.className");
		WildcardImport wildcardImport = new WildcardImport("importQualifier.*", classLoader);

		Class<?> load = wildcardImport.load("className");

		assertThat(load, is(equalTo(clazz)));
		verify(classLoader).loadClass("importQualifier.className");
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		ClassLoader classLoader1 = mock(ClassLoader.class);
		ClassLoader classLoader2 = mock(ClassLoader.class);
		EqualsVerifier.forClass(WildcardImport.class).withPrefabValues(ClassLoader.class, classLoader1, classLoader2).allFieldsShouldBeUsed().usingGetClass()
		        .verify();
	}

}
