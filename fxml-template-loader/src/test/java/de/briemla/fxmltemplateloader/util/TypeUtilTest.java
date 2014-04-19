package de.briemla.fxmltemplateloader.util;

import static de.briemla.fxmltemplateloader.util.TypeUtil.convert;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import de.briemla.fxmltemplateloader.DummyEnum;

public class TypeUtilTest {

	@Test
	public void bigIntegerType() {
		assertThat(convert(null, BigInteger.class), is(nullValue()));
		assertThat(convert("2423424", BigInteger.class), is(equalTo(new BigInteger("2423424"))));
		assertThat(convert("40981418340912", BigInteger.class), is(equalTo(new BigInteger("40981418340912"))));
	}

	@Test
	public void bigDecimalType() {
		assertThat(convert(null, BigDecimal.class), is(nullValue()));
		assertThat(convert("23479249.234798324", BigDecimal.class), is(equalTo(new BigDecimal("23479249.234798324"))));
		assertThat(convert("798109843179.6587164313876132", BigDecimal.class), is(equalTo(new BigDecimal("798109843179.6587164313876132"))));
	}

	@Test
	public void booleanType() {
		assertThat(convert(null, Boolean.class), is(nullValue()));
		assertThat(convert("true", boolean.class), is(true));
		assertThat(convert("false", boolean.class), is(false));
		assertThat(convert("true", Boolean.class), is(true));
		assertThat(convert("false", Boolean.class), is(false));
	}

	@Test
	public void byteType() {
		assertThat(convert(null, Byte.class), is(nullValue()));
		assertThat(convert("2", byte.class), is(equalTo((byte) 2)));
		assertThat(convert("2", Byte.class), is(equalTo(new Byte((byte) 2))));
	}

	@Test
	public void charType() {
		assertThat(convert(null, Character.class), is(nullValue()));
		assertThat(convert("c", char.class), is(equalTo('c')));
		assertThat(convert("d", Character.class), is(equalTo(new Character('d'))));
	}

	@Test
	public void doubleType() {
		assertThat(convert(null, Double.class), is(nullValue()));
		assertThat(convert("12.345", double.class), is(equalTo(12.345d)));
		assertThat(convert("23.456", Double.class), is(equalTo(new Double(23.456d))));
	}

	@Test
	public void enumType() {
		assertThat(convert(null, DummyEnum.class), is(nullValue()));
		assertThat(convert("DUMMY_1", DummyEnum.class), is(equalTo(DummyEnum.DUMMY_1)));
		assertThat(convert("DUMMY_0", DummyEnum.class), is(equalTo(DummyEnum.DUMMY_0)));
	}

	@Test
	public void floatType() {
		assertThat(convert(null, Float.class), is(nullValue()));
		assertThat(convert("54.321", float.class), is(equalTo(54.321f)));
		assertThat(convert("43.210", Float.class), is(equalTo(new Float(43.210f))));
	}

	@Test
	public void intType() {
		assertThat(convert(null, Integer.class), is(nullValue()));
		assertThat(convert("1234567", int.class), is(equalTo(1234567)));
		assertThat(convert("2345678", Integer.class), is(equalTo(new Integer(2345678))));
	}

	@Test
	public void numberType() {
		assertThat(convert(null, Number.class), is(nullValue()));
		assertThat(convert("13141230", Number.class), is(equalTo(new Integer(13141230))));
		assertThat(convert("743911239", Number.class), is(equalTo(new Integer(743911239))));
		assertThat(convert("13141230.37129313", Number.class), is(equalTo(new Double(13141230.37129313d))));
		assertThat(convert("743911239.571987123", Number.class), is(equalTo(new Double(743911239.571987123d))));
	}

	@Test
	public void shortType() {
		assertThat(convert(null, Short.class), is(nullValue()));
		assertThat(convert("123", short.class), is(equalTo((short) 123)));
		assertThat(convert("234", Short.class), is(equalTo(new Short((short) 234))));
	}

	@Test
	public void stringType() {
		assertThat(convert(null, String.class), is(nullValue()));
		assertThat(convert("sadjlsad", String.class), is(equalTo("sadjlsad")));
		assertThat(convert("kksladjklas", String.class), is(equalTo("kksladjklas")));
	}
}
