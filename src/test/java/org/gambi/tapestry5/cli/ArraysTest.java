package org.gambi.tapestry5.cli;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ArraysTest {

	public String[] coerce(String arg0) {
		System.out.println("\n\n" + arg0);

		String _stringarray = arg0;
		_stringarray = _stringarray.trim();
		if (_stringarray.startsWith("[")) {
			_stringarray = _stringarray.replace("[", "");
		}

		if (_stringarray.endsWith("]")) {
			_stringarray = _stringarray.substring(0, _stringarray.length() - 1);
		}

		// NOTE THAT WE USE ', ' and not ','
		String[] tokens = _stringarray.split(", ");
		String[] result = new String[tokens.length];

		for (int i = 0; i < result.length; i++) {
			// Escaping
			result[i] = tokens[i].replaceAll("%2C", ",");
		}

		return result;
	}

	@Test
	public void parseComma() {
		String[] args = new String[] { ",", ", ", "pippo" };
		System.out.println(Arrays.toString(args));

		// char[][] values = new char[args.length][];
		// // We use this format to avoid failing in parsing ,
		// for (int i = 0; i < args.length; i++) {
		// values[i] = args[i].toCharArray();
		// }

		String[] values = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			values[i] = args[i].replaceAll(",", "%2C");
		}
		// Transform back to String. This is necessary because the
		// target property may not be a String[]
		System.out.println(Arrays.toString(values));

		String[] coerced = coerce(Arrays.toString(values));

		System.out.println(Arrays.toString(coerced));
		Assert.assertTrue(Arrays.toString(coerced)
				.equals(Arrays.toString(args)));
	}

	// @Test
	public void parse() {
		String[] args = new String[] { "a", "ciccio", "pippo" };
		System.out.println(Arrays.toString(args));

		// char[][] values = new char[args.length][];
		// // We use this format to avoid failing in parsing ,
		// for (int i = 0; i < args.length; i++) {
		// values[i] = args[i].toCharArray();
		// }

		String[] values = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			values[i] = args[i].replaceAll(",", "%2C");
		}
		// Transform back to String. This is necessary because the
		// target property may not be a String[]
		System.out.println(Arrays.toString(values));

	}
}
