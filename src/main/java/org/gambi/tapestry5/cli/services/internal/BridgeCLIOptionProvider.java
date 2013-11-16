package org.gambi.tapestry5.cli.services.internal;

import java.util.List;
import java.util.Map;

import org.gambi.tapestry5.cli.data.CLIOption;
import org.gambi.tapestry5.cli.services.CLIOptionProvider;

public interface BridgeCLIOptionProvider extends CLIOptionProvider {

	public void add(Map<CLIOption, String> options);

	public void add(List<String> inputs);
}
