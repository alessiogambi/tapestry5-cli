package org.gambi.tapestry5.cli.services;

import javax.validation.ValidationException;

import org.apache.commons.cli.ParseException;

public interface CLIParser {

	public void parse(String[] args) throws ParseException, ValidationException;

}
