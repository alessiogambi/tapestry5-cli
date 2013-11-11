package org.gambi.tapestry5.cli.modules;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.data.NestedBean;

public class ComplexConstraintModule {

	/**
	 * Note that those options are used with the one specified inside
	 * {@link TestModule}
	 * 
	 * @param configuration
	 */
	public void contributeCLIParser(Configuration<CLIOption> configuration) {

		configuration.add(new CLIOption("a", "alfa", 1, true,
				"alfa-description"));
		configuration.add(new CLIOption("b", "beta", 1, true,
				"beta-description"));
		configuration
				.add(new CLIOption("g", "gamma", 1, false, "This is gamma"));

		configuration.add(new CLIOption("d", "delta", 1, true, "A delta"));
		configuration.add(new CLIOption("v", "vector", 3, true,
				"Vector must have 3 elements !"));

		configuration.add(new CLIOption("e", "epsilon", 1, true, "epsilon"));

		configuration.add(new CLIOption("g", "gamma", 1, true,
				"This is anothre definition of gamma"));

	}

	public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {

		configuration.addInstance("NestedBean", NestedBean.class);

	}
}
