package org.gambi.tapestry5.cli.services;

import java.util.Map;

import javax.validation.ValidationException;

import org.apache.commons.cli.ParseException;

public interface CLIParser {

	public void parse(String[] args) throws ParseException, ValidationException;

	// Not sure if really ok
	public Map<String, String> getSymbols();

}
