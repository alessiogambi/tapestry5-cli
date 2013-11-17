package org.gambi.tapestry5.cli.services;

/**
 * Services implementing this interface are meant to provide a value for a given
 * option by name, or input by position.
 * 
 * @author alessiogambi
 * 
 */
public interface CLIOptionSource {

	/**
	 * Return the option corresponding to the optionName (either short or long
	 * version)
	 * 
	 * The method is supposed to return null if the option was not specified and
	 * have not default value.
	 * 
	 * 
	 * @param optionName
	 * @return
	 */
	public String valueForOption(String optionName);

	/**
	 * 
	 * @param inputPosition
	 * @return
	 */
	public String valueForInput(int inputPosition);
}
