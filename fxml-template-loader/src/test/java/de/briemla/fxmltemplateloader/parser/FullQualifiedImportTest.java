package de.briemla.fxmltemplateloader.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class FullQualifiedImportTest {

	private ClassLoader classLoader;
	private FullQualifiedImport fullQualifiedImport;

	@Before
	public void initialize() {
		classLoader = mock(ClassLoader.class);
		fullQualifiedImport = new FullQualifiedImport("importQualifier", classLoader);
	}

	@Test
	public void matches() throws Exception {
		assertThat(fullQualifiedImport.matches("importQualifier"), is(equalTo(true)));
		verifyZeroInteractions(classLoader);
	}

	@Test()
	public void matchesNull() throws Exception {
		assertThat(fullQualifiedImport.matches(null), is(equalTo(false)));
		verifyZeroInteractions(classLoader);
	}

	@Test
	public void matchesNot() throws Exception {
		assertThat(fullQualifiedImport.matches("notMatchingQualifier"), is(equalTo(false)));
		verifyZeroInteractions(classLoader);
	}

	@Test
	public void load() throws Exception {
		Class<?> clazz = Object.class;
		load(clazz);

		Class<?> loadedClass = fullQualifiedImport.load("importQualifier");

		assertThat(loadedClass, is(equalTo(clazz)));

		verify(classLoader).loadClass("importQualifier");
	}

	private void load(Class<?> clazz) throws ClassNotFoundException {
		doReturn(clazz).when(classLoader).loadClass("importQualifier");
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		ClassLoader classLoader1 = mock(ClassLoader.class);
		ClassLoader classLoader2 = mock(ClassLoader.class);
		EqualsVerifier.forClass(FullQualifiedImport.class)
				.withPrefabValues(ClassLoader.class, classLoader1, classLoader2).allFieldsShouldBeUsed().usingGetClass()
				.verify();
	}
}
