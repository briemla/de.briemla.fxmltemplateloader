package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TemplateRegistryTest {

	@Test
	public void register() throws Exception {
		TemplateRegistry registry = new TemplateRegistry();
		String id = "someId";
		Object previouslyAddedElement = new Object();
		registry.register(id, previouslyAddedElement);

		Object returnedElementFromRegistry = registry.getFxElement(id);

		assertThat(returnedElementFromRegistry, is(equalTo(previouslyAddedElement)));
	}
}
