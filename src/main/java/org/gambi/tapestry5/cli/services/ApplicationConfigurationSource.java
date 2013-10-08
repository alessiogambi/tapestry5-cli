package org.gambi.tapestry5.cli.services;

import org.apache.commons.cli.CommandLine;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;

public interface ApplicationConfigurationSource {

	public ApplicationConfiguration get(CommandLine parsedOptions);

}
