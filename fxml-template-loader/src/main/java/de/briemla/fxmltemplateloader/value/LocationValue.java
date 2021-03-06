package de.briemla.fxmltemplateloader.value;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javafx.fxml.LoadException;

import de.briemla.fxmltemplateloader.template.TemplateRegistry;

public class LocationValue implements IValue {

    private final ClassLoader classLoader;
    private final URI location;
    private final String value;

    /**
     * Value object for FXML locations.
     */
    public LocationValue(ClassLoader classLoader, URI location, String value) {
        super();
        if (classLoader == null) {
            throw new NullPointerException("ClassLoader is not allowed to be null");
        }
        if (location == null) {
            throw new NullPointerException("Location is not allowed to be null");
        }
        this.classLoader = classLoader;
        this.location = location;
        this.value = value;
    }

    /**
     * Behaviour is same as in FXMLLoader.
     */
    @Override
    public Object create(TemplateRegistry registry) throws LoadException {
        if (value.charAt(0) == '/') {
            URL resource = classLoader.getResource(value.substring(1));
            if (resource == null) {
                throw new LoadException("Invalid resource: " + value + " not found on classpath");
            }
            return resource.toString();
        }
        try {
            return new URL(location.toURL(), value).toString();
        } catch (MalformedURLException exception) {
            System.err.println(location + "/" + value);
        }
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classLoader == null) ? 0 : classLoader.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LocationValue other = (LocationValue) obj;
        if (classLoader == null) {
            if (other.classLoader != null) {
                return false;
            }
        } else if (!classLoader.equals(other.classLoader)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
