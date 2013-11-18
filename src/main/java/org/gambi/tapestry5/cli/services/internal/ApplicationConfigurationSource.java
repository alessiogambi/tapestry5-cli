package org.gambi.tapestry5.cli.services.internal;

import java.util.Collection;
import java.util.List;

import org.gambi.tapestry5.cli.data.ApplicationConfiguration;
import org.gambi.tapestry5.cli.data.CLIOption;

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
	 * @param parsedInputs
	 * @return
	 */
	public ApplicationConfiguration get(Collection<CLIOption> parsedOptions,
			List<String> parsedInputs);

}
