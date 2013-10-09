package org.gambi.tapestry5.cli.services.impl;

import java.util.Arrays;
import java.util.Collection;
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
import org.slf4j.Logger;

public class CLIParserImpl implements CLIParser {

	private Logger logger;
	// private Messages messages;
	private Options options;

	private ApplicationConfigurationSource applicationConfigurationSource;

	private Validator validator;

	// Internal implementation via commons cli
	private CommandLineParser parser;
	private CommandLine parsedOptions;

	// Not sure this is the most beautiful way

	public CLIParserImpl(
			Logger logger,
			// Messages messages, // Apparently this cannot be injected so
			// easily
			Collection<Option> configuration,
			ApplicationConfigurationSource applicationBeanSource,
			Validator validator) {

		this.logger = logger;
		// this.messages = messages;
		this.options = new Options();
		for (Option option : configuration) {
			options.addOption(option);
		}
		this.validator = validator;
		this.applicationConfigurationSource = applicationBeanSource;
	}

	public ApplicationConfiguration parse(String[] args) throws ParseException,
			ValidationException {
		logger.debug("Parsing " + Arrays.toString(args));
		ApplicationConfiguration application = null;
		try {
			parser = new BasicParser();
			// Parse the input line
			parsedOptions = parser.parse(options, args);
			// Gives value to each property of the application bean object

			// NOT SURE ABOUT THE FORM HERE...
			application = applicationConfigurationSource.get(parsedOptions);

		} catch (ParseException exp) {
			logger.error("Parsing failed.  Reason: " + exp.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("iter", options);

			throw exp;
		}

		try {
			boolean isValid = true;
			for (Object property : application.getAllProperties()) {

				Set<ConstraintViolation<Object>> result = validator
						.validate(property);
				for (ConstraintViolation<Object> viol : result) {
					System.out.println("CLIParserImpl.validate() : "
							+ viol.getMessage());
					System.out.println("CLIParserImpl.validate() : "
							+ viol.getConstraintDescriptor());
					System.out.println("CLIParserImpl.validate() : "
							+ viol.getInvalidValue());
				}
				if (result.size() > 0) {
					isValid = false;
				}
			}

			if (!isValid) {
				throw new ValidationException("The provided input is not valid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ValidationException();
		}

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
			String symbolName;
			String symbolValue;
			for (Object _option : options.getOptions()) {
				Option option = (Option) _option;
				if (!option.hasArg()) {
					symbolName = String.format("args:%s", option.getLongOpt());
					symbolValue = "false";
					System.out
							.println("CLIParserImpl.parse(): Default setting "
									+ symbolName + " == " + symbolValue);
					System.getProperties().put(symbolName, symbolValue);
				}
			}

			symbolName = null;
			symbolValue = null;
			// TODO We cannot deal with String[] as inputs for the options !
			for (Option option : parsedOptions.getOptions()) {
				symbolName = String.format("args:%s", option.getLongOpt());
				symbolValue = "true";

				// Boolean options must be processed in a different way
				if (!option.hasArg()) {
					symbolValue = "true";
				} else {
					symbolValue = option.getValue();
				}
				System.out.println("CLIParserImpl.parse(): Exporting "
						+ symbolName + " == " + symbolValue);

				System.getProperties().put(symbolName, symbolValue);
			}

			symbolName = null;
			symbolValue = null;

			// Export also the inputs ?
			for (int i = 0; i < parsedOptions.getArgs().length; i++) {
				symbolName = String.format("args:input[%d]", i);
				symbolValue = parsedOptions.getArgs()[i];
				System.out.println("CLIParserImpl.parse(): Exporting "
						+ symbolName + " == " + symbolValue);

				System.getProperties().put(symbolName, symbolValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return application;
	}
}
