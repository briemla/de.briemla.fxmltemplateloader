package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TemplateRegistryTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void register() throws Exception {
		TemplateRegistry registry = new TemplateRegistry();
		String id = "someId";
		Object previouslyAddedElement = new Object();
		registry.register(id, previouslyAddedElement);

		Object returnedElementFromRegistry = registry.getFxElement(id);

		assertThat(returnedElementFromRegistry, is(equalTo(previouslyAddedElement)));
	}

	@Test
	public void registerSameIdTwice() throws Exception {
		TemplateRegistry registry = new TemplateRegistry();
		Object id = "someId";
		Object firstObject = new Object();
		Object secondObject = new Object();
		registry.register(id, firstObject);

		thrown.expect(RuntimeException.class);
		thrown.expectMessage(startsWith("ID already registered."));

		registry.register(id, secondObject);
	}
}
