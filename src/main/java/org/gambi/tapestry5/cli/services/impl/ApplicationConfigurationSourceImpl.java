package org.gambi.tapestry5.cli.services.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.util.UnknownValueException;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.services.ApplicationConfigurationSource;
import org.slf4j.Logger;

public class ApplicationConfigurationSourceImpl implements
		ApplicationConfigurationSource {

	private Logger logger;

	// Since TranslatoSource is not there we need to used typeCoercer
	private TypeCoercer typeCoercer;

	// The properties to be validate... all of them have the validation
	// annotations
	private Map<String, Object> beans;

	public ApplicationConfigurationSourceImpl(Logger logger,
			TypeCoercer typeCoercer, Map<String, Object> beans) {
		this.logger = logger;
		this.typeCoercer = typeCoercer;
		this.beans = beans;
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
		logger.debug("Assign " + option + " to " + propertyName);

		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
				bean, propertyName);
		// Boolean options must be treated differently
		Object value = null;
		if (!option.hasArg()) {
			logger.debug("Flag Option");
			// This is a boolean option that is present
			value = typeCoercer.coerce(new Boolean(true),
					descriptor.getPropertyType());
		} else if (option.hasArgs()) {
			logger.debug("Multiple Arguments Options");
			char[][] values = new char[option.getArgs()][];

			// We use this format to avoid failing in parsing ,
			for (int i = 0; i < option.getArgs(); i++) {
				values[i] = option.getValues()[i].toCharArray();
			}

			// Transform back to String. This is necessary because the
			// target property may not be a String[]
			value = typeCoercer.coerce(Arrays.toString(values),
					descriptor.getPropertyType());
		} else {
			logger.debug("Single Argument option");
			value = typeCoercer.coerce(option.getValue(),
					descriptor.getPropertyType());
		}

		PropertyUtils.setProperty(bean, propertyName, value);
	}

	private void evaluateTheBean(CommandLine parsedOptions, Object bean)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ValidationException {

		// Fill the matching props of the Bean
		Map properties = PropertyUtils.describe(bean);

		for (Object _propName : properties.keySet()) {
			String propertyName = (String) _propName;

			logger.debug("propertyName " + propertyName);

			// Check if this is contained in the parsed Options
			if (optionPresent(parsedOptions, propertyName)) {
				logger.debug("The bean property (" + propertyName
						+ ") was specified as input");
				assignOption(parsedOptions, propertyName, bean);
			} else {
				logger.debug("The property was not specified on the command line.");

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
					return;
				} catch (UnknownValueException uve) {
					// Check if that is actually a bean or something else

					// Try to go one step inside this and retry
					Object newInnerBeanInstance = null;
					try {
						newInnerBeanInstance = ConstructorUtils
								.invokeConstructor(innerBean.getPropertyType(),
										new Object[0]);
					} catch (NoSuchMethodException e) {
						// If does not provide a no args constructor by
						// definition its not a bean
						logger.debug(propertyName + " is not a bean ! ");
						return;
					} catch (Exception e) {
						logger.error("Generic error", e);
						throw new RuntimeException(e);
					}

					logger.debug("Evaluating the inner bean " + propertyName);
					// Here we are almost sure that this is a bean, therefore we
					// make a recursive call and try to evaluate the inner bean
					evaluateTheBean(parsedOptions, newInnerBeanInstance);

					logger.debug("Setting the inner bean "
							+ innerBean.getPropertyType());
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
		Collection<Object> _beans = new ArrayList<Object>();
		for (Entry<String, Object> entry : beans.entrySet()) {

			Object originalBeanInstance = entry.getValue();
			Object newBeanInstance = null;
			try {
				// Create a copy of the bean
				newBeanInstance = ConstructorUtils.invokeConstructor(
						originalBeanInstance.getClass(), new Object[0]);

				// Set the value of the properties that we found inside CLI
				evaluateTheBean(parsedOptions, newBeanInstance);
			} catch (Exception e) {
				logger.error(" Error while setting values for the bean", e);
			}
			// Add to the returned object
			_beans.add(newBeanInstance);
		}
		return new ApplicationConfiguration(_beans);
	}
}
