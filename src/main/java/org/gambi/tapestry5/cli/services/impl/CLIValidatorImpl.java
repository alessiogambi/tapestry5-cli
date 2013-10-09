package org.gambi.tapestry5.cli.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.services.CLIValidator;

public class CLIValidatorImpl implements CLIValidator {

	// As default return an empty list of violations
	public List<String> validate(Map<String, String> cli) {
		return new ArrayList<String>();
	}

}
