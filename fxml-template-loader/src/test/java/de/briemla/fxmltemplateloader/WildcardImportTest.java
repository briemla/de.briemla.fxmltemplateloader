package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

}
