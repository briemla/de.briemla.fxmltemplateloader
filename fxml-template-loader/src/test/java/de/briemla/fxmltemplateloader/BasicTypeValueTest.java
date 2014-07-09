package de.briemla.fxmltemplateloader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BasicTypeValueTest {

	@Test
	public void create() {
		Object value = new Object();
		BasicTypeValue encapsulatedValue = new BasicTypeValue(value);

		assertThat(encapsulatedValue.create(), is(sameInstance(value)));
	}
}
