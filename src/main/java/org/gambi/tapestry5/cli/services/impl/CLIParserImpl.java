package org.gambi.tapestry5.cli.services.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.internal.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.services.internal.BridgeCLIOptionProvider;
import org.gambi.tapestry5.cli.utils.CLIDefaultOptions;
import org.slf4j.Logger;

public class CLIParserImpl implements CLIParser {

	private Logger logger;
	private ApplicationConfigurationSource applicationConfigurationSource;
	private Validator validator;
	private CLIValidator cliValidator;

	private BridgeCLIOptionProvider bridgeCLIOptionProvider;

	// User Contributions. This is also used to store the parsed inputs if any
	private Collection<CLIOption> cliOptions;
	// User contribution... must be this the args[0] element ?
	private String commandName;

	// Internal implementation
	private CommandLineParser parser;
	private CommandLine parsedOptions;
	private HelpFormatter formatter;
	// Configure this via Symbol ?
	private PrintWriter pw;

	public CLIParserImpl(Logger logger,
			ApplicationConfigurationSource applicationBeanSource,
			Validator validator, CLIValidator cliValidator, String commandName,
			BridgeCLIOptionProvider bridgeCLIOptionProvider,
			Collection<CLIOption> _options) {

		this.logger = logger;
		this.validator = validator;
		this.cliValidator = cliValidator;
		this.applicationConfigurationSource = applicationBeanSource;

		this.commandName = commandName;

		this.bridgeCLIOptionProvider = bridgeCLIOptionProvider;

		this.formatter = new HelpFormatter();
		this.pw = new PrintWriter(System.out);
		this.parser = new BasicParser();

		validateAndMerge(_options);
	}

	/**
	 * Print the help message and call System.exit
	 */
	/*
	 * Note: this should be implemented by using a special command called help:
	 * each commadn should be executed from scratch: add to wish list!
	 */
	// FIXME Kind of bad !
	private void prindUsage(String[] args) {
		final Options options = setupParsing();
		try {
			// THIS IS TAKEN FROM
			// http://stackoverflow.com/questions/14309467/how-can-i-avoid-a-parserexception-for-required-options-when-user-just-wants-to-p
			final Options helpOptions = new Options();

			helpOptions.addOption(CLIDefaultOptions.HELP_OPTION);
			CommandLine tmpLine = parser.parse(helpOptions, args, true);
			if (tmpLine.hasOption(CLIDefaultOptions.HELP_OPTION.getLongOpt())) {
				formatter.printHelp(commandName, options);
				// TODO !!
				// System.exit(0);
			}
		} catch (Exception e) {
			logger.error("", e);
			formatter.printHelp(commandName, options);
			// TODO
			// System.exit(1);
		}
	}

	/**
	 * This check for duplicate entries and merge the ones that can be merged
	 */
	private void validateAndMerge(Collection<CLIOption> _options) {
		ArrayList<CLIOption> cliOptions = new ArrayList<CLIOption>();

		for (CLIOption cliOption : _options) {

			if (!cliOptions.contains(cliOption)) {
				logger.info("Adding " + cliOption);
				cliOptions.add(cliOption);
			} else {
				logger.info("\t Merging options "
						+ cliOptions.get(cliOptions.indexOf(cliOption))
						+ " with " + cliOption);

				cliOptions.get(cliOptions.indexOf(cliOption)).merge(cliOption);
			}
		}

		// Validate (ideally here there are few options to check);
		for (CLIOption cliOption1 : cliOptions) {
			for (CLIOption cliOption2 : cliOptions) {
				if (cliOption1.conflicts(cliOption2)) {

					throw new IllegalArgumentException(
							"Found conflicting CLIOptions: Option "
									+ cliOption1 + " conflicts with "
									+ cliOption2
									+ ". Please revise your contributions !");
				}
			}
		}

		// Now update the variable
		this.cliOptions = cliOptions;

		logger.info("Validate and merge : " + this.cliOptions);
	}

	/*
	 * Initialize the objects to parse the command line
	 * 
	 * @return
	 */
	private Options setupParsing() {
		Options configuration = new Options();
		for (CLIOption cliOption : cliOptions) {

			@SuppressWarnings("static-access")
			Option option = OptionBuilder.withLongOpt(cliOption.getLongOpt())
					.hasArgs(cliOption.getnArgs())
					.isRequired(cliOption.isRequired())
					.withDescription(cliOption.getDescription())
					.create(cliOption.getShortOpt());

			logger.debug("Created option " + option);
			configuration.addOption(option);
		}
		return configuration;
	}

	// Maybe some useful message here as well
	private void printAndReThrow(Throwable t) throws ParseException,
			ValidationException {

		Options options = setupParsing();
		if (t instanceof ParseException) {
			// TODO Add some more info on error
			formatter.printHelp(commandName, options);
			throw (ParseException) t;
		} else if (t instanceof ValidationException) {
			// TODO Add some more info on error
			formatter.printHelp(commandName, options);
			throw (ValidationException) t;
		} else {
			// TODO Add some more info on error
			formatter.printHelp(commandName, options);
			// By default we wrap everything inside a ValidationException
			throw new ValidationException(t);
		}
	}

	// Maybe this should be a pipeline thing ?
	public void parse(String[] args) throws ParseException, ValidationException {

		// If --help is present then print usage and exit(0)
		// prindUsage(args);

		try {
			logger.debug("Parsing Input");
			ApplicationConfiguration application = parseTheInput(args);
			logger.debug("Basic Validation of Input " + application);
			validate(application);

			// This is actually only for better readability
			logger.debug("Prepare CLIInput");
			List<String> inputs = prepareCLIInputs();
			logger.debug("\t" + inputs);

			logger.debug("Prepare CLIOptions");
			Map<CLIOption, String> options = prepareCLIOptions();
			logger.debug("\t" + options);

			logger.debug("CLI Validation");
			validate(options, inputs);

			// Setup the CLIOptionSource
			bridgeCLIOptionProvider.add(options);
			bridgeCLIOptionProvider.add(inputs);

		} catch (Exception e) {
			printAndReThrow(e);
		}
	}

	private Option findOptionByName(String name) {
		for (Option option : parsedOptions.getOptions()) {
			if (option.getOpt().equals(name)
					|| option.getLongOpt().equals(name)) {
				return option;
			}
		}
		logger.warn("Cannot find option " + name);
		return null;
	}

	private ApplicationConfiguration parseTheInput(String[] args)
			throws ParseException {

		Options options = setupParsing();
		logger.debug("Parsing " + Arrays.toString(args));
		// Parse the input line
		parsedOptions = parser.parse(options, args);

		List<String> parsedInputs = parsedOptions.getArgList();

		for (CLIOption cliOption : cliOptions) {
			logger.debug("Processing " + cliOption.toString());

			Option theOption = findOptionByName(cliOption.getShortOpt());
			if (theOption != null) {
				if (!theOption.hasArg()) {
					// FLAG: this may override the previous value
					cliOption.setValue("true");
				} else {
					if (!theOption.hasArgs()) {
						// 1 arg
						cliOption.setValue(theOption.getValue());
					} else {
						// 2+ args
						cliOption.setValues(theOption.getValues());
					}
				}

				logger.debug("Done Option" + cliOption.toString());
			} else {
				// Set default values if any
				logger.debug(String.format("CLIOption %s is not set.",
						cliOption.toString()));
				if (cliOption.getnArgs() == 0) {
					// FLAG Options have default value to false. They become
					// true ONLY if they were specified on the command line
					logger.debug("CLIParserImpl.parse(): Default setting "
							+ cliOption.getLongOpt() + " == false ");
					cliOption.setDefaultValue("false");
					cliOption.setValue("false");
				} else {
					if (cliOption.getnArgs() == 1) {
						logger.debug(String.format(
								"Force default value %s for Option %s",
								cliOption.getDefaultValue(),
								cliOption.toString()));

						cliOption.setValue(cliOption.getDefaultValue());

						logger.debug("The value now is " + cliOption.getValue());
					} else if (cliOption.getnArgs() > 1) {

						logger.debug("Force default values %s for Option %s",
								Arrays.toString(cliOption.getDefaultValues()),
								cliOption.toString());

						cliOption.setValues(cliOption.getDefaultValues());

						logger.debug("The values now are "
								+ Arrays.toString(cliOption.getValues()));
					}
				}
			}

		}

		return applicationConfigurationSource.get(cliOptions, parsedInputs);
	}

	private void validate(ApplicationConfiguration applicationConfiguration)
			throws ValidationException {
		boolean isValid = true;

		StringBuffer errorBuffer = new StringBuffer();
		errorBuffer.append("\t Violation Messages: \n");
		try {
			for (Object property : applicationConfiguration.getAllProperties()) {

				Set<ConstraintViolation<Object>> result = validator
						.validate(property);

				for (ConstraintViolation<Object> viol : result) {
					logger.debug("CLIParserImpl.validate() : "
							+ viol.getMessage());
					logger.debug("CLIParserImpl.validate() : "
							+ viol.getConstraintDescriptor());
					logger.debug("CLIParserImpl.validate() : "
							+ viol.getInvalidValue());

					errorBuffer.append("-");
					errorBuffer.append(viol.getMessage());
					errorBuffer.append("\n");
				}

				if (result.size() > 0) {
					isValid = false;
				}
			}

			errorBuffer.append("\n");
			errorBuffer.append("\n");

		} catch (Exception e) {

			formatter.printWrapped(pw, formatter.getWidth(),
					errorBuffer.toString());
			pw.flush();
			throw new ValidationException("Error during validation", e);
		}

		if (!isValid) {
			formatter.printWrapped(pw, formatter.getWidth(),
					errorBuffer.toString());
			pw.flush();
			throw new ValidationException(
					"The provided command line input is not valid");
		}
	}

	private void validate(Map<CLIOption, String> options, List<String> inputs)
			throws ValidationException {
		boolean isValid = true;
		StringBuffer errorBuffer = new StringBuffer();
		errorBuffer.append("\tViolation Messages: \n");
		try {
			List<String> result = new ArrayList<String>();

			cliValidator.validate(options, inputs, result);

			for (String violation : result) {
				logger.warn("CLIParserImpl.validate() Input Violation : "
						+ violation);
				errorBuffer.append("-");
				errorBuffer.append(violation);
				errorBuffer.append("\n");
			}
			if (result.size() > 0) {
				isValid = false;
			}
			errorBuffer.append("\n");
			errorBuffer.append("\n");
		} catch (Throwable e) {
			formatter.printWrapped(pw, formatter.getWidth(),
					errorBuffer.toString());
			pw.flush();
			throw new ValidationException("Error during validation", e);
		}

		if (!isValid) {
			formatter.printWrapped(pw, formatter.getWidth(),
					errorBuffer.toString());
			pw.flush();
			throw new ValidationException(
					"The provided command line input is not valid");
		}
	}

	/**
	 * User the parsed command line to build the structure that contains the
	 * ORDERED user inputs
	 */
	@SuppressWarnings("unchecked")
	private List<String> prepareCLIInputs() {
		try {
			List<String> result = new ArrayList<String>();
			result.addAll(parsedOptions.getArgList());
			return result;
		} catch (Throwable e) {
			logger.error("Error while preparing CLI Inputs", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Use the parsed options and the CLIOptions definitions to create a map of
	 * values that corresponds to the final data to be validated
	 * 
	 * @return
	 */
	private Map<CLIOption, String> prepareCLIOptions() {
		Map<CLIOption, String> result = new HashMap<CLIOption, String>();

		try {
			for (CLIOption cliOption : cliOptions) {
				if (cliOption.getValue() != null) {
					result.put(cliOption, cliOption.getValue());
				} else if (cliOption.getValues() != null) {
					result.put(cliOption,
							Arrays.toString(cliOption.getValues()));
				}
			}

			return result;
		} catch (Exception e) {
			logger.error("Error while preparing CLIOptions", e);
			throw new RuntimeException(e);

		}
	}

}
