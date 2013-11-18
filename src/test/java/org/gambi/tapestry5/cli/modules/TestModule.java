package org.gambi.tapestry5.cli.modules;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.gambi.tapestry5.cli.data.BeanA;
import org.gambi.tapestry5.cli.data.BeanFOO;
import org.gambi.tapestry5.cli.data.BeanWithURL;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.utils.CLISymbolConstants;

public class TestModule {

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> symbols) {
		symbols.add(CLISymbolConstants.COMMAND_NAME, "test");
	}

	/**
	 * Define some sample contributions
	 * 
	 * @param configuration
	 */
	public void contributeCLIParser(Configuration<CLIOption> configuration) {
		configuration.add(new CLIOption("a", "alfa", 1, true,
				"alfa-description"));
		configuration.add(new CLIOption("b", "beta", 0, true,
				"beta-description"));
		configuration
				.add(new CLIOption("g", "gamma", 1, false, "This is gamma"));

		configuration.add(new CLIOption("v", "a-vector-options", 5, false,
				"This is vector"));

		configuration.add(new CLIOption("u", "the-URL", 1, true, "Mandatory"));

		CLIOption delta = new CLIOption("d", "delta", 1, false, "");
		delta.setDefaultValue("3");
		configuration.add(delta);

		CLIOption epsilon = new CLIOption("e", "epsilon", 1, false, "");
		epsilon.setDefaultValue("12");
		configuration.add(epsilon);
	}

	public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {

		configuration.addInstance("beanURL", BeanWithURL.class);

		configuration.add("beanA", new BeanA());
		configuration.add("beanFOO", new BeanFOO());

	}
}
