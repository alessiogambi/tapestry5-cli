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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.services.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.RuntimeSymbolProvider;
import org.gambi.tapestry5.cli.utils.CLIDefaultOptions;
import org.slf4j.Logger;

// TODO Makes better refactoring for help/message on console
public class CLIParserImpl implements CLIParser {

	private Logger logger;
	private Options options;
	private ApplicationConfigurationSource applicationConfigurationSource;
	private Validator validator;

	private CLIValidator cliValidator;

	// TODO: ADDITIONAL CONF PARAMETER !true so
	// it
	// does not throw on unrecognized options
	private CommandLineParser parser;
	private CommandLine parsedOptions;

	private HelpFormatter formatter;
	private PrintWriter pw;

	private RuntimeSymbolProvider runtimeSymbolProvider;

	private String commandName;

	public CLIParserImpl(
			// Resources
			Logger logger,
			// Contributions
			String commandName,
			//
			Collection<Option> configuration,
			ApplicationConfigurationSource applicationBeanSource,
			Validator validator, CLIValidator cliValidator,
			//
			RuntimeSymbolProvider runtimeSymbolProvider) {

		this.logger = logger;
		this.options = new Options();
		for (Option option : configuration) {
			options.addOption(option);
		}
		this.validator = validator;
		this.cliValidator = cliValidator;
		this.applicationConfigurationSource = applicationBeanSource;

		this.runtimeSymbolProvider = runtimeSymbolProvider;
		this.commandName = commandName;
		formatter = new HelpFormatter();
		pw = new PrintWriter(System.out);
		parser = new BasicParser();
	}

	private ApplicationConfiguration parseTheInput(String[] args)
			throws ParseException {
		logger.debug("Parsing " + Arrays.toString(args));
		// Parse the input line
		parsedOptions = parser.parse(options, args);
		// Gives value to each property of the application bean object
		return applicationConfigurationSource.get(parsedOptions);
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

	private void validate(final Map<String, String> theSymbols)
			throws ValidationException {
		boolean isValid = true;
		StringBuffer errorBuffer = new StringBuffer();
		errorBuffer.append("\tViolation Messages: \n");
		try {
			List<String> result = new ArrayList<String>();

			cliValidator.validate(theSymbols, result);

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

	private Map<String, String> prepareSymbols() {
		try {
			// Boolean options must be processed in a different way:
			// We need to explicitly set the value of boolean properties, but
			// false properties are simply not present on the CLI.
			// so we default their value to false, and if they are actually
			// present, their value will be tranformed later to true.

			/*
			 * TODO Here actually we need to use the ApplicationConfiguration
			 * object... and also the Properties.Uils for the reflection For an
			 * easy use we export the options and the inputs as SystemProperties
			 * ! Ideally we should get a reference to some SymbolProvider object
			 * to contribute... Or even, we should make CLIParse a
			 * SymbolProvider...
			 */

			Map<String, String> theSymbols = new HashMap<String, String>();

			String symbolName;
			String symbolValue;
			for (Object _option : options.getOptions()) {
				Option option = (Option) _option;
				if (!option.hasArg()) {
					symbolName = String.format("args:%s", option.getLongOpt());
					symbolValue = "false";

					logger.debug("CLIParserImpl.parse(): Default setting "
							+ symbolName + " == " + symbolValue);

					theSymbols.put(symbolName, symbolValue);

				}
			}

			symbolName = null;
			symbolValue = null;
			for (Option option : parsedOptions.getOptions()) {
				symbolName = String.format("args:%s", option.getLongOpt());
				symbolValue = "true";

				// Boolean options must be processed in a different way
				if (!option.hasArg()) {
					symbolValue = "true";
				} else if (option.hasArgs()) {
					// Arrays must be treated differently:
					String[] values = new String[option.getArgs()];
					System.arraycopy(option.getValues(), 0, values, 0,
							option.getArgs());
					// Transform back to String. This is necessary because the
					// target property may not be a String[]
					// This actually can be a coercion back to String ?
					symbolValue = Arrays.toString(values);
				} else {
					symbolValue = option.getValue();
				}
				logger.debug("CLIParserImpl.parse(): Exporting " + symbolName
						+ " == " + symbolValue);

				theSymbols.put(symbolName, symbolValue);
			}

			symbolName = null;
			symbolValue = null;

			// Export also the inputs ?
			for (int i = 0; i < parsedOptions.getArgs().length; i++) {
				symbolName = String.format("args:input[%d]", i);
				symbolValue = parsedOptions.getArgs()[i];
				logger.debug("CLIParserImpl.parse(): Exporting " + symbolName
						+ " == " + symbolValue);

				theSymbols.put(symbolName, symbolValue);
			}

			return theSymbols;
		} catch (Exception e) {
			logger.error("Preparing Symbols", e);
			throw new RuntimeException(e);

		}
	}

	// FIXME Kind of bad !
	private void prindUsageAndExit(String[] args) {
		try {
			// THIS IS TAKEN FROM
			// http://stackoverflow.com/questions/14309467/how-can-i-avoid-a-parserexception-for-required-options-when-user-just-wants-to-p
			final Options helpOptions = new Options();
			helpOptions.addOption(CLIDefaultOptions.HELP_OPTION);
			CommandLine tmpLine = parser.parse(helpOptions, args, true);
			if (tmpLine.hasOption(CLIDefaultOptions.HELP_OPTION.getLongOpt())) {
				formatter.printHelp(commandName, options);
				// TODO !!
				System.exit(0);
			}
		} catch (Exception e) {
			logger.error("", e);
			formatter.printHelp(commandName, options);
			// TODO
			System.exit(1);
		}
	}

	// Maybe some useful message here as well
	private void printAndReThrow(Throwable t) throws ParseException,
			ValidationException {

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
		prindUsageAndExit(args);

		try {
			ApplicationConfiguration application = parseTheInput(args);

			validate(application);

			Map<String, String> theSymbols = prepareSymbols();

			validate(theSymbols);

			runtimeSymbolProvider.addSymbols(theSymbols);
		} catch (Exception e) {
			printAndReThrow(e);
		}
	}
}
