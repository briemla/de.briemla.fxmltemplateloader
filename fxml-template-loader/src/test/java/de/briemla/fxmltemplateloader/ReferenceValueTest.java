package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class ReferenceValueTest {

	@Test
	public void create() throws Exception {
		TemplateRegistry registry = mock(TemplateRegistry.class);
		Object fxElement = new Object();
		when(registry.getFxElement("referenceId")).thenReturn(fxElement);
		ReferenceValue referenceValue = new ReferenceValue("referenceId");
		Object createdObject = referenceValue.create(registry);

		assertThat(createdObject, is(sameInstance(fxElement)));
	}
}
