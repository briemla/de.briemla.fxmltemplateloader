package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.LoadException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ValueResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
