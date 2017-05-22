package de.briemla.fxmltemplateloader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
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
	public void loadsElementViaOtherLoader() throws Exception {
		cache.doLoad(resource);

		verify(loader).doLoadTemplate(resource);
		verify(template).create();
	}

	@Test
	public void loadsElementWithController() throws Exception {
		Object controller = mock(Object.class);

		cache.setController(controller);
		cache.doLoad(resource);

		verify(template).create(controller);
	}
	
	@Test
	public void loadsElementWithRoot() throws Exception {
		Object root = mock(Object.class);
		
		cache.setRoot(root);
		cache.doLoad(resource);
		
		verify(template).setRoot(root);
	}
	
	@Test
	public void loadsTemplateViaOtherLoader() throws IOException {
		cache.doLoadTemplate(resource);

		verify(loader).doLoadTemplate(resource);
	}

	@Test
	public void cachesTemplate() throws IOException {
		cache.doLoadTemplate(resource);
		cache.doLoadTemplate(resource);

		verify(loader).doLoadTemplate(resource);
	}

	@Test
	public void cannotCacheElementsFromInputStream() throws Exception {
		InputStream firstStream = fromFreshInputStream();
		InputStream secondStream = fromFreshInputStream();

		cache.load(firstStream);
		cache.load(secondStream);

		verify(loader).load(firstStream);
		verify(loader).load(secondStream);
	}

	@Test
	public void cannotCacheTemplatesFromInputStream() throws Exception {
		InputStream firstStream = fromFreshInputStream();
		InputStream secondStream = fromFreshInputStream();

		cache.loadTemplate(firstStream);
		cache.loadTemplate(secondStream);

		verify(loader).loadTemplate(firstStream);
		verify(loader).loadTemplate(secondStream);
	}

	private static InputStream fromFreshInputStream() {
		return mock(InputStream.class);
	}

}
