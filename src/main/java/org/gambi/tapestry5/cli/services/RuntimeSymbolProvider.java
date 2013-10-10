package org.gambi.tapestry5.cli.services;

import java.util.Map;

import org.apache.tapestry5.ioc.services.SymbolProvider;

public interface RuntimeSymbolProvider extends SymbolProvider {

	public void addSymbols(Map<String, String> runtimeSymbols);
}
