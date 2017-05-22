package de.briemla.fxmltemplateloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.template.ITemplate;

public class CachedLoader implements TemplateLoader {

	private final TemplateLoader loader;

	public CachedLoader(TemplateLoader loader) {
		super();
		this.loader = loader;
	}

	@Override
	public void setResourceBundle(ResourceBundle bundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setController(Object controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocation(URL location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRoot(Object root) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T doLoad(URL resource) throws IOException {
		return loader.doLoad(resource);
	}

	@Override
	public ITemplate doLoadTemplate(URL resource) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T load(InputStream inputStream) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITemplate loadTemplate(InputStream xmlInput) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
