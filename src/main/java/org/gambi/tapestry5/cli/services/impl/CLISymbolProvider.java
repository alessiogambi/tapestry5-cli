package org.gambi.tapestry5.cli.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.gambi.tapestry5.cli.services.RuntimeSymbolProvider;

/**
 * 
 * @author alessiogambi
 * 
 */
public class CLISymbolProvider implements RuntimeSymbolProvider {

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

	public void addSymbols(Map<String, String> runtimeSymbols) {
		symbols.putAll(runtimeSymbols);

	}
}
