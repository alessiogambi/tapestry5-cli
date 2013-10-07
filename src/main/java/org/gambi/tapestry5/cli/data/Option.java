package org.gambi.tapestry5.cli.data;


public class Option {

	private org.apache.commons.cli.Option delegate;
	private String[] validatorNames;

	public Option(String opt, String longOpt, boolean hasArg,
			String description, String... validatorNames) {
		this.delegate = new org.apache.commons.cli.Option(opt, longOpt, hasArg,
				description);
		this.validatorNames = validatorNames;
	}

	public org.apache.commons.cli.Option getOption() {
		return delegate;
	}

	public String[] getValidatorNames() {
		return validatorNames;
	}
}
