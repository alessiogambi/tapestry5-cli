package org.gambi.tapestry5.cli.services.impl;

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
import org.slf4j.Logger;

public class CLIParserImpl implements CLIParser {

	private Logger logger;
	private Options options;
	private ApplicationConfigurationSource applicationConfigurationSource;
	private Validator validator;

	private CLIValidator cliValidator;

	private CommandLineParser parser;
	private CommandLine parsedOptions;

	private Map<String, String> symbols;

	public CLIParserImpl(Logger logger, Collection<Option> configuration,
			ApplicationConfigurationSource applicationBeanSource,
			Validator validator, CLIValidator cliValidator) {

		this.logger = logger;
		// this.messages = messages;
		this.options = new Options();
		for (Option option : configuration) {
			options.addOption(option);
		}
		this.validator = validator;
		this.cliValidator = cliValidator;
		this.applicationConfigurationSource = applicationBeanSource;
	}

	private ApplicationConfiguration parseTheInput(String[] args)
			throws ParseException {
		try {
			logger.debug("Parsing " + Arrays.toString(args));
			parser = new BasicParser();
			// Parse the input line
			parsedOptions = parser.parse(options, args);
			// Gives value to each property of the application bean object
			return applicationConfigurationSource.get(parsedOptions);

		} catch (ParseException exp) {
			logger.error("Parsing failed.  Reason: " + exp.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("iter", options);

			throw exp;
		}
	}

	private void validate(ApplicationConfiguration applicationConfiguration)
			throws ValidationException {
		boolean isValid = true;
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
				}
				if (result.size() > 0) {
					isValid = false;
				}
			}
		} catch (Exception e) {
			throw new ValidationException("Error during validation", e);
		}

		if (!isValid) {
			throw new ValidationException(
					"The provided command line input is not valid");
		}
	}

	private void validate(Map<String, String> theSymbols)
			throws ValidationException {
		boolean isValid = true;

		try {
			List<String> result = cliValidator.validate(theSymbols);
			for (String violation : result) {
				logger.debug("CLIParserImpl.validate() : " + violation);
			}
			if (result.size() > 0) {
				isValid = false;
			}
		} catch (Throwable e) {
			throw new ValidationException("Error during validation", e);
		}

		if (!isValid) {
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

	public void parse(String[] args) throws ParseException, ValidationException {

		ApplicationConfiguration application = parseTheInput(args);

		validate(application);

		Map<String, String> theSymbols = prepareSymbols();

		validate(theSymbols);

		symbols = theSymbols;
	}

	public Map<String, String> getSymbols() {
		return symbols;
	}
}
