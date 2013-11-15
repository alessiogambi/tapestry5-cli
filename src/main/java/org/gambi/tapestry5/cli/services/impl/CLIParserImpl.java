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
import org.apache.tapestry5.internal.antlr.PropertyExpressionParser.constant_return;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.internals.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.utils.CLIDefaultOptions;
import org.slf4j.Logger;

public class CLIParserImpl implements CLIParser {

	private Logger logger;
	private ApplicationConfigurationSource applicationConfigurationSource;
	private Validator validator;
	private CLIValidator cliValidator;

	// User Contributions
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
			Collection<CLIOption> _options) {

		this.logger = logger;
		this.validator = validator;
		this.cliValidator = cliValidator;
		this.applicationConfigurationSource = applicationBeanSource;

		this.commandName = commandName;

		formatter = new HelpFormatter();
		pw = new PrintWriter(System.out);

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
	private void prindUsageAndExit(String[] args) {
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
				System.exit(0);
			}
		} catch (Exception e) {
			logger.error("", e);
			formatter.printHelp(commandName, options);
			// TODO
			System.exit(1);
		}
	}

	/**
	 * This check for duplicate entries and merge the ones that can be merged
	 */
	private void validateAndMerge(Collection<CLIOption> _options) {
		ArrayList<CLIOption> cliOptions = new ArrayList<CLIOption>();

		for (CLIOption cliOption : _options) {

			if (!cliOptions.contains(cliOption)) {
				logger.debug("Adding " + cliOption);
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
	}

	/*
	 * Initialize the objects to parse the command line
	 * 
	 * @return
	 */
	private Options setupParsing() {
		Options configuration = new Options();
		for (CLIOption cliOption : cliOptions) {

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

	private ApplicationConfiguration parseTheInput(String[] args)
			throws ParseException {
		Options options = setupParsing();
		logger.debug("Parsing " + Arrays.toString(args));
		// Parse the input line
		parsedOptions = parser.parse(options, args);
		// Update the local CLIOptions

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

			// Initialize with defaults if not null
			Map<String, String> theSymbols = new HashMap<String, String>();

			// FLAGs. FLAG Option: there is no default, if it is there true,
			// otherwise false. Default value is not considered
			for (CLIOption cliOption : cliOptions) {

				if (cliOption.getnArgs() <= 0) {
					logger.debug("CLIParserImpl.parse(): Default setting "
							+ cliOption.getLongOpt() + " == FALSE ");
					theSymbols.put(cliOption.getLongOpt(), "false");
				} else {
					if (cliOption.getnArgs() == 1 && cliOption.getDefaultValue() != null){ 
						theSymbols.put(cliOption.getLongOpt(), cliOption.getDefaultValue());	
					}
					
					if (cliOption.getnArgs() > 1 && cliOption.getDefaultValues() != null){ 
						theSymbols.put(cliOption.getLongOpt(), cliOption.getDefaultValue());	
					}
				}
			}

			// TODO This must be done for the actually parsed options !
			for (CLIOption cliOption : cliOptions) {
				
				// Boolean options must be processed in a different way
				String symbolValue;
				if (cliOption.getnArgs() == 0) {
					symbolValue = "true";
				} else {
					
					if( cliOption.getValue() == null ){
						continue;
					} else if(cliOption.getValues() == null ){
						continue;
					}
					
					if (cliOption.getnArgs() == 1) {
				}
					symbolValue = cliOption.getValue();
				} else {
					// Arrays must be treated differently:
					String[] values = cliOption.getValues();
					// Transform back to String. This is necessary because the
					// target property may not be a String[]
					// This actually can be a coercion back to String ?
					symbolValue = Arrays.toString(values);
				}
				logger.debug("CLIParserImpl.parse(): Exporting "
						+ cliOption.getLongOpt() + " == " + symbolValue);

				theSymbols.put(cliOption.getLongOpt(), symbolValue);
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

	public void parseMe(String[] args) throws ParseException, ValidationException {

		
//		ApplicationConfiguration application = null;
//		
//			parser = new BasicParser();
//			// Parse the input line
//			parsedOptions = parser.parse(configuration, args);
//			// Gives value to each property of the application bean object
//
//		} catch (ParseException exp) {
//			logger.error("Parsing failed.  Reason: " + exp.getMessage());
//
//			HelpFormatter formatter = new HelpFormatter();
//			formatter.printHelp(this.commandName, configuration);
//
//			throw exp;
//		}
//		try {
//			// NOT SURE ABOUT THE FORM HERE...
//			application = applicationConfigurationSource.get(parsedOptions);
//		} catch (Throwable e) {
//			throw new ParseException(
//					"Error while creating the ApplicationConfiguration"
//							+ e.getMessage());
//		}

		logger.debug("\t\tJSR 303 Bean Validation");
		boolean isValid = true;
		try {

			for (Object property : application.getAllProperties()) {

				Set<ConstraintViolation<Object>> result = validator
						.validate(property);
				System.out.println("CLIParserImpl.validate() property : "
						+ property.toString());

				for (ConstraintViolation<Object> viol : result) {

					System.out.println("CLIParserImpl.validate() : "
							+ viol.getInvalidValue());
					System.out.println("CLIParserImpl.validate() : "
							+ viol.getPropertyPath());
					System.out.println("CLIParserImpl.validate() : "
							+ viol.getConstraintDescriptor().getAnnotation());
					System.out.println("CLIParserImpl.validate() : "
							+ viol.getMessage());
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

		logger.debug("\t\tCLIValidation");
		isValid = true;

		try {

			cliValidator.validate(inputs, accumulator)
			if (!isValid) {
				throw new ValidationException("The provided input is not valid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ValidationException();
		}

		try {

			// TODO We cannot deal with String[] as inputs for the options !
			for (Option option : parsedOptions.getOptions()) {
				String symbolName = String.format("args:%s",
						option.getLongOpt());
				String symbolValue = option.getValue();
				System.out.println("CLIParserImpl.parse(): Exporting "
						+ symbolName + " == " + symbolValue);

				if (symbolValue != null) {
					System.getProperties().put(symbolName, symbolValue);
				}
			}
		} catch (Throwable e) {
			throw new ValidationException("Generic Error : " + e);
		}
	}
}
