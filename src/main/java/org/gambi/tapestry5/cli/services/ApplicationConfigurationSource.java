package org.gambi.tapestry5.cli.services;

import org.apache.commons.cli.CommandLine;

public interface ApplicationConfigurationSource {

	public ApplicationConfiguration get(Class<ApplicationConfiguration> clazz,
			CommandLine parsedOptions);

}
