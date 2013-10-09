package org.gambi.tapestry5.cli.modules;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.gambi.tapestry5.cli.data.BeanA;
import org.gambi.tapestry5.cli.data.BeanFOO;
import org.gambi.tapestry5.cli.data.BeanT5;
import org.gambi.tapestry5.cli.data.BooleanBean;
import org.gambi.tapestry5.cli.data.NestedBean;
import org.gambi.tapestry5.cli.data.VectorBean;

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
		configuration.add(new Option("d", "delta", true, "Delta"));
		configuration.add(new Option("e", "epsilon", true, "Epsilon"));
		configuration.add(new Option("o", "omega", true,
				"This is validated via T5-Validators"));
		configuration.add(new Option("su", "string-url", true,
				"An URL passed a STRING"));
		configuration.add(new Option("u", "url", true, "An URL"));
		configuration.add(new Option("j", "jonny", false, "A boolean option"));
		configuration.add(new Option("t", "tommy", false,
				"Another boolean option"));

		configuration.add(OptionBuilder.withArgName("vector")
				.withLongOpt("vector").hasArgs(3).create("v"));
	}

	public void contributeApplicationConfigurationSource(
			MappedConfiguration<String, Object> configuration) {

		// configuration.add("beanA", new BeanA());
		// configuration.add("beanFOO", new BeanFOO());
		// configuration.add("T5Bean", new BeanT5());
		// configuration.add("BooleanBean", new BooleanBean());
		// configuration.add("VectorBean", new VectorBean());
		configuration.add("NestedBean", new NestedBean());

	}
}
