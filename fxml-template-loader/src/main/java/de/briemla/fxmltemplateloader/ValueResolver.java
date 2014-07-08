package de.briemla.fxmltemplateloader;

import static de.briemla.fxmltemplateloader.util.CodeSugar.to;
import static de.briemla.fxmltemplateloader.util.TypeUtil.convert;

import java.util.ResourceBundle;

import javafx.fxml.LoadException;

public class ValueResolver {

	private static final String RESOURCE_PREFIX = "%";

	private final ResourceBundle bundle;

	public ValueResolver() {
		this(null);
	}

	public ValueResolver(ResourceBundle bundle) {
		super();
		this.bundle = bundle;
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
	public Object resolve(String value, Class<?> type) throws LoadException {
		if (value.startsWith(RESOURCE_PREFIX)) {
			return resolveResource(value);
		}
		return convert(value, to(type));
	}

	private Object resolveResource(String value) throws LoadException {
		if (bundle == null) {
			throw new LoadException("No resources specified.");
		}
		String resourceKey = value.substring(1);
		if (bundle.containsKey(resourceKey)) {
			return bundle.getString(resourceKey);
		}
		throw new LoadException("Resource \"" + resourceKey + "\" not found.");
	}

}
