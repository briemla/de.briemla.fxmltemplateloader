package de.briemla.fxmltemplateloader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class CachedLoaderTest {

	@Test
	public void cachesAlreadyLoadedResources() throws IOException {
		URL resource = new URL("file://");
		TemplateLoader loader = mock(TemplateLoader.class);
		CachedLoader cache = new CachedLoader(loader);

		cache.doLoad(resource);
		
		verify(loader).doLoad(resource);
	}

}
