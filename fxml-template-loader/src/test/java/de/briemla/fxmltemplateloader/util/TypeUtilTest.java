package de.briemla.fxmltemplateloader.util;

import static de.briemla.fxmltemplateloader.util.TypeUtil.convert;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.briemla.fxmltemplateloader.DummyEnum;

public class TypeUtilTest {

	@Test
	public void booleanType() {
		assertThat(convert("true", boolean.class), is(true));
	}

	@Test
	public void byteType() {
		assertThat(convert("2", byte.class), is(equalTo((byte) 2)));
	}

	@Test
	public void charType() {
		assertThat(convert("c", char.class), is(equalTo('c')));
	}

	@Test
	public void doubleType() {
		assertThat(convert("12.345", double.class), is(equalTo(12.345d)));
	}

	@Test
	public void enumType() {
		assertThat(convert("DUMMY_1", DummyEnum.class), is(equalTo(DummyEnum.DUMMY_1)));
	}

	@Test
	public void floatType() {
		assertThat(convert("54.321", float.class), is(equalTo(54.321f)));
	}

	@Test
	public void intType() {
		assertThat(convert("1234567", int.class), is(equalTo(1234567)));
	}

	@Test
	public void shortType() {
		assertThat(convert("123", short.class), is(equalTo((short) 123)));
	}

	@Test
	public void stringType() {
		assertThat(convert("sadjlsad", String.class), is(equalTo("sadjlsad")));
	}
}