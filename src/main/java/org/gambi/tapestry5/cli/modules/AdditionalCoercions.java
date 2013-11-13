package org.gambi.tapestry5.cli.modules;

import java.net.URL;
import java.util.Arrays;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;

/**
 * Define and contribute additional coercions.
 * 
 * @author alessiogambi
 * 
 */
public class AdditionalCoercions {
	@SuppressWarnings("rawtypes")
	public static void contributeTypeCoercer(
			Configuration<CoercionTuple> configuration) {
		Coercion<URL, String> urlToString = new Coercion<URL, String>() {

			public String coerce(URL arg0) {
				if (arg0 == null) {
					return null;
				} else {
					return arg0.toExternalForm();
				}
			}
		};

		configuration.add(new CoercionTuple<URL, String>(URL.class,
				String.class, urlToString));

		Coercion<String, URL> stringToURL = new Coercion<String, URL>() {

			public URL coerce(String arg0) {
				try {
					return new URL(arg0);
				} catch (Throwable e) {
					return null;
				}
			}
		};
		configuration.add(new CoercionTuple<String, URL>(String.class,
				URL.class, stringToURL));

		Coercion<String, String[]> stringToStringArray = new Coercion<String, String[]>() {

			public String[] coerce(String arg0) {
				System.out.println("\n\n Coercing string: " + arg0
						+ " to String[]");

				String _stringarray = arg0;
				_stringarray = _stringarray.trim();
				if (_stringarray.startsWith("[")) {
					_stringarray = _stringarray.replace("[", "");
				}

				if (_stringarray.endsWith("]")) {
					_stringarray = _stringarray.substring(0,
							_stringarray.length() - 1);
				}

				// NOTE THAT WE USE ', ' and not ','
				String[] tokens = _stringarray.split(", ");
				String[] result = new String[tokens.length];

				for (int i = 0; i < result.length; i++) {
					// We need to escape back to the original form
					result[i] = tokens[i].replaceAll("%2C", ",");
				}

				return result;
			}
		};
		configuration.add(new CoercionTuple<String, String[]>(String.class,
				String[].class, stringToStringArray));

		Coercion<char[], String> charsToString = new Coercion<char[], String>() {

			public String coerce(char[] arg0) {
				System.out.println("\n\n Coercing char[]: "
						+ Arrays.toString(arg0) + " to String");

				StringBuffer result = new StringBuffer();

				for (char c : arg0) {
					result.append(c);
				}

				return result.toString();
			}
		};
		configuration.add(new CoercionTuple<char[], String>(char[].class,
				String.class, charsToString));

		Coercion<char[][], String[]> charMatrixToStringArray = new Coercion<char[][], String[]>() {

			public String[] coerce(char[][] arg0) {
				System.out.println("\n\n Coercing char[][]: " + arg0
						+ " to String[]");

				String[] result = new String[arg0.length];

				for (int i = 0; i < arg0.length; i++) {
					char[] word = arg0[i];
					StringBuffer _result = new StringBuffer();
					for (char c : word) {
						_result.append(c);
					}
					result[i] = _result.toString();
				}

				return result;
			}
		};
		configuration.add(new CoercionTuple<char[][], String[]>(char[][].class,
				String[].class, charMatrixToStringArray));
	}
}
