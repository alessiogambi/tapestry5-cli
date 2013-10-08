package org.gambi.tapestry5.cli.modules;

import org.apache.commons.cli.Option;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.gambi.tapestry5.cli.data.BeanA;
import org.gambi.tapestry5.cli.data.BeanFOO;

public class TestModule {

	// @Inject
	// private volatile Messages messages;

	// public void contributeCLIParser(Configuration<Option> configuration) {
	//
	// configuration.add(new Option("a", "alfa-option", true, messages
	// .get("alfa-description")));
	//
	// configuration.add(new Option("b", "beta", true, messages
	// .get("beta-description")));
	//
	// configuration.add(new Option("g", "this-is-gamma", true, messages
	// .get("This is gamma")));
	//
	// }

	public void contributeCLIParser(Configuration<Option> configuration) {
		configuration.add(new Option("a", "alfa", true, "alfa-description"));
		configuration.add(new Option("b", "beta", true, "beta-description"));
		configuration.add(new Option("g", "gamma", true, "This is gamma"));
	}

	public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {

		configuration.add("beanA", new BeanA());
		configuration.add("beanFOO", new BeanFOO());

	}
}
