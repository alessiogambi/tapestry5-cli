package org.gambi.tapestry5.cli.services;

import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;

public interface ApplicationConfigurationSource {

	public ApplicationConfiguration afterParsing(CommandLine parsedOptions);

	public Collection<Option> parsingOptions();

}
