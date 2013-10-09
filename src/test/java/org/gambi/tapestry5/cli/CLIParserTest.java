package org.gambi.tapestry5.cli;

import java.net.MalformedURLException;
import java.util.Arrays;

import javax.validation.ValidationException;

import org.apache.commons.cli.ParseException;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.gambi.tapestry5.cli.modules.TestModule;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CLIParserTest {

	private Registry registry;

	@Before
	public void setup() {
		// TODO Auto-generated constructor stub
		RegistryBuilder builder = new RegistryBuilder();
		// Load all the modules in the cp
		// CANNOT LOAD THIS !
		// IOCUtilities.addDefaultModules(builder);
		// Load all the local modules
		builder.add(CLIModule.class);
		// Add the test module

		builder.add(TestModule.class);

		// Build the registry
		registry = builder.build();
		registry.performRegistryStartup();
	}

	@After
	public void shutdown() {
		registry.shutdown();
	}

	// @Test
	public void unrecognizedOption() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-v", "-a", "10", "--beta", "7", "-g",
				"the gamma input", "first-arg", "second-args", "whaterver" };

		try {
			parser.parse(args);
			Assert.fail();
		} catch (ParseException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			Assert.fail("Wrong exception raised");
		}

	}

	// @Test
	public void parse() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "10", "--beta", "7axc", "-g",
				"the gamma input", "first-arg", "second-args", "whaterver" };

		try {
			parser.parse(args);
		} catch (Exception e) {
			// e.printStackTrace();
			Assert.fail();
		}

		SymbolSource symbolSource = registry.getService(SymbolSource.class);
		Assert.assertEquals("10", symbolSource.valueForSymbol("args:alfa"));
	}

	// @Test
	public void validateInteger() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "7xxs", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "first-arg", "-su",
				"http://www.google.com", "second-args", "whaterver" };

		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}
	}

	// @Test
	public void booleanOption() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "7xxs", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "first-arg", "-u",
				"http://www.google.com", "-su", "http://www.bing.com", "-o",
				"123", "--jonny", "second-args", "whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}

		SymbolSource symbolSource = registry.getService(SymbolSource.class);
		Assert.assertEquals("false", symbolSource.valueForSymbol("args:tommy"));

	}

	@Test
	public void vectorOption() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "7xxs", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "first-arg", "-u",
				"http://www.google.com", "-su", "http://www.bing.com", "-o",
				"123", "--jonny", "-v", "1", "2", "blabl4", "second-args",
				"whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}

		SymbolSource symbolSource = registry.getService(SymbolSource.class);
		Assert.assertNotNull(symbolSource.valueForSymbol("args:vector"));

		String[] vector = new String[] { "1", "2", "blabl4" };
		Assert.assertTrue(Arrays.toString(vector).equals(
				symbolSource.valueForSymbol("args:vector")));

	}

	// @Test
	public void validateStringUrl() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "7xxs", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "first-arg", "-u",
				"http://www.google.com", "-su", "http://www.bing.com", "-o",
				"123", "second-args", "whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}
	}

	// @Test
	public void validate() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "-1", "--beta", "734", "-g", "",
				"first-arg", "second-args", "whaterver" };

		// alpha is invalid !
		try {
			parser.parse(args);
		} catch (ValidationException e) {
			// e.printStackTrace();
			return;
		} catch (Exception e) {
			Assert.fail("Wrong exception " + e.getMessage());
		}
		Assert.fail("Validation Exception not raised");
	}
}
