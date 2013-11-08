package org.gambi.tapestry5.cli.validators;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.gambi.tapestry5.cli.CLIModule;
import org.gambi.tapestry5.cli.modules.TestModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BasicJSR303ValidatorTest {

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
	}
}
