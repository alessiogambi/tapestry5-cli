package org.gambi.tapestry5.cli.services;

import java.util.List;
import java.util.Map;

public interface CLIValidatorFilter {

	public void validate(Map<String, String> inputs, List<String> accumulator,
			CLIValidator delegate);
}
