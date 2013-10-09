package org.gambi.tapestry5.cli;

import java.net.URL;
import java.util.Collection;

import javax.validation.MessageInterpolator;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.apache.commons.cli.Option;
import org.apache.tapestry5.beanvalidator.BeanValidatorConfigurer;
import org.apache.tapestry5.beanvalidator.BeanValidatorGroupSource;
import org.apache.tapestry5.beanvalidator.BeanValidatorSource;
import org.apache.tapestry5.internal.beanvalidator.BeanValidationGroupSourceImpl;
import org.apache.tapestry5.internal.beanvalidator.BeanValidatorSourceImpl;
import org.apache.tapestry5.internal.beanvalidator.MessageInterpolatorImpl;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.gambi.tapestry5.cli.services.ApplicationConfigurationSource;
import org.gambi.tapestry5.cli.services.CLIParser;
import org.gambi.tapestry5.cli.services.impl.ApplicationConfigurationSourceImpl;
import org.gambi.tapestry5.cli.services.impl.CLIParserImpl;
import org.gambi.tapestry5.cli.services.impl.TapestryConstraintValidatorFactory;
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
 */
public class CLIModule {

	/*
	 * FIXME Ideally we should just add the BeanValidationModule as submodule to
	 * have everything working. However, by doing so we end up generating
	 * exceptions at startup because of an unknown problem. Therefore the
	 * solution here is to "recreate" what we need from the original
	 * BeanValidationModule.class: <br/>
	 * https://raw.github.com/apache/tapestry-5/master
	 * /tapestry-beanvalidator/src
	 * /main/java/org/apache/tapestry5/beanvalidator/modules
	 * /BeanValidatorModule.java
	 */

	public static void bind(final ServiceBinder binder) {
		binder.bind(BeanValidatorGroupSource.class,
				BeanValidationGroupSourceImpl.class);
		binder.bind(BeanValidatorSource.class, BeanValidatorSourceImpl.class);
		binder.bind(ApplicationConfigurationSource.class,
				ApplicationConfigurationSourceImpl.class);
	}

	/*
	 * TODO Need to Build the validator service. Taken from
	 * http://tawus.wordpress.com/2011/05
	 * /12/tapestry-magic-12-tapestry-ioc-aware-jsr-303-custom-validators/
	 */
	public static Validator buildBeanValidator(
			ValidatorFactory validatorFactory,
			PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(validatorFactory, "validator",
				Validator.class);
	}

	public static ValidatorFactory buildValidatorFactory(
			BeanValidatorSource beanValidatorSource,
			PropertyShadowBuilder propertyShadowBuilder) {
		return propertyShadowBuilder.build(beanValidatorSource,
				"validatorFactory", ValidatorFactory.class);
	}

	public static void contributeBeanValidatorGroupSource(
			final Configuration<Class> configuration) {
		configuration.add(Default.class);
	}

	@SuppressWarnings("rawtypes")
	public static void contributeTypeCoercer(
			Configuration<CoercionTuple> configuration) {
		Coercion<URL, String> urlToString = new Coercion<URL, String>() {

			public String coerce(URL arg0) {
				if (arg0 == null) {
					return null;
				} else {
					return arg0.toExternalForm();
				}
			}
		};

		configuration.add(new CoercionTuple<URL, String>(URL.class,
				String.class, urlToString));

		Coercion<String, URL> stringToURL = new Coercion<String, URL>() {

			public URL coerce(String arg0) {
				try {
					return new URL(arg0);
				} catch (Throwable e) {
					return null;
				}
			}
		};
		configuration.add(new CoercionTuple<String, URL>(String.class,
				URL.class, stringToURL));

	}

	public static void contributeBeanValidatorSource(
			final OrderedConfiguration<BeanValidatorConfigurer> configuration,
			final ThreadLocale threadLocale, final ObjectLocator locator) {

		configuration.add("LocaleAwareMessageInterpolator",
				new BeanValidatorConfigurer() {
					public void configure(
							javax.validation.Configuration<?> configuration) {
						MessageInterpolator defaultInterpolator = configuration
								.getDefaultMessageInterpolator();

						configuration
								.messageInterpolator(new MessageInterpolatorImpl(
										defaultInterpolator, threadLocale));
					}
				});

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

	/*
	 * Build the CLI Parser object to process the input data
	 */
	public CLIParser buildCLIParser(
	// Resources
			Logger logger,
			// FIXME Apparently this cannot be injected so easily !
			// Messages messages,
			// Services
			ApplicationConfigurationSource applicationConfigurationSource,
			// JSR 303 BeanValidator. TODO Shall we inject the TapestryValidator
			// instead?
			Validator validator,
			//
			// Collected the Distributed Configurations
			Collection<Option> options) {

		return new CLIParserImpl(logger, options,
				applicationConfigurationSource, validator);
	}

}
