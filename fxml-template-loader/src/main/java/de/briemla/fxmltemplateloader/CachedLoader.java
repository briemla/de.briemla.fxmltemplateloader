package de.briemla.fxmltemplateloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.briemla.fxmltemplateloader.template.ITemplate;

public class CachedLoader implements TemplateLoader {

	private final TemplateLoader loader;
	private final Map<URL, ITemplate> cache;
	private Object controller;

	public CachedLoader(TemplateLoader loader) {
		super();
		this.loader = loader;
		cache = new HashMap<>();
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setController(Object controller) {
		this.controller = controller;
	}

	@Override
	public void setRoot(Object root) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T doLoad(URL resource) throws IOException {
		try {
			return loadViaTemplate(resource);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IOException("Could not instatiate Nodes.", e);
		}
	}

	private <T> T loadViaTemplate(URL resource)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		if (null == controller) {
			return doLoadTemplate(resource).create();
		}
		return doLoadTemplate(resource).create(controller);
	}

	@Override
	public ITemplate doLoadTemplate(URL resource) throws IOException {
		if (cacheContains(resource)) {
			return cachedTemplateFor(resource);
		}
		ITemplate template = loadTemplateFor(resource);
		cache(template, resource);
		return template;
	}

	private boolean cacheContains(URL resource) {
		return cache.containsKey(resource);
	}

	private ITemplate cachedTemplateFor(URL resource) {
		return cache.get(resource);
	}

	private ITemplate loadTemplateFor(URL resource) throws IOException {
		return loader.doLoadTemplate(resource);
	}

	private void cache(ITemplate template, URL resource) {
		cache.put(resource, template);
	}

	@Override
	public <T> T load(InputStream inputStream) throws IOException {
		return loader.load(inputStream);
	}

	@Override
	public ITemplate loadTemplate(InputStream xmlInput) throws IOException {
		return loader.loadTemplate(xmlInput);
	}

}
