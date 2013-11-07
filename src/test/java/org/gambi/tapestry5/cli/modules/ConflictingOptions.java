package org.gambi.tapestry5.cli.modules;

import org.apache.tapestry5.ioc.Configuration;
import org.gambi.tapestry5.cli.data.CLIOption;

public class ConflictingOptions {

	/**
	 * Define some sample contributions. This will conflict with alfa option
	 * 
	 * @param configuration
	 */
	public void contributeCLIParser(Configuration<CLIOption> configuration) {
		configuration.add(new CLIOption("a", "alaa", 2, true,
				"alfa-description"));
	}
}
