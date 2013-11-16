package org.gambi.tapestry5.cli.services;

public interface CLIOptionProvider {

	public String valueForOption(String optionName);

	public String valueForInput(int position);

}
