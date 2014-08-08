package de.briemla.fxmltemplateloader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.net.URL;

import org.junit.Test;

public class LocationValueTest {

	@Test
	public void create() throws Exception {
		TemplateRegistry registry = mock(TemplateRegistry.class);
		ClassLoader classLoader = mock(ClassLoader.class);
		URL location = new URL("file://test");
		LocationValue locationValue = new LocationValue(classLoader, location, "Value.url");

		locationValue.create(registry);

		verifyZeroInteractions(registry);
	}
}
