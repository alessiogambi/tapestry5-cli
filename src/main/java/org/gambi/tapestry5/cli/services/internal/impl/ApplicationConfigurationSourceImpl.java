package org.gambi.tapestry5.cli.services.internal.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.util.UnknownValueException;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.services.internal.ApplicationConfigurationSource;
import org.slf4j.Logger;

public class ApplicationConfigurationSourceImpl implements
		ApplicationConfigurationSource {

	private Logger logger;

	// Since TranslatoSource is not there we need to used typeCoercer
	private TypeCoercer typeCoercer;

	// The properties to be validate... all of them have the validation
	// annotations
	private Map<String, Object> contributions;

	public ApplicationConfigurationSourceImpl(Logger logger,
			TypeCoercer typeCoercer, Map<String, Object> contributions) {
		this.logger = logger;
		this.typeCoercer = typeCoercer;
		this.contributions = contributions;
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

	private CLIOption findOption(Collection<CLIOption> parsedOptions,
			String propertyName) {
		for (CLIOption option : parsedOptions) {
			String optionName = escapePropertyName(option.getLongOpt());
			if (optionName.equals(propertyName)) {
				return option;
			}
		}
		return null;

	}

	private boolean optionPresent(Collection<CLIOption> parsedOptions,
			String propertyName) {
		return findOption(parsedOptions, propertyName) != null;
	}

	private void assignOption(Collection<CLIOption> parsedOptions,
			String propertyName, Object bean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		CLIOption option = findOption(parsedOptions, propertyName);

		if (option != null) {
			assignOption(option, propertyName, bean);
		}
	}

	private void assignOption(CLIOption option, String propertyName, Object bean)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		if (option == null) {
			return;
		}

		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
				bean, propertyName);

		// Boolean options must be treated differently
		Object value = null;
		if (option.getnArgs() == 0) {
			// logger.debug("Flag Option");
			// This is a boolean option that is present
			value = typeCoercer.coerce(new Boolean(true),
					descriptor.getPropertyType());
		} else if (option.getnArgs() > 1) {
			// logger.debug("Multiple Arguments Options");
			logger.debug("Assign values " + Arrays.toString(option.getValues())
					+ " to " + propertyName + " for bean " + bean.getClass());

			char[][] values = new char[option.getnArgs()][];

			// We use this format to avoid failing in parsing the ","
			for (int i = 0; i < option.getnArgs(); i++) {
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
	private void evaluateTheBean(Collection<CLIOption> parsedOptions,
			List<String> parsedInputs, Object bean)
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
					evaluateTheBean(parsedOptions, parsedInputs,
							newInnerBeanInstance);

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

	public ApplicationConfiguration get(Collection<CLIOption> parsedOptions,
			List<String> parsedInputs) {
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
				evaluateTheBean(parsedOptions, parsedInputs, newBeanInstance);
			} catch (Exception e) {
				logger.error(" Error while setting bean properties", e);
				throw new RuntimeException(e);
			}

			// Add to the returned object
			properties.add(newBeanInstance);
		}
		return new ApplicationConfiguration(properties);
	}

}
