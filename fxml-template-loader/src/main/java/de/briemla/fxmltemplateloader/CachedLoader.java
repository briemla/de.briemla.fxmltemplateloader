package de.briemla.fxmltemplateloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.template.ITemplate;

public class CachedLoader implements TemplateLoader {

	private static final ResourceBundle withoutResourceBundle = null;
	
	private final TemplateLoader loader;
	private final Map<URL, ITemplate> cache;
	private Object controller;
	private Object root;

	public CachedLoader(TemplateLoader loader) {
		super();
		this.loader = loader;
		cache = new HashMap<>();
	}

	@Override
	public void setController(Object controller) {
		this.controller = controller;
	}

	@Override
	public void setRoot(Object root) {
		this.root = root;
	}

	@Override
	public <T> T doLoad(URL resource) throws IOException {
		return doLoad(resource, withoutResourceBundle);
	}
	
	@Override
	public <T> T doLoad(URL resource, ResourceBundle bundle) throws IOException {
		try {
			return loadViaTemplate(resource);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IOException("Could not instatiate Nodes.", e);
		}
	}

	private <T> T loadViaTemplate(URL resource)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		ITemplate template = rootify(doLoadTemplate(resource));
		if (null == controller) {
			return template.create();
		}
		return template.create(controller);
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

	private ITemplate rootify(ITemplate template) {
		if (null != root) {
			template.setRoot(root);
		}
		return template;
	}

}
