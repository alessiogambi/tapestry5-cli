package org.gambi.tapestry5.cli.data;

import org.apache.tapestry5.beaneditor.Validate;

/*
 * This class MUST be validated via the Tapestry validator named minlength and NOT via the JSR303 validator !
 */
public class BeanT5 {

	@Validate("minlength=10")
	// @Validate("required")
	private String omega;

	public BeanT5() {
	}

	public String getOmega() {
		return omega;
	}

	public void setOmega(String omega) {
		this.omega = omega;
	}
}
