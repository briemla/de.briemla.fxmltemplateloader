package de.briemla.fxmltemplateloader.parser;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;
import static de.briemla.fxmltemplateloader.util.TypeUtil.convert;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import de.briemla.fxmltemplateloader.value.BasicTypeValue;
import de.briemla.fxmltemplateloader.value.ControllerMethodValue;
import de.briemla.fxmltemplateloader.value.IValue;
import de.briemla.fxmltemplateloader.value.LocationValue;
import de.briemla.fxmltemplateloader.value.ReferenceValue;
import javafx.fxml.LoadException;

public class ValueResolver {

    private static final String LOCATION_PREFIX = "@";
    private static final String RESOURCE_PREFIX = "%";
    private static final String CONTROLLER_METHOD_PREFIX = "#";
    private static final String REFERENCE_PREFIX = "$";

    private ResourceBundle bundle;
    private URL location;
    private ClassLoader classLoader;

    public ValueResolver() {
        super();
    }

    ValueResolver(ResourceBundle bundle) {
        super();
        this.bundle = bundle;
    }

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setLocation(URL location) {
        this.location = location;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Values for attributes in FXML can start with some special characters. This method resolves those strings.
     *
     * For more information about all special characters see FXMLLoader#Element#resolvePrefixedValue
     *
     * @param value
     *            value to be resolved
     * @param type
     *            target type of value
     * @return value in target type or resolved string for resources
     * @throws LoadException
     *             when resource bundle has not been set or the resource key is not available. FXMLLoader throws same LoadExceptions
     */
    public IValue resolve(String value, Class<?> type) throws LoadException {
        if (value.startsWith(RESOURCE_PREFIX)) {
            return new BasicTypeValue(resolveResource(value));
        }
        if (value.startsWith(CONTROLLER_METHOD_PREFIX)) {
            return new ControllerMethodValue(unwrap(value));
        }
        if (value.startsWith(LOCATION_PREFIX)) {
            try {
                return new LocationValue(classLoader, location.toURI(), unwrap(value));
            } catch (URISyntaxException exception) {
                throw new LoadException(exception);
            }
        }
        if (value.startsWith(REFERENCE_PREFIX)) {
            return new ReferenceValue(unwrap(value));
        }
        return new BasicTypeValue(convert(value, to(type)));
    }

    private Object resolveResource(String value) throws LoadException {
        if (bundle == null) {
            throw new LoadException("No resources specified.");
        }
        String resourceKey = unwrap(value);
        if (bundle.containsKey(resourceKey)) {
            return bundle.getString(resourceKey);
        }
        throw new LoadException("Resource \"" + resourceKey + "\" not found.");
    }

    private static String unwrap(String value) {
        return value.substring(1);
    }

    public boolean hasClassLoader() {
        return classLoader != null;
    }

}
