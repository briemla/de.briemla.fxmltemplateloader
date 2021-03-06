package de.briemla.fxmltemplateloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.template.ITemplate;

interface TemplateLoader {

	void setController(Object controller);

	void setRoot(Object root);

	/**
	 * Load the given resource as FXML and return the root element.
	 *
	 * @param resource
	 *            {@link URL} to FXML file
	 * @return root element created from FXML file
	 * @throws IOException
	 *             in case the file can not be loaded or can not be parsed
	 */
	<T> T doLoad(URL resource) throws IOException;
	
	<T> T doLoad(URL resource, ResourceBundle bundle) throws IOException;

	ITemplate doLoadTemplate(URL resource) throws IOException;

	<T> T load(InputStream inputStream) throws IOException;

	ITemplate loadTemplate(InputStream xmlInput) throws IOException;

}