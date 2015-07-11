package de.briemla.fxmltemplateloader.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.LoadException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.briemla.fxmltemplateloader.value.BasicTypeValue;
import de.briemla.fxmltemplateloader.value.ControllerMethodValue;
import de.briemla.fxmltemplateloader.value.IValue;
import de.briemla.fxmltemplateloader.value.LocationValue;
import de.briemla.fxmltemplateloader.value.ReferenceValue;

public class ValueResolverTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void resolveReference() throws Exception {
        String valueWithPrefix = "$referenceId";
        ReferenceValue testValue = new ReferenceValue("referenceId");
        ValueResolver valueResolver = new ValueResolver();

        IValue resolvedValue = valueResolver.resolve(valueWithPrefix, String.class);

        assertThat(resolvedValue, is(equalTo(testValue)));
    }

    /**
     * Long running test, because {@link URL#equals(Object)} will be called
     *
     * @see URL#equals(Object)
     * @throws Exception
     */
    @Test
    public void resolveUrl() throws Exception {
        String valueWithPrefix = "@SomeFileAs.url";
        ClassLoader classLoader = mock(ClassLoader.class);
        URL location = new URL("file://blubberTest");
        IValue testValue = new LocationValue(classLoader, location.toURI(), "SomeFileAs.url");
        ValueResolver valueResolver = new ValueResolver();
        valueResolver.setLocation(location);
        valueResolver.setClassLoader(classLoader);

        IValue resolvedValue = valueResolver.resolve(valueWithPrefix, String.class);

        assertThat(resolvedValue, is(equalTo(testValue)));
    }

    @Test
    public void resolveControllerMethod() throws Exception {
        String valueWithPrefix = "#methodName";
        IValue testValue = new ControllerMethodValue("methodName");
        ValueResolver valueResolver = new ValueResolver();

        IValue resolvedValue = valueResolver.resolve(valueWithPrefix, String.class);

        assertThat(resolvedValue, is(equalTo(testValue)));
    }

    @Test
    public void resolveResource() throws Exception {
        String bundlePath = ValueResolverTest.class.getPackage().getName() + ".ValueResolverTest";
        ResourceBundle bundle = ResourceBundle.getBundle(bundlePath, Locale.GERMAN);
        String keyWithResourcePrefix = "%testKey";
        IValue testValue = new BasicTypeValue("testValue");
        ValueResolver resolver = new ValueResolver(bundle);

        IValue resolvedValue = resolver.resolve(keyWithResourcePrefix, String.class);

        assertThat(resolvedValue, is(equalTo(testValue)));
    }

    @Test
    public void resolveMissingResourceKey() throws Exception {
        String bundlePath = ValueResolverTest.class.getPackage().getName() + ".ValueResolverTest";
        ResourceBundle bundle = ResourceBundle.getBundle(bundlePath, Locale.GERMAN);
        ValueResolver resolver = new ValueResolver(bundle);

        thrown.expect(LoadException.class);
        thrown.expectMessage(is(equalTo("Resource \"missingKey\" not found.")));
        resolver.resolve("%missingKey", String.class);
    }

    @Test
    public void resolveMissingResourceBundle() throws Exception {
        ValueResolver resolver = new ValueResolver();

        thrown.expect(LoadException.class);
        thrown.expectMessage(is(equalTo("No resources specified.")));
        resolver.resolve("%noResourcesSpecified", String.class);
    }
}
