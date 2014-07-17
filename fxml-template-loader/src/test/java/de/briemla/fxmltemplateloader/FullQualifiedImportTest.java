package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class FullQualifiedImportTest {

	@Test
	public void matches() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		FullQualifiedImport fullQualifiedImport = new FullQualifiedImport("importQualifier", classLoader);

		assertThat(fullQualifiedImport.matches("importQualifier"), is(equalTo(true)));
		verifyZeroInteractions(classLoader);
	}

	@Test()
	public void matchesNull() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		FullQualifiedImport fullQualifiedImport = new FullQualifiedImport("importQualifier", classLoader);

		assertThat(fullQualifiedImport.matches(null), is(equalTo(false)));
		verifyZeroInteractions(classLoader);
	}

	@Test()
	public void matchesNot() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		FullQualifiedImport fullQualifiedImport = new FullQualifiedImport("importQualifier", classLoader);

		assertThat(fullQualifiedImport.matches("notMatchingQualifier"), is(equalTo(false)));
		verifyZeroInteractions(classLoader);
	}

	@Test
	public void load() throws Exception {
		ClassLoader classLoader = mock(ClassLoader.class);
		Class<?> clazz = Object.class;
		doReturn(clazz).when(classLoader).loadClass("importQualifier");
		FullQualifiedImport fullQualifiedImport = new FullQualifiedImport("importQualifier", classLoader);

		Class<?> loadedClass = fullQualifiedImport.load("importQualifier");
		assertThat(loadedClass, is(equalTo(clazz)));

		verify(classLoader).loadClass("importQualifier");
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		ClassLoader classLoader1 = mock(ClassLoader.class);
		ClassLoader classLoader2 = mock(ClassLoader.class);
		EqualsVerifier.forClass(FullQualifiedImport.class).withPrefabValues(ClassLoader.class, classLoader1, classLoader2).allFieldsShouldBeUsed()
		        .usingGetClass().verify();
	}
}
