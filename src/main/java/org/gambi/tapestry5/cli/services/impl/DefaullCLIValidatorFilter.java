package org.gambi.tapestry5.cli.services.impl;

import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.CLIValidatorFilter;

/**
 * The terminal validator.. it just return
 * 
 * @author alessiogambi
 * 
 */
public class DefaullCLIValidatorFilter implements CLIValidatorFilter {

	public void validate(Map<CLIOption, String> options, List<String> inputs,
			List<String> accumulator, CLIValidator delegate) {
		return;
	}
}
