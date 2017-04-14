package de.briemla.fxmltemplateloader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import de.briemla.fxmltemplateloader.parser.ImportCollection;
import de.briemla.fxmltemplateloader.parser.ImportFactory;
import de.briemla.fxmltemplateloader.parser.Parser;
import de.briemla.fxmltemplateloader.parser.ValueResolver;
import de.briemla.fxmltemplateloader.template.ITemplate;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.LoadException;

public class FxmlTemplateLoader {

	private final Parser parser;
	
	private Object root;

    /**
     * Class to load FXML files. The class creates {@link ITemplate} which can be reused to speed up
     * generation of several objects from the same FXML file without reading it several times from
     * the file system.
     */
    public FxmlTemplateLoader() {
        super();
        ImportFactory factory = new ImportFactory(FxmlTemplateLoader.class.getClassLoader());
        ImportCollection imports = new ImportCollection(factory);
        JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
        ValueResolver valueResolver = new ValueResolver();
        parser = new Parser(factory, imports, builderFactory, valueResolver);
    }

    public static <T> T load(URL resource) throws IOException {
        return new FxmlTemplateLoader().doLoad(resource);
    }

    /**
     * Load the given resource as FXML and return the root element.
     *
     * @param resource
     *            {@link URL} to FXML file
     * @param bundle
     *            {@link ResourceBundle} to retrieve language specific texts from
     * @return root element created from FXML file
     * @throws IOException
     *             in case the file can not be loaded or can not be parsed
     */
    public static <T> T load(URL resource, ResourceBundle bundle) throws IOException {
        FxmlTemplateLoader fxmlTemplateLoader = new FxmlTemplateLoader();
        fxmlTemplateLoader.setResourceBundle(bundle);
        return fxmlTemplateLoader.doLoad(resource);
    }

    private void setResourceBundle(ResourceBundle bundle) {
    	parser.setResourceBundle(bundle);
    }

    /**
     * This method changes the classLoader on an necessary objects.
     *
     * @param classLoader
     *            new {@link ClassLoader} instance. Not allowed to be <code>null</code>
     */
    public void setClassLoader(ClassLoader classLoader) {
        parser.setClassLoader(classLoader);
    }

    public void setController(Object controller) {
        parser.setController(controller);
    }

    public void setLocation(URL location) {
    	parser.setLocation(location);
    }
    
    public void setRoot(Object root) {
    	this.root = root;
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
        try {
            if (parser.controller() != null) {
                return doLoadTemplate(resource).create(parser.controller());
            }
            return doLoadTemplate(resource).create();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new IOException("Could not instatiate Nodes.", exception);
        }
    }
    
    public static ITemplate loadTemplate(URL resource) throws IOException {
    	return new FxmlTemplateLoader().doLoadTemplate(resource);
    }
    
    private ITemplate doLoadTemplate(URL resource) throws IOException {
    	setLocation(resource);
    	try (InputStream xmlInput = resource.openStream()) {
    		return loadTemplate(xmlInput);
    	}
    }

	public <T> T load(InputStream inputStream) throws IOException {
		try {
			return loadTemplate(inputStream).create();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
			throw new IOException("Could not instatiate Nodes.", exception);
		}
	}

	public ITemplate loadTemplate(InputStream xmlInput)
			throws IOException {
		try {
			return doLoadTemplate(xmlInput);
		} catch (NoSuchMethodException | SecurityException exception) {
			throw new IOException("Could not find correct classes.", exception);
		} catch (XMLStreamException exception) {
			throw new IOException("Could not parse XML.", exception);
		} catch (IllegalArgumentException exception) {
    		throw new IOException("Something went wrong with the arguments.", exception);
    	}
	}

	private ITemplate doLoadTemplate(InputStream xmlInput)
			throws FactoryConfigurationError, XMLStreamException, NoSuchMethodException, LoadException {
		ITemplate template = parser.doLoadTemplate(xmlInput);
		if (null != root) {
			template.setRoot(root);
		}
		return template;
	}
}
