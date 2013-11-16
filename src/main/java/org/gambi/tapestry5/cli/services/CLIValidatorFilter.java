package org.gambi.tapestry5.cli.services;

import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.data.CLIOption;

public interface CLIValidatorFilter {

	public void validate(Map<CLIOption, String> options, List<String> inputs,
			List<String> accumulator, CLIValidator delegate);
}
