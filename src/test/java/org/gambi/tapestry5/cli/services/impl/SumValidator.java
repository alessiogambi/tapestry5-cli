package org.gambi.tapestry5.cli.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.gambi.tapestry5.cli.services.CLIValidator;
import org.slf4j.Logger;

/**
 * Sample validator to check if the sum of the first two inputs (not options !)
 * is greater than 100.
 * 
 * Being a CLIValidator, this service is invoked as the last step before ending
 * the parsing.
 * 
 * @author alessiogambi
 * 
 */
public class SumValidator implements CLIValidator {

	@Inject
	private Logger logger;

	@Inject
	private TypeCoercer typeCoercer;

	public void validate(Map<String, String> options, List<String> inputs,
			List<String> accumulator) {

		List<String> failedValidation = new ArrayList<String>();
		try {
			Integer a = typeCoercer.coerce(inputs.get(0), Integer.class);
			Integer b = typeCoercer.coerce(inputs.get(1), Integer.class);

			if (a + b > 100) {
				failedValidation.add("SumValidation Failed ! a+b > 100");
			}
		} catch (Throwable e) {
			logger.warn("Error during validation ", e);
			failedValidation.add("SumValidation Failed!");
		}

		if (failedValidation.size() > 0) {
			logger.debug("Validation Failed: " + failedValidation);
		}
		accumulator.addAll(failedValidation);
	}

}
