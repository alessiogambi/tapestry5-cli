

import org.apache.commons.cli.CommandLine;

public class ApplicationConfiguration {
	private CommandLine parsedOptions;

	public ApplicationConfiguration(CommandLine parsedOptions) {
		this.parsedOptions = parsedOptions;
	}

	public boolean hasOption(String option) {
		return parsedOptions.hasOption(option);
	}

	public String getOptionValue(String option) {
		return parsedOptions.getOptionValue(option);
	}
}
