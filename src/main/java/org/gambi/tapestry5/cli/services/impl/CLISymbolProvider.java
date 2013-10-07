package org.gambi.tapestry5.cli.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.services.SymbolProvider;

/**
 * Ideally this must be contributed to SymbolSource via an injection, however
 * this must be "Instantiated" by the CLI Parser. One solution would be to let
 * the CLIParser to get the SymbolSource and provide CLIParser to the
 * contributeSymbolSource method. It feels really hacky in this way
 * 
 * @author alessiogambi
 * 
 */
public class CLISymbolProvider implements SymbolProvider {

	public Map<String, String> symbols;

	public CLISymbolProvider() {
		symbols = new HashMap<String, String>();
	}

	public String valueForSymbol(String symbolName) {
		return symbols.get(symbolName);
	}

	public void addSymbols(String symbolName, String symbolValue) {
		if (symbols.containsKey(symbolName)) {
			throw new RuntimeException(String.format(
					"Symbol %s is already defined", symbolName));
		} else {
			symbols.put(symbolName, symbolValue);
		}
	}
}
