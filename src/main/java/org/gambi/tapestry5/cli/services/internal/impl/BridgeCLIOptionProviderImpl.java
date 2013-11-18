package org.gambi.tapestry5.cli.services.internal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.services.internal.BridgeCLIOptionProvider;

public class BridgeCLIOptionProviderImpl implements BridgeCLIOptionProvider {

	private List<String> inputs;
	// TODO: if values are stored inside options why do we need a MAP ? can we
	// just use a collection of CLIOptions?
	private Map<CLIOption, String> options;

	public BridgeCLIOptionProviderImpl() {
		inputs = new ArrayList<String>();
		options = new HashMap<CLIOption, String>();
	}

	public String valueForInput(int inputPosition) {
		try {
			return inputs.get(inputPosition);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public String valueForOption(String optionName) {
		try {
			for (CLIOption cliOption : options.keySet()) {
				if (cliOption.getShortOpt().equals(optionName)
						|| cliOption.getLongOpt().equals(optionName)) {
					return cliOption.getValue();
				}
			}
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] valuesForOption(String optionName) {
		try {
			for (CLIOption cliOption : options.keySet()) {
				if (cliOption.getShortOpt().equals(optionName)
						|| cliOption.getLongOpt().equals(optionName)) {
					return cliOption.getValues();
				}
			}
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public void add(Map<CLIOption, String> options) {
		this.options.putAll(options);

	}

	public void add(List<String> inputs) {
		this.inputs.addAll(inputs);

	}

}
