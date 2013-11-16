package org.gambi.tapestry5.cli.services;

import java.util.List;
import java.util.Map;

/**
 * THis ideally is used to build a validation chain/pipeline
 * 
 * @author alessiogambi
 * 
 */
public interface CLIValidator {

	public void validate(final Map<String, String> options,
			final List<String> inputs, List<String> accumulator);
}
