package org.gambi.tapestry5.cli.services;

import java.util.List;
import java.util.Map;

public interface CLIValidator {

	public List<String> validate(Map<String, String> cli);
}
