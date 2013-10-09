package org.gambi.tapestry5.cli.validators;

import java.net.URL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.gambi.tapestry5.cli.annotations.ValidURL;

/**
 * This is an example of a custom Validator directly defined to work with
 * JSR-303
 * 
 * @author alessiogambi
 * 
 */
public class URLConstraintValidator implements
		ConstraintValidator<ValidURL, Object> {

	// NOTE: apparently having something like "http:// a c c dcd cd" is still a
	// valid URL... sounds quite odd althoug

	public void initialize(ValidURL paramA) {
	}

	// The alternative would be to pattern match with
	// @Pattern(regexp =
	// "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
	public boolean isValid(Object url,
			ConstraintValidatorContext paramConstraintValidatorContext) {

		// By definition null objects are valid
		if (url == null) {
			return true;
		}
		if (url instanceof URL) {
			return true;
		} else if (url instanceof String) {
			// The naive way...
			try {
				new URL((String) url);
			} catch (Exception e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
}
