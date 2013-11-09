package org.gambi.tapestry5.cli.validators;

import java.util.ArrayList;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.gambi.tapestry5.cli.CLIModule;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.modules.ComplexConstraintModule;
import org.gambi.tapestry5.cli.modules.TestModule;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComplexConstraintValidatorTest {
	private Registry registry;
	private RegistryBuilder builder;

	@Before
	public void setup() {
		// TODO Auto-generated constructor stub
		builder = new RegistryBuilder();
		// Load all the modules in the cp
		IOCUtilities.addDefaultModules(builder);

		// Load all the local modules
		builder.add(CLIModule.class);
		// Add the test module
		builder.add(TestModule.class);
		// Add the Complex test module
		builder.add(ComplexConstraintModule.class);

		registry = builder.build();
		registry.performRegistryStartup();
	}

	@After
	public void shutdown() {
		if (registry != null) {
			registry.shutdown();
		}
	}

	// @Test
	public void containsTest() {
		ArrayList<CLIOption> options = new ArrayList<CLIOption>();

		CLIOption o1 = new CLIOption("1", "bb", 0, true, "description");
		CLIOption o2 = new CLIOption("3", "dasdsabb", 0, true, "description");
		CLIOption o3 = new CLIOption("1", "bb", 0, true, "bibi description");

		System.out
				.println("ComplexConstraintValidatorTest.containsTest() o1 == o3 "
						+ o1.equals(o3));

		System.out
				.println("ComplexConstraintValidatorTest.containsTest() o3 == o1 "
						+ o3.equals(o1));

		options.add(o1);
		options.add(o2);

		System.out
				.println("ComplexConstraintValidatorTest.containsTest() Contains o1: "
						+ options.contains(o1));
		System.out
				.println("ComplexConstraintValidatorTest.containsTest() Contains o3: "
						+ options.contains(o3));
	}

	@Test
	public void okOptions() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "-d", "10", "--beta",
				"ciccio", "-g", "gamma", "--epsilon", "12", "-d", "15", "-v",
				"1", "2", "1", "13", "50" };

		try {
			parser.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception " + e.getMessage());
		}
	}

	// @Test
	public void koOptions() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "-g", "gamma",
				"--epsilon", "12", "-d", "15", "first-arg", "-v", "1", "2",
				"blabl4", "second-args", "whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			return;
		}
		Assert.fail("No Exception generated");

	}

}
