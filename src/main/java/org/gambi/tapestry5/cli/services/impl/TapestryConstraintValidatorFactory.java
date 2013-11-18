package org.gambi.tapestry5.cli.services.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.apache.tapestry5.ioc.ObjectLocator;

//http://tawus.wordpress.com/2011/05/12/tapestry-magic-12-tapestry-ioc-aware-jsr-303-custom-validators/
public class TapestryConstraintValidatorFactory implements
		ConstraintValidatorFactory {
	private ObjectLocator locator;

	public TapestryConstraintValidatorFactory(ObjectLocator locator) {
		this.locator = locator;
	}

	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		return locator.autobuild(key);
	}
}
