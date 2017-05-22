package de.briemla.fxmltemplateloader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import de.briemla.fxmltemplateloader.template.ITemplate;

public class CachedLoaderTest {

	private URL resource;
	private TemplateLoader loader;
	private CachedLoader cache;
	private ITemplate template;

	@Before
	public void initialize() throws IOException {
		resource = new URL("file://");
		loader = mock(TemplateLoader.class);
		cache = new CachedLoader(loader);
		template = mock(ITemplate.class);

		when(loader.doLoadTemplate(resource)).thenReturn(template);
	}

	@Test
	public void loadsViaOtherLoader() throws IOException {
		cache.doLoadTemplate(resource);

		verify(loader).doLoadTemplate(resource);
	}

	@Test
	public void cachesTemplate() throws IOException {
		cache.doLoadTemplate(resource);
		cache.doLoadTemplate(resource);

		verify(loader).doLoadTemplate(resource);
	}

}
