package org.gambi.tapestry5.cli.services.internals;

import org.apache.commons.cli.CommandLine;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;

/**
 * This is the service that instantiate user's provided beans and set the value
 * (sometimes coerced) of the matched properties after the parsing.
 * 
 * <strong>This is an internal service so its direct usage is not suggested as
 * it may changes without prior notice</strong>
 * 
 * @author alessiogambi
 * 
 */
public interface ApplicationConfigurationSource {

	/**
	 * Instantiate an {@link ApplicationConfiguration} object given the output
	 * of the parsing
	 * 
	 * @param parsedOptions
	 * @return
	 */
	public ApplicationConfiguration get(CommandLine parsedOptions);

}
