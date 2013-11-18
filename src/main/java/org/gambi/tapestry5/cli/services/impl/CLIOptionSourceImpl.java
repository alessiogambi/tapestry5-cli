package org.gambi.tapestry5.cli.services.impl;

import java.util.List;

import org.gambi.tapestry5.cli.services.CLIOptionProvider;
import org.gambi.tapestry5.cli.services.CLIOptionSource;

public class CLIOptionSourceImpl implements CLIOptionSource {

	private List<CLIOptionProvider> providers;

	public CLIOptionSourceImpl(List<CLIOptionProvider> providers) {
		this.providers = providers;
	}

	public String valueForOption(String optionName) {
		for (CLIOptionProvider provider : providers) {
			if (provider.valueForOption(optionName) != null) {
				return provider.valueForOption(optionName);
			}
		}
		return null;
	}

	public String[] valuesForOption(String optionName) {
		for (CLIOptionProvider provider : providers) {
			if (provider.valuesForOption(optionName) != null) {
				return provider.valuesForOption(optionName);
			}
		}
		return null;
	}

	public String valueForInput(int inputPosition) {
		for (CLIOptionProvider provider : providers) {
			if (provider.valueForInput(inputPosition) != null) {
				return provider.valueForInput(inputPosition);
			}
		}
		return null;
	}
}
