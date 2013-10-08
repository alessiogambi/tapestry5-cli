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

	private void evaluateTheBean(CommandLine parsedOptions, Object bean)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ValidationException {
		// Fill the matching props of the Bean
		Map properties = PropertyUtils.describe(bean);
		for (Option option : parsedOptions.getOptions()) {
			String propertyName = option.getLongOpt();
			System.out.println("propertyName " + propertyName);
			if (properties.containsKey(propertyName)) {
				System.out.println("The bean contains the property "
						+ propertyName + ". Set its value ");

				PropertyDescriptor descriptor = PropertyUtils
						.getPropertyDescriptor(bean, propertyName);
				System.out.println("PropertyType "
						+ descriptor.getPropertyType());

				Object value = typeCoercer.coerce(option.getValue(),
						descriptor.getPropertyType());

				PropertyUtils.setProperty(bean, propertyName, value);

			} else {
				System.out.println("The bean does not contains the property "
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
