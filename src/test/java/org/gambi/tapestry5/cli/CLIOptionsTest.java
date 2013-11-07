package org.gambi.tapestry5.cli;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.gambi.tapestry5.cli.modules.ConflictingOptions;
import org.gambi.tapestry5.cli.modules.TestModule;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CLIOptionsTest {

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
	public void conflictingOptions() {
		// Add the "Erroneous" Module
		builder.add(ConflictingOptions.class);
		try {
			// Build the registry: note this will not cause an exception because
			// we need to instantiate the parser first
			registry = builder.build();
			registry.performRegistryStartup();

			CLIParser parser = registry.getService(CLIParser.class);
			String[] args = new String[] { "-a", "-1", "--beta", "7", "-g", "",
					"first-arg", "second-args", "whaterver" };

			parser.parse(args);
		} catch (Exception e) {
			return;
		}
		Assert.fail("Exception not raised !");
	}
}
