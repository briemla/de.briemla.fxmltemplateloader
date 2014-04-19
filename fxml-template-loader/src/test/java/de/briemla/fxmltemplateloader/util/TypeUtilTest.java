package de.briemla.fxmltemplateloader.util;

import static de.briemla.fxmltemplateloader.util.TypeUtil.convert;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Test;

import de.briemla.fxmltemplateloader.DummyEnum;

public class TypeUtilTest {

	@Test
	public void bigIntegerType() {
		assertThat(convert("2423424", BigInteger.class), is(equalTo(new BigInteger("2423424"))));
	}

	@Test
	public void booleanType() {
		assertThat(convert("true", boolean.class), is(true));
		assertThat(convert("false", boolean.class), is(false));
		assertThat(convert("true", Boolean.class), is(true));
		assertThat(convert("false", Boolean.class), is(false));
	}

	@Test
	public void byteType() {
		assertThat(convert("2", byte.class), is(equalTo((byte) 2)));
		assertThat(convert("2", Byte.class), is(equalTo(new Byte((byte) 2))));
	}

	@Test
	public void charType() {
		assertThat(convert("c", char.class), is(equalTo('c')));
		assertThat(convert("d", Character.class), is(equalTo(new Character('d'))));
	}

	@Test
	public void doubleType() {
		assertThat(convert("12.345", double.class), is(equalTo(12.345d)));
		assertThat(convert("23.456", Double.class), is(equalTo(new Double(23.456d))));
	}

	@Test
	public void enumType() {
		assertThat(convert("DUMMY_1", DummyEnum.class), is(equalTo(DummyEnum.DUMMY_1)));
		assertThat(convert("DUMMY_0", DummyEnum.class), is(equalTo(DummyEnum.DUMMY_0)));
	}

	@Test
	public void floatType() {
		assertThat(convert("54.321", float.class), is(equalTo(54.321f)));
		assertThat(convert("43.210", Float.class), is(equalTo(new Float(43.210f))));
	}

	@Test
	public void intType() {
		assertThat(convert("1234567", int.class), is(equalTo(1234567)));
		assertThat(convert("2345678", Integer.class), is(equalTo(new Integer(2345678))));
	}

	@Test
	public void shortType() {
		assertThat(convert("123", short.class), is(equalTo((short) 123)));
		assertThat(convert("234", Short.class), is(equalTo(new Short((short) 234))));
	}

	@Test
	public void stringType() {
		assertThat(convert("sadjlsad", String.class), is(equalTo("sadjlsad")));
		assertThat(convert("kksladjklas", String.class), is(equalTo("kksladjklas")));
	}
}
