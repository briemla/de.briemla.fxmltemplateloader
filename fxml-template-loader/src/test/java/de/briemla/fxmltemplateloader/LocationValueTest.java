package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.net.URL;

import org.junit.Test;

public class LocationValueTest {

	@Test
	public void createFromUrl() throws Exception {
		TemplateRegistry registry = mock(TemplateRegistry.class);
		ClassLoader classLoader = mock(ClassLoader.class);
		URL location = new URL("file://test");
		LocationValue locationValue = new LocationValue(classLoader, location, "Value.url");
		Object expectedLocation = "file://test/Value.url";

		Object resolvedLocation = locationValue.create(registry);

		assertThat(resolvedLocation, is(equalTo(expectedLocation)));

		verifyZeroInteractions(registry);
		verifyZeroInteractions(classLoader);
	}

	@Test
	public void createFromClassLoader() throws Exception {
		TemplateRegistry registry = mock(TemplateRegistry.class);
		ClassLoader classLoader = mock(ClassLoader.class);
		URL expectedLocationUrl = new URL("file://test/Value.url");
		when(classLoader.getResource("Value.url")).thenReturn(expectedLocationUrl);
		URL location = new URL("file://test");
		LocationValue locationValue = new LocationValue(classLoader, location, "/Value.url");

		Object resolvedLocation = locationValue.create(registry);

		Object expectedLocation = expectedLocationUrl.toString();
		assertThat(resolvedLocation, is(equalTo(expectedLocation)));

		verifyZeroInteractions(registry);
	}
}
