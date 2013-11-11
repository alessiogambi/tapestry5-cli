package org.gambi.tapestry5.cli;

import java.util.Collection;

import javax.validation.Validator;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.modules.AdditionalCoercions;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.gambi.tapestry5.cli.services.impl.CLIParserImpl;
import org.gambi.tapestry5.cli.services.internals.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.services.internals.impl.ApplicationConfigurationSourceImpl;
import org.slf4j.Logger;

/*
 * References : -
 * http://blog.tapestry5.de/index.php/2010/01/04/tapestry-and-jsr-
 * 303-bean-validation-api/ -
 * http://tawus.wordpress.com/2011/05/12/tapestry-magic
 * -12-tapestry-ioc-aware-jsr-303-custom-validators/
 * 
 * 
 */

/**
 * A T5 enable module for CLI argument parsing.
 * 
 * The module provide a CLI-Parser and CLI-validation framework to easy the
 * definition and operation of T5 enable applications that rely on inputs, and
 * options to be specified via the command line.
 * 
 * The input arguments are parsed, then validated, and eventually "exported" as
 * symbols to be used inside the application.
 * 
 * Input arguments are exported as symbols with the following convention for
 * their names: <br/>
 * <b>args:inputs:<<n>></b><br/>
 * where <<n>> stands for the position, of the input (starting form 0)
 * 
 * Command line options are exported as symbols with the following convention
 * for their name: <br/>
 * <b>args:<<long-name-option>></b><br/>
 * where <<long-name-option>> is the parameter specified in the configuration.
 * We adopt this solution because symbol names are case insensitive.
 * 
 * @author alessiogambi
 * 
 * @category Module
 */

@SubModule({ AdditionalCoercions.class })
public class CLIModule {

	/**
	 * Auto build services
	 * 
	 * @category AutoBuild ApplicationConfigurationSource
	 */
	public static void bind(final ServiceBinder binder) {
		binder.bind(ApplicationConfigurationSource.class,
				ApplicationConfigurationSourceImpl.class);
	}

	/**
	 * Build the CLI Parser object to process the input data.
	 * 
	 * @param logger
	 * @param applicationConfigurationSource
	 * @param validator
	 * @param options
	 * @return CLIParser
	 * 
	 * @category Build CLIParser
	 */
	public CLIParser buildCLIParser(
	/**
	 * @category Resource
	 */
	Logger logger,
	/**
	 * @category Service CLIModule
	 */
	ApplicationConfigurationSource applicationConfigurationSource,
	/**
	 * @category Service SimpleValidatorModule
	 */
	Validator validator,
	/**
	 * @category UserContributions
	 */
	Collection<CLIOption> options) {

		return new CLIParserImpl(logger, applicationConfigurationSource,
				validator, options);
	}
}
