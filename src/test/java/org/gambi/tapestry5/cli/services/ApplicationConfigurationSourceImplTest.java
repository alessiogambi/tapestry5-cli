package org.gambi.tapestry5.cli.services;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.gambi.tapestry5.cli.data.BeanA;
import org.junit.Test;

public class ApplicationConfigurationSourceImplTest {

	@Test
	public void injectValues() throws ParseException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		String[] args = new String[] { "-a", "10", "--beta", "7", "-g",
				"the gamma input", "first-arg", "second-args", "whaterver" };

		Options options = new Options();
		options.addOption(new Option("a", "alfa", true, "alfa-description"));
		options.addOption(new Option("b", "beta", true, "beta-description"));
		options.addOption(new Option("g", "gamma", true, "This is gamma"));

		CommandLine parsedOptions = (new BasicParser()).parse(options, args);

		Object bean = new BeanA();

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

				PropertyUtils
						.setProperty(bean, propertyName, option.getValue());

			} else {
				System.out.println("The bean does not contains the property "
						+ propertyName);
			}

			// PropertyDescriptor propertyDescriptor = PropertyUtils
			// .getPropertyDescriptor(aBean, propertyName);
			// System.out
			// .println("ApplicationConfigurationSourceImpl.injectValues() "
			// + propertyName);
		}

		// PropertyUtils
	}
}
