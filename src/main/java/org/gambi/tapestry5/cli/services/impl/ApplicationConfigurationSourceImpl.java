package org.gambi.tapestry5.cli.services.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.services.TypeCoercer;
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

	private void evaluateTheBean(CommandLine parsedOptions, Object bean)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ValidationException {
		// Fill the matching props of the Bean
		Map properties = PropertyUtils.describe(bean);
		for (Option option : parsedOptions.getOptions()) {

			String propertyName = escapePropertyName(option.getLongOpt());

			// This must be transformed the proper way !!
			// dash char - is not valid for variable/method names !
			// Either use _ instead or the camel notation
			// alpha-beta will become alphaBeta

			logger.debug("propertyName " + propertyName);
			if (properties.containsKey(propertyName)) {

				PropertyDescriptor descriptor = PropertyUtils
						.getPropertyDescriptor(bean, propertyName);
				logger.debug("PropertyType " + descriptor.getPropertyType());

				Object value = typeCoercer.coerce(option.getValue(),
						descriptor.getPropertyType());

				PropertyUtils.setProperty(bean, propertyName, value);
				logger.debug("The bean contains the property " + propertyName
						+ ". Set its value to " + value);

			} else {
				logger.debug("The bean does not contains the property "
						+ propertyName);
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
