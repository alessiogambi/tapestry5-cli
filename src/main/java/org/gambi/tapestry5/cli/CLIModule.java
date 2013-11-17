package org.gambi.tapestry5.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Validator;

import org.apache.tapestry5.beanvalidator.BeanValidatorConfigurer;
import org.apache.tapestry5.beanvalidator.BeanValidatorSource;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.Builtin;
import org.apache.tapestry5.ioc.services.MasterObjectProvider;
import org.apache.tapestry5.ioc.services.PipelineBuilder;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.modules.AdditionalCoercions;
import org.gambi.tapestry5.cli.services.CLIOptionProvider;
import org.gambi.tapestry5.cli.services.CLIOptionSource;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.CLIValidatorFilter;
import org.gambi.tapestry5.cli.services.impl.CLIOptionSourceImpl;
import org.gambi.tapestry5.cli.services.impl.CLIParserImpl;
import org.gambi.tapestry5.cli.services.impl.CLIValidatorFilterImpl;
import org.gambi.tapestry5.cli.services.impl.DefaullCLIValidatorFilter;
import org.gambi.tapestry5.cli.services.impl.TapestryConstraintValidatorFactory;
import org.gambi.tapestry5.cli.services.internal.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.services.internal.BridgeCLIOptionProvider;
import org.gambi.tapestry5.cli.services.internal.CLIInputObjectProvider;
import org.gambi.tapestry5.cli.services.internal.CLIOptionObjectProvider;
import org.gambi.tapestry5.cli.services.internal.impl.ApplicationConfigurationSourceImpl;
import org.gambi.tapestry5.cli.services.internal.impl.BridgeCLIOptionProviderImpl;
import org.gambi.tapestry5.cli.utils.CLISymbolConstants;
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
	 * @category AutoBuild ApplicationConfigurationSource CLIOptionSource
	 */
	@SuppressWarnings("unchecked")
	public static void bind(final ServiceBinder binder) {
		binder.bind(ApplicationConfigurationSource.class,
				ApplicationConfigurationSourceImpl.class);

		/*
		 * Note that we MUST mark this with the Builtin annotation
		 */
		binder.bind(CLIOptionSource.class, CLIOptionSourceImpl.class)
				.withMarker(Builtin.class);

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
	 * @category Symbol UserContributions
	 */
	@Symbol(CLISymbolConstants.COMMAND_NAME) String commandName,
	/**
	 * @category Service CLIModule
	 */
	ApplicationConfigurationSource applicationConfigurationSource,
	/**
	 * @category Service SimpleValidatorModule
	 */
	Validator validator,
	/**
	 * @category Service CLIValidator
	 */
	CLIValidator cliValidator,
	/**
	 * @category Service BridgeCLIOptionProvider
	 */
	BridgeCLIOptionProvider bridgeCLIOptionProvider,
	/**
	 * @category UserContributions
	 */
	Collection<CLIOption> options) {

		return new CLIParserImpl(logger, applicationConfigurationSource,
				validator, cliValidator, commandName, bridgeCLIOptionProvider,
				options);
	}

	/**
	 * Build the second validation layer as a sequence of CLIValidators
	 * 
	 * 
	 * @param commands
	 * @param chainBuilder
	 * @return
	 * 
	 * @category Build CLIValidator
	 */
	public static CLIValidator build(
	/**
	 * @category Resource
	 */
	Logger logger,
	/**
	 * Add the CLIOption and CLIInput annotation provider. This code is taken
	 * from {@link TapestryIOCModule}.
	 * 
	 * <dl>
	 * <dt>CLIOption</dt>
	 * <dd>Supports the {@link org.gambi.tapestry5.cli.annotations.CLIOption}
	 * annotations</dd>
	 * <dt>CLIInput</dt>
	 * <dd>Supports the {@link org.gambi.tapestry5.cli.annotations.CLIInput}
	 * annotations</dd>
	 * </dl>
	 */
	PipelineBuilder pipe,
	/**
	 * @category UserContributions
	 */
	List<CLIValidator> contributions) {

		/*
		 * This is a trick to force a specific sequence of filters: we use the
		 * contributions to define an standard class that acts as the filter
		 */
		List<CLIValidatorFilter> filters = new ArrayList<CLIValidatorFilter>();
		for (CLIValidator cliValidator : contributions) {
			filters.add(new CLIValidatorFilterImpl(logger, cliValidator));
		}

		// Define the terminator filter
		filters.add(new DefaullCLIValidatorFilter());

		return pipe.build(logger, CLIValidator.class, CLIValidatorFilter.class,
				filters);
	}

	/**
	 * Contribute the CLIOption/CLIInput injection services
	 * 
	 * @param configuration
	 * 
	 * @category UserContributions MasterObjectProvider
	 */
	@Contribute(MasterObjectProvider.class)
	public static void setupObjectProviders(
			OrderedConfiguration<ObjectProvider> configuration) {

		configuration.addInstance("CLIOption", CLIOptionObjectProvider.class,
				"before:AnnotationBasedContributions");

		configuration.addInstance("CLIInput", CLIInputObjectProvider.class,
				"before:AnnotationBasedContributions");
	}

	/**
	 * Contribute the CLIOption/CLIInput service providers
	 * 
	 * @param providers
	 * 
	 * @category UserContributions CLIOptionSource
	 */
	@Contribute(CLIOptionSource.class)
	public static void setupCLIOptionProviders(
			@InjectService("BridgeCLIOptionProvider") CLIOptionProvider bridgeCLIOptionProvider,
			OrderedConfiguration<CLIOptionProvider> providers) {
		providers.add("Bridge", bridgeCLIOptionProvider, "");
	}

	/**
	 * Contribute the simple validation with tapestry5 enabled validators. this
	 * solution is taken from:
	 * http://tawus.wordpress.com/2011/05/12/tapestry-magic
	 * -12-tapestry-ioc-aware-jsr-303-custom-validators/
	 * 
	 * @param locator
	 * @param threadLocale
	 * @param configuration
	 * 
	 * @category UserContributions BeanValidatorSource
	 */
	@Contribute(BeanValidatorSource.class)
	public static void contributeBeanValidatorSource(
			final OrderedConfiguration<BeanValidatorConfigurer> configuration,
			final ObjectLocator locator) {

		configuration.add("TapestryEnabledValidationConstraints",
				new BeanValidatorConfigurer() {
					public void configure(
							javax.validation.Configuration<?> configuration) {
						configuration
								.constraintValidatorFactory(new TapestryConstraintValidatorFactory(
										locator));
					}
				});
	}

	/**
	 * Build the internal service that acts as bridge to carry around validated
	 * input
	 * 
	 * @return
	 * 
	 * @category Build BridgeCLIOptionProvider
	 */
	public BridgeCLIOptionProvider build() {
		return new BridgeCLIOptionProviderImpl();
	}

}
