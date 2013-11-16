package org.gambi.tapestry5.cli.validators;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.gambi.tapestry5.cli.CLIModule;
import org.gambi.tapestry5.cli.data.BeanWithURL;
import org.gambi.tapestry5.cli.modules.TestModule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the ValidURL custom JSR303 validator.
 * 
 * @author alessiogambi
 * 
 */
public class BasicJSR303ValidatorTest {

	private Registry registry;
	private RegistryBuilder builder;

	@Before
	public void setup() {
		// TODO Auto-generated constructor stub
		builder = new RegistryBuilder();
		// Load all the modules in the cp
		IOCUtilities.addDefaultModules(builder);

		// Load all the local modules
		builder.add(CLIModule.class);
		// Add the test module
		builder.add(TestModule.class);

		registry = builder.build();
		registry.performRegistryStartup();
	}

	@After
	public void shutdown() {
		if (registry != null) {
			registry.shutdown();
		}
	}

	@Test
	public void validateURL() {
		BeanWithURL b = new BeanWithURL();
		try {
			b.setTheURL(new URL("http://google.com").toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Validator validator = registry.getService(Validator.class);
		Set<ConstraintViolation<BeanWithURL>> result = validator.validate(b);

		Assert.assertTrue(result.size() == 0);
	}

	@Test
	public void validateNullURL() {
		BeanWithURL b = new BeanWithURL();

		Validator validator = registry.getService(Validator.class);
		Set<ConstraintViolation<BeanWithURL>> result = validator.validate(b);

		System.out.println("BasicJSR303ValidatorTest.validateNullURL()"
				+ result);
		Assert.assertTrue(result.size() != 0);
	}

	@Test
	public void validateWrongURL() {
		BeanWithURL b = new BeanWithURL();
		b.setTheURL("Not a valid URL");
		Validator validator = registry.getService(Validator.class);
		Set<ConstraintViolation<BeanWithURL>> result = validator.validate(b);

		System.out.println("BasicJSR303ValidatorTest.validateWrongURL(): "
				+ result);
		Assert.assertTrue(result.size() != 0);
	}
}
