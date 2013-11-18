package org.gambi.tapestry5.cli.services;

import java.util.Arrays;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.gambi.tapestry5.cli.CLIModule;
import org.gambi.tapestry5.cli.modules.ConflictingOptions;
import org.gambi.tapestry5.cli.modules.TestModule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CLIOptionSourceTest {

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
	}

	@After
	public void shutdown() {
		if (registry != null) {
			registry.shutdown();
		}
	}

	@Test
	public void options() {
		try {
			// Build the registry: note this will not cause an exception because
			// we need to instantiate the parser first
			registry = builder.build();
			registry.performRegistryStartup();

			CLIParser parser = registry.getService(CLIParser.class);
			String[] args = new String[] { "-u", "file:///tmp", "-a", "14",
					"--beta", "-g", "", "-v", "1", "2", "3", "first-arg",
					"second-args", "whaterver", "17" };

			parser.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception raised !");
		}

		CLIOptionSource optionSource = registry
				.getService(CLIOptionSource.class);

		Assert.assertEquals("14", optionSource.valueForOption("a"));
		Assert.assertEquals("true", optionSource.valueForOption("beta"));
		Assert.assertEquals("", optionSource.valueForOption("gamma"));
		Assert.assertNull(optionSource.valueForOption("v"));

		Assert.assertNull(optionSource.valuesForOption("a"));
		Assert.assertNull(optionSource.valuesForOption("beta"));
		Assert.assertNull(optionSource.valuesForOption("gamma"));
		Assert.assertNotNull(optionSource.valuesForOption("v"));
	}

	@Test
	public void vectorOptions() {
		try {
			// Build the registry: note this will not cause an exception because
			// we need to instantiate the parser first
			registry = builder.build();
			registry.performRegistryStartup();

			CLIParser parser = registry.getService(CLIParser.class);
			String[] args = new String[] { "-u", "file:///tmp", "-a", "14",
					"--beta", "-g", "", "-v", "1", "2", "3", "first-arg",
					"second-args", "whaterver", "17" };

			parser.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception raised !");
		}

		CLIOptionSource optionSource = registry
				.getService(CLIOptionSource.class);

		System.out.println("CLIOptionSourceTest.vectorOptions()"
				+ Arrays.toString(optionSource.valuesForOption("v")));

		Assert.assertTrue(Arrays.equals(new String[] { "1", "2", "3",
				"first-arg", "second-args" }, optionSource.valuesForOption("v")));

	}
}
