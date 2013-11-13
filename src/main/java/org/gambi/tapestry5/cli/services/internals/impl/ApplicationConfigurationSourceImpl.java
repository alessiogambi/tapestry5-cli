package org.gambi.tapestry5.cli.services.internals.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.util.UnknownValueException;
import org.gambi.tapestry5.cli.annotations.ParsingOption;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.data.CLITuple;
import org.gambi.tapestry5.cli.services.internals.ApplicationConfigurationSource;
import org.slf4j.Logger;

public class ApplicationConfigurationSourceImpl implements
		ApplicationConfigurationSource {

	private Logger logger;

	// Since TranslatoSource is not there we need to used typeCoercer
	private TypeCoercer typeCoercer;

	// The properties to be validate... all of them have the validation
	// annotations
	private Map<String, Object> contributions;

	// This is extracted from the contributions!
	// Note that we allowed
	private Set<CLITuple> propertiesOptionsMapping;

	// TODO Use BeanUtils class to improve the code

	public ApplicationConfigurationSourceImpl(Logger logger,
			TypeCoercer typeCoercer, Map<String, Object> contributions) {
		this.logger = logger;
		this.typeCoercer = typeCoercer;
		this.contributions = contributions;
		// Prepare the datastructure
		propertiesOptionsMapping = new HashSet<CLITuple>();
		propertiesOptionsMapping.addAll(extractMappings(contributions));
	}

	// TODO We assume that the bean convention is actually respected
	private Collection<CLITuple> extractMappingsFromBean(Class clazz,
			String prefix) {
		Set<CLITuple> tuples = new HashSet<CLITuple>();
		System.out
				.println("ApplicationConfigurationSourceImpl.extractMappingsFromBean() from "
						+ clazz + " with prefix [" + prefix + "]");
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(ParsingOption.class)) {
				StringBuffer propertyName = new StringBuffer();
				propertyName.append(prefix);
				propertyName.append(f.getName());
				ParsingOption annotation = f.getAnnotation(ParsingOption.class);

				OptionBuilder builder = OptionBuilder
						.withLongOpt(annotation.longOpt())
						.withDescription(annotation.description())
						.isRequired(annotation.isRequired());
				if (annotation.hasArg() && annotation.nArgs() == 1) {
					builder.hasArg();
				} else if (annotation.hasArg() && annotation.nArgs() > 1) {
					builder.hasArgs(annotation.nArgs());
				}
				Option option = builder.create(annotation.opt());

				tuples.add(new CLITuple(propertyName.toString(), option));
				System.out
						.println("\t\tApplicationConfigurationSourceImpl.extractMappingsFromBean() : adding "
								+ propertyName.toString() + " --" + option);
			} else {
				// Repeat the search one level down only if we do not have a
				// type coercer for the bean !
				// FIXME LiveLocks are possible here, we need to check if the
				// instance was already considered !

				try {
					// If the object can be build from a string then it must be
					// an user define property, so we do not look inside
					typeCoercer.getCoercion(String.class, f.getType());
					logger.debug(f.getName() + " is not annotate by the user !");
					continue;
				} catch (UnknownValueException uve) {
					logger.debug("Merging : " + f.getName());
					// Here we need to go one step inside and add the prefix
					tuples.addAll(extractMappingsFromBean(f.getType(),
							f.getName() + "."));

				}
			}
		}

		return tuples;
	}

	private Set<CLITuple> extractMappings(Map<String, Object> contributions) {
		// Not sure this is the right way.. in case, just use plain reflection
		Set<CLITuple> tuples = new HashSet<CLITuple>();

		for (Object contribution : contributions.values()) {

			tuples.addAll(extractMappingsFromBean(contribution.getClass(), ""));
		}
		return tuples;
	}

	// TODO This should be improved !
	private String escapePropertyName(String propertyName) {
		String _pName = propertyName.trim().replaceAll("--", "-");
		StringBuilder sb = new StringBuilder();
		boolean capitalizeNext = false;
		for (char c : _pName.toCharArray()) {
			if (c == '-') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					sb.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	private Option findOption(CommandLine parsedOptions, String propertyName) {
		for (Option option : parsedOptions.getOptions()) {
			String optionName = escapePropertyName(option.getLongOpt());
			if (optionName.equals(propertyName)) {
				return option;
			}
		}
		return null;

	}

	private boolean optionPresent(CommandLine parsedOptions, String propertyName) {
		return findOption(parsedOptions, propertyName) != null;
	}

	private void assignOption(CommandLine parsedOptions, String propertyName,
			Object bean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		Option option = findOption(parsedOptions, propertyName);

		if (option != null) {
			assignOption(option, propertyName, bean);
		}
	}

	private void assignOption(Option option, String propertyName, Object bean)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		if (option == null) {
			return;
		}

		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
				bean, propertyName);

		// Boolean options must be treated differently
		Object value = null;
		if (!option.hasArg()) {
			// logger.debug("Flag Option");
			// This is a boolean option that is present
			value = typeCoercer.coerce(new Boolean(true),
					descriptor.getPropertyType());
		} else if (option.hasArgs()) {
			// logger.debug("Multiple Arguments Options");
			logger.debug("Assign values " + Arrays.toString(option.getValues())
					+ " to " + propertyName + " for bean " + bean.getClass());

			char[][] values = new char[option.getArgs()][];

			// We use this format to avoid failing in parsing the ","
			for (int i = 0; i < option.getArgs(); i++) {
				values[i] = option.getValues()[i].toCharArray();
			}

			// Transform back to String. This is necessary because the
			// target property may not be a String[]

			logger.debug("\n\nCoercing " + values + " to "
					+ descriptor.getPropertyType().getCanonicalName());

			value = typeCoercer.coerce(values, descriptor.getPropertyType());
		} else {
			// logger.debug("Single Argument option");
			logger.debug("Assign value " + option.getValue() + " to "
					+ propertyName + " for bean " + bean.getClass());
			value = typeCoercer.coerce(option.getValue(),
					descriptor.getPropertyType());
		}

		PropertyUtils.setProperty(bean, propertyName, value);

		logger.debug(propertyName + "  "
				+ PropertyUtils.getProperty(bean, propertyName));
	}

	// This is a recursive call !
	private void evaluateTheBean(CommandLine parsedOptions, Object bean)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ValidationException {

		Map properties = PropertyUtils.describe(bean);

		// Remove Objects Properties
		properties.remove("class");
		properties.remove("methods");

		for (Object _propName : properties.keySet()) {
			String propertyName = (String) _propName;

			// logger.debug("propertyName " + propertyName);

			// Check if this is contained in the parsed Options
			if (optionPresent(parsedOptions, propertyName)) {
				// logger.debug("The bean property (" + propertyName
				// + ") was specified as input");
				assignOption(parsedOptions, propertyName, bean);
			} else {
				logger.debug("The property (" + propertyName
						+ ") was not specified on the command line.");

				// Access the property
				PropertyDescriptor innerBean = PropertyUtils
						.getPropertyDescriptor(bean, propertyName);

				// Does it have a TypeCoercer from String to its' type ?

				try {
					typeCoercer.getCoercion(String.class,
							innerBean.getPropertyType());
					// If the object can be build from a string then it must be
					// a property
					logger.debug(propertyName
							+ " is actually a real property that was SKIP by the user !");
					continue;
				} catch (UnknownValueException uve) {
					// Check if that is actually a bean or something else
					// Try to go one step inside this and retry
					Object newInnerBeanInstance = null;
					Object originalInnerBeanInstance = PropertyUtils
							.getProperty(bean, propertyName);

					if (originalInnerBeanInstance != null) {
						newInnerBeanInstance = originalInnerBeanInstance;
					} else {

						try {
							newInnerBeanInstance = ConstructorUtils
									.invokeConstructor(
											innerBean.getPropertyType(),
											new Object[0]);
						} catch (NoSuchMethodException e) {
							// If does not provide a no args constructor by
							// definition its not a bean
							logger.debug(propertyName + " is not a bean SKIP! ");
							continue;
						} catch (Exception e) {
							logger.error("Generic error", e);
							throw new RuntimeException(e);
						}
					}
					// logger.debug("Evaluating the inner bean " +
					// propertyName);
					// Here we are almost sure that this is a bean, therefore we
					// make a recursive call and try to evaluate the inner bean
					evaluateTheBean(parsedOptions, newInnerBeanInstance);

					// logger.debug("Setting the inner bean "
					// + innerBean.getPropertyType());
					// If everything went fine then this should be ok
					PropertyUtils.setProperty(bean, propertyName,
							newInnerBeanInstance);
				} catch (Throwable e) {
					// Temporary patch
					logger.error("Exception ", e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	public ApplicationConfiguration get(CommandLine parsedOptions) {
		Collection<Object> properties = new ArrayList<Object>();

		for (Entry<String, Object> entry : contributions.entrySet()) {

			Object newBeanInstance = entry.getValue();

			// try {
			// // Create new instance of the contributed bean
			// newBeanInstance = ConstructorUtils.invokeConstructor(
			// originalBeanInstance.getClass(), new Object[0]);
			// } catch (Exception e) {
			// logger.error(" Error while instantiating bean "
			// + originalBeanInstance.getClass(), e);
			// throw new RuntimeException(e);
			// }
			try {
				// Set the value of the properties that we found inside CLI
				evaluateTheBean(parsedOptions, newBeanInstance);
			} catch (Exception e) {
				logger.error(" Error while setting bean properties", e);
				throw new RuntimeException(e);
			}

			// Here we overwrite all the properties contributed via the
			// annotation.
			// This is because the annotation is stronger than the naming
			// convention
			// !
			logger.debug("\t\n\n Override " + newBeanInstance + "\n\n\n");
			for (CLITuple tuple : propertiesOptionsMapping) {
				// We can directly access the option and the property because we
				// build the nested path for it
				logger.debug("\t\t TRY " + tuple.getProperty());
				try {

					if (PropertyUtils.getPropertyDescriptor(newBeanInstance,
							tuple.getProperty()) != null) {

						assignOption(
								findOption(parsedOptions, tuple.getOption()
										.getLongOpt()), tuple.getProperty(),
								newBeanInstance);
						logger.debug("\t\t DONE: " + tuple.getProperty()
								+ " for " + newBeanInstance);
					} else {
						logger.debug("\t\t NOT THERE : " + tuple.getProperty()
								+ " for " + newBeanInstance);
						logger.debug("Available Props: "
								+ PropertyUtils.describe(newBeanInstance));

					}
				} catch (NoSuchMethodException e) {
					logger.debug("NoSuchMethodException" + e.getMessage());
				} catch (Throwable e) {
					e.printStackTrace();
				}

			}

			// Add to the returned object
			properties.add(newBeanInstance);
		}
		return new ApplicationConfiguration(properties);
	}

	/*
	 * THis is now implemented directly inside CLIParserImpl
	 */
	// private void mergeAndKeepStrongest(Collection<Option> options, Option
	// option) {
	//
	// if (options.contains(option)) {
	// Option original = null;
	// for (Option o : options) {
	// if (o.equals(option)) {
	// original = o;
	// }
	// }
	//
	// if (original.isRequired() || !option.isRequired()) {
	// return;
	// }
	// options.remove(original);
	// options.add(option);
	//
	// } else {
	// options.add(option);
	// }
	//
	// }

	// // TODO We Assume that this will not create a mess by multiple
	// inconsistent
	// // option definitions
	// public Collection<Option> parsingOptions() {
	// Collection<Option> parsingOptions = new HashSet<Option>();
	// for (CLITuple tuple : propertiesOptionsMapping) {
	// // Here merge and keep the stronger !
	// mergeAndKeepStrongest(parsingOptions, tuple.getOption());
	// }
	// return parsingOptions;
	// }
}
