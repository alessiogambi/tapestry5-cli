package org.gambi.tapestry5.cli.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.gambi.tapestry5.cli.services.CLIValidatorFilter;
import org.slf4j.Logger;

public class SumValidator implements CLIValidatorFilter {

	@Inject
	private Logger logger;

	@Inject
	private TypeCoercer typeCoercer;

	public List<String> validate(final Map<String, String> inputs) {

		List<String> failedValidation = new ArrayList<String>();
		try {
			// NOTE WE NEED SOME REAL WAY TO STORE AND RETRIEVE SYMBOLS THAT
			// MATCH CLI OBJECTS !
			Integer a = typeCoercer.coerce(inputs.get("args:input[0]"),
					Integer.class);
			Integer b = typeCoercer.coerce(inputs.get("args:input[1]"),
					Integer.class);

			if (a + b > 100) {
				failedValidation.add("SumValidation Failed ! a+b > 10 ");
			}
		} catch (Exception e) {
			logger.warn("Error during validation ", e);
			failedValidation.add("SumValidation Failed!");
		}
		return failedValidation;
	}

	public void validate(Map<String, String> inputs, List<String> accumulator,
			CLIValidator delegate) {

		// This will accumulate stuff from previous filters
		delegate.validate(inputs, accumulator);

		accumulator.addAll(validate(inputs));
	}
}
