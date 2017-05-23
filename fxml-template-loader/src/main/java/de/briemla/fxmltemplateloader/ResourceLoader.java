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

class ResourceLoader implements TemplateLoader {

	private static final ResourceBundle withoutResourceBundle = null;
	
	private final Parser parser;
	private Object root;
	private final ResourceBundle bundle;

    /**
     * Class to load FXML files. The class creates {@link ITemplate} which can be reused to speed up
     * generation of several objects from the same FXML file without reading it several times from
     * the file system.
     */
    ResourceLoader(ResourceBundle bundle) {
        super();
		this.bundle = bundle;
        ImportFactory factory = new ImportFactory(ResourceLoader.class.getClassLoader());
        ImportCollection imports = new ImportCollection(factory);
        JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
        ValueResolver valueResolver = new ValueResolver();
        parser = new Parser(factory, imports, builderFactory, valueResolver);
        parser.setResourceBundle(this.bundle);
    }
    
    ResourceLoader() {
    	this(null);
    }

    @Override
	public void setController(Object controller) {
        parser.setController(controller);
    }
    
    @Override
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
    @Override
    public <T> T doLoad(URL resource) throws IOException {
    	return doLoad(resource, withoutResourceBundle);
    }
    
    @Override
	public <T> T doLoad(URL resource, ResourceBundle bundle) throws IOException {
        try {
            ITemplate template = rootify(doLoadTemplate(resource));
			if (parser.controller() != null) {
                return template.create(parser.controller());
            }
            return template.create();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new IOException("Could not instatiate Nodes.", exception);
        }
    }
    
    @Override
	public ITemplate doLoadTemplate(URL resource) throws IOException {
    	setLocation(resource);
    	try (InputStream xmlInput = resource.openStream()) {
    		return loadTemplate(xmlInput);
    	}
    }

	private void setLocation(URL location) {
    	parser.setLocation(location);
    }

    @Override
	public <T> T load(InputStream inputStream) throws IOException {
		try {
			return rootify(loadTemplate(inputStream)).create();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
			throw new IOException("Could not instatiate Nodes.", exception);
		}
	}

    @Override
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
		return parser.doLoadTemplate(xmlInput);
	}

	private ITemplate rootify(ITemplate template) {
		if (null != root) {
			template.setRoot(root);
		}
		return template;
	}
}
