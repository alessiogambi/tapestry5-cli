package org.gambi.tapestry5.cli.services;

import javax.validation.ValidationException;

import org.apache.commons.cli.ParseException;
import org.gambi.tapestry5.cli.data.ApplicationConfiguration;

public interface CLIParser {

	public ApplicationConfiguration parse(String[] args) throws ParseException,
			ValidationException;
}
