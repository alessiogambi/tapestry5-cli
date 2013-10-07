package org.gambi.tapestry5.cli.services;

public interface CLIParser {

	public void parse(String[] args) throws IllegalArgumentException;
}
