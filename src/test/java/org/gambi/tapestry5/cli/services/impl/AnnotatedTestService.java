package org.gambi.tapestry5.cli.services.impl;

import org.gambi.tapestry5.cli.annotations.CLIOption;

public class AnnotatedTestService {

	private String alfa;

	public AnnotatedTestService(
			@CLIOption(name = "a", longName = "alfa") String alfa) {
		this.alfa = alfa;
	}

	public String getOptionAlfaValue() {
		return alfa;
	}
}
