package org.gambi.tapestry5.cli;

import java.net.MalformedURLException;

import javax.validation.ValidationException;

import org.apache.commons.cli.ParseException;
import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.gambi.tapestry5.cli.modules.ComplexConstraintModule;
import org.gambi.tapestry5.cli.services.CLIOptionSource;
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
		IOCUtilities.addDefaultModules(builder);
		// Load all the local modules
		builder.add(CLIModule.class);
		// Add the test module
		// Add the Complex test modul e
		builder.add(ComplexConstraintModule.class);
		// builder.add(TestModule.class);

		// Build the registry
		registry = builder.build();
		registry.performRegistryStartup();
	}

	@After
	public void shutdown() {
		registry.shutdown();
		System.out.println("CLIParserTest.shutdown()\n\n\n\n\n");
	}

	@Test
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

	@Test
	public void parse() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-d", "33", "-a", "10", "--beta",
				"7axc", "-g", "the gamma input", "-e", "12", "first-arg",
				"second-args", "whaterver" };

		try {
			parser.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Parsing failed ");
			return;
		}
	}

	@Test
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

	@Test
	public void booleanOption() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-t", "false", "-a", "100", "--beta",
				"7xxs", "-g", "gamma", "--epsilon", "12", "-d", "15",
				"first-arg", "-u", "http://www.google.com", "-su",
				"http://www.bing.com", "-o", "123", "--jonny", "second-args",
				"whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}

		CLIOptionSource cliOptionSource = registry
				.getService(CLIOptionSource.class);
		Assert.assertEquals("false", cliOptionSource.valueForOption("t"));
		Assert.assertEquals("false", cliOptionSource.valueForOption("tommy"));

	}

	@Test
	public void flagTrue() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-t", "false", "-a", "100", "--beta",
				"7xxs", "-g", "gamma", "--epsilon", "12", "-d", "15",
				"first-arg", "-u", "http://www.google.com", "-su",
				"http://www.bing.com", "-o", "123", "--jonny", "second-args",
				"whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}

		CLIOptionSource cliOptionSource = registry
				.getService(CLIOptionSource.class);
		Assert.assertEquals("true", cliOptionSource.valueForOption("j"));
		Assert.assertEquals("true", cliOptionSource.valueForOption("jonny"));

	}

	@Test
	public void flagFalse() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-t", "false", "-a", "100", "--beta",
				"7xxs", "-g", "gamma", "--epsilon", "12", "-d", "15",
				"first-arg", "-u", "http://www.google.com", "-su",
				"http://www.bing.com", "-o", "123", "second-args", "whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}

		CLIOptionSource cliOptionSource = registry
				.getService(CLIOptionSource.class);
		Assert.assertEquals("false", cliOptionSource.valueForOption("j"));
		Assert.assertEquals("false", cliOptionSource.valueForOption("jonny"));

	}

	@Test
	public void vectorOption() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "7xxs", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "-u",
				"http://www.google.com", "-su", "http://www.bing.com", "-o",
				"123", "--jonny", "-v", "1", "2", "1", "13", "50" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception " + e.getMessage());
		}
	}

	@Test
	public void validateInputs() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "7xxs", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "first-arg", "-u",
				"http://www.google.com", "-su", "http://www.bing.com", "-o",
				"123", "--jonny", "-v", "1", "2", "blabl4", "second-args",
				"whaterver" };
		try {
			parser.parse(args);
		} catch (ValidationException e) {

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception " + e.getMessage());
		}

	}

	// @Test
	// This will result in a System.exit() call that cannot be managed
	// here !
	public void printHelp() throws MalformedURLException {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-h" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			Assert.fail("Exception " + e.getMessage());
		}
	}

	@Test
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

	@Test
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
