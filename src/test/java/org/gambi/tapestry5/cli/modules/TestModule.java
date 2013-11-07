package org.gambi.tapestry5.cli.modules;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.gambi.tapestry5.cli.data.BeanA;
import org.gambi.tapestry5.cli.data.BeanFOO;
import org.gambi.tapestry5.cli.data.CLIOption;

public class TestModule {

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
	}

	public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {

		configuration.add("beanA", new BeanA());
		configuration.add("beanFOO", new BeanFOO());

	}
}
