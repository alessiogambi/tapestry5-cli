package org.gambi.tapestry5.cli.data;

import org.apache.commons.cli.Option;

public class CLITuple {
	private String property; // Note this can be a nested property !
	private Option option;

	public CLITuple(String property, Option option) {
		super();
		this.property = property;
		this.option = option;
	}

	public final String getProperty() {
		return property;
	}

	public final Option getOption() {
		return option;
	}
}
