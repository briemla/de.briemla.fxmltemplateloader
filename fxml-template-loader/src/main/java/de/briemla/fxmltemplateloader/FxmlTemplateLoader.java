package de.briemla.fxmltemplateloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.template.ITemplate;

public class FxmlTemplateLoader {

	private final TemplateLoader loader;

	FxmlTemplateLoader(TemplateLoader loader) {
		super();
		this.loader = loader;
	}

	public FxmlTemplateLoader() {
		this(new ResourceLoader());
	}

	public static <T> T load(URL resource) throws IOException {
		return createLoader().doLoad(resource);
	}

	private static FxmlTemplateLoader createLoader(ResourceBundle bundle) {
		return new FxmlTemplateLoader(new CachedLoader(new ResourceLoader(bundle)));
	}

	private static FxmlTemplateLoader createLoader() {
		return createLoader(null);
	}

	/**
	 * Load the given resource as FXML and return the root element.
	 *
	 * @param resource
	 *            {@link URL} to FXML file
	 * @param bundle
	 *            {@link ResourceBundle} to retrieve language specific texts
	 *            from
	 * @return root element created from FXML file
	 * @throws IOException
	 *             in case the file can not be loaded or can not be parsed
	 */
	public static <T> T load(URL resource, ResourceBundle bundle) throws IOException {
		return createLoader(bundle).doLoad(resource);
	}

	public static ITemplate loadTemplate(URL resource) throws IOException {
		return createLoader().doLoadTemplate(resource);
	}

	/**
	 * Load the given resource as FXML and return the root element.
	 *
	 * @param resource
	 *            {@link URL} to FXML file
	 * @return root element created from FXML file
	 * @throws IOException
	 *             in case the file can not be loaded or can not be parsed
	 */
	public <T> T doLoad(URL resource) throws IOException {
		return loader.doLoad(resource);
	}

	public void setController(Object controller) {
		loader.setController(controller);
	}

	public void setRoot(Object root) {
		loader.setRoot(root);
	}
	
	private ITemplate doLoadTemplate(URL resource) throws IOException {
		return loader.doLoadTemplate(resource);
	}

	public <T> T load(InputStream inputStream) throws IOException {
		return loader.load(inputStream);
	}

	public ITemplate loadTemplate(InputStream xmlInput) throws IOException {
		return loader.loadTemplate(xmlInput);
	}

}
