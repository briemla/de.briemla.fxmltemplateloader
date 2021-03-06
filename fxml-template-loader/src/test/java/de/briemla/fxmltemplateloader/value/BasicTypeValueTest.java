package de.briemla.fxmltemplateloader.value;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class BasicTypeValueTest {

    @Test
    public void create() {
        Object value = new Object();
        BasicTypeValue encapsulatedValue = new BasicTypeValue(value);

        assertThat(encapsulatedValue.create(null), is(sameInstance(value)));
    }

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(BasicTypeValue.class).allFieldsShouldBeUsed().usingGetClass()
                .verify();
    }
}
