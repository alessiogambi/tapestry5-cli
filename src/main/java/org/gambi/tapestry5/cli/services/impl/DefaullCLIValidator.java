package org.gambi.tapestry5.cli.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.services.CLIValidator;

/**
 * The terminal validator.. it just return
 * 
 * @author alessiogambi
 * 
 */
public class DefaullCLIValidator implements CLIValidator {

	// As default return an empty list of violations
	public void validate(Map<String, String> cli, List<String> accumulator) {
		if (accumulator == null) {
			accumulator = new ArrayList<String>();
		}
	}
}
