package org.gambi.tapestry5.cli.validators;

import java.util.ArrayList;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.gambi.tapestry5.cli.CLIModule;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.modules.ComplexConstraintModule;
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

	@Test
	public void okOptions() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "-d", "10", "--beta",
				"cicc", "-g", "gamma", "--epsilon", "12", "-d", "15", "-v",
				"1", "2", "1", "13", "50" };

		try {
			parser.parse(args);
		} catch (Throwable e) {
			e.printStackTrace();
			Assert.fail("Exception " + e.getMessage());
		}
	}

	@Test
	public void koOptions() {
		CLIParser parser = registry.getService(CLIParser.class);
		String[] args = new String[] { "-a", "100", "--beta", "cicc", "-g",
				"gamma", "--epsilon", "12", "-d", "15", "first-arg", "-v",
				"a-very-longhish-parameters-that-is-not goood", "short",
				"blabl4", "second-args", "whaterver" };
		try {
			parser.parse(args);
		} catch (Exception e) {
			return;
		}
		Assert.fail("No Exception generated");

	}

}
