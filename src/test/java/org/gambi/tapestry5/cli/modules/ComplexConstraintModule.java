package org.gambi.tapestry5.cli.modules;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.data.NestedBean;
import org.gambi.tapestry5.cli.utils.CLISymbolConstants;

public class ComplexConstraintModule {

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> defaults) {
		defaults.add(CLISymbolConstants.COMMAND_NAME, "complexTest");
	}

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

		configuration.add(new CLIOption("d", "delta", 1, false, "A delta"));

		configuration.add(new CLIOption("v", "vector", 3, false,
				"Vector must have 3 elements !"));

		configuration.add(new CLIOption("e", "epsilon", 1, false, "epsilon"));

		configuration.add(new CLIOption("g", "gamma", 1, true,
				"This is anothre definition of gamma"));

		configuration.add(new CLIOption("su", "string-url", 1, false,
				"string-url"));

		configuration.add(new CLIOption("u", "an-url", 1, false,
				"This is another URL"));

		configuration.add(new CLIOption("o", "orsu", 1, false,
				"This is another URL"));

		configuration.add(new CLIOption("j", "jonny", 0, false,
				"This is another URL"));

		configuration.add(new CLIOption("t", "tommy", 1, false,
				"This is another URL"));

	}

	public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {

		configuration.addInstance("NestedBean", NestedBean.class);

	}
}
