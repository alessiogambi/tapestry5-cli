package org.gambi.tapestry5.cli.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.CLIValidatorFilter;
import org.slf4j.Logger;

public class CLIValidatorFilterImpl implements CLIValidatorFilter {

	private CLIValidator cliValidator;
	private Logger logger;

	public CLIValidatorFilterImpl(Logger logger, CLIValidator cliValidator) {
		this.logger = logger;
		this.cliValidator = cliValidator;
	}

	public void validate(Map<String, String> options, List<String> inputs,
			List<String> accumulator, CLIValidator delegate) {
		/*
		 * By default invoke mine first, the others then. Capture all the
		 * exceptions
		 */
		if (accumulator == null) {
			logger.debug("Initialize the accumulator data structure");
			accumulator = new ArrayList<String>();
		}

		try {

			cliValidator.validate(options, inputs, accumulator);
		} catch (Throwable e) {
			logger.warn("Exception while validating options/input:", e);
		} finally {
			delegate.validate(options, inputs, accumulator);
		}

	}
}
