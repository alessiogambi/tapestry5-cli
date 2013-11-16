package org.gambi.tapestry5.cli.modules;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.impl.SumValidator;
import org.slf4j.Logger;

public class CLIValidatorsModule {

	@Contribute(CLIValidator.class)
	public static void addDataCollectors(
	/**
	 * @category Resource
	 */
	Logger logger,
	/**
	 * @category UserContributions
	 */
	OrderedConfiguration<CLIValidator> configuration) {
		configuration.addInstance("SumValidator", SumValidator.class,
				"before:*");
	}
}
