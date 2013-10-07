package org.gambi.tapestry5.cli.services.impl;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.gambi.tapestry5.cli.services.ApplicationConfiguration;
import org.gambi.tapestry5.cli.services.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.slf4j.Logger;

public class CLIParserImpl implements CLIParser {

	private Logger logger;
	private Options configuration;

	private ApplicationConfigurationSource applicationConfigurationSource;

	private Validator validator;

	// Internal implementation via commons cli
	private CommandLineParser parser;
	private CommandLine parsedOptions;

	public CLIParserImpl(Logger logger, Collection<Option> _options,
			ApplicationConfigurationSource applicationBeanSource,
			Validator validator) {
		this.logger = logger;
		this.configuration = new Options();
		for (Option option : _options) {
			configuration.addOption(option);
		}
		this.validator = validator;
		this.applicationConfigurationSource = applicationBeanSource;
	}

	public void parse(String[] args) throws IllegalArgumentException {
		try {
			parser = new BasicParser();
			// Parse the input line
			parsedOptions = parser.parse(configuration, args);
			// Gives value to each property of the application bean object

			// NOT SURE ABOUT THE FORM HERE...
			ApplicationConfiguration application = applicationConfigurationSource
					.get(ApplicationConfiguration.class, parsedOptions);

			Set<ConstraintViolation<ApplicationConfiguration>> result = validator
					.validate(application);

		} catch (ParseException exp) {
			logger.error("Parsing failed.  Reason: " + exp.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("iter", configuration);

			throw new IllegalArgumentException("Cannot parse the input");
		}

	}
}
