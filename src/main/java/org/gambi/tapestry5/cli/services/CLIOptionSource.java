package org.gambi.tapestry5.cli.services;

public interface CLIOptionSource {

	public String valueForOption(String optionName);

	public String valueForInput(int inputPosition);
}
