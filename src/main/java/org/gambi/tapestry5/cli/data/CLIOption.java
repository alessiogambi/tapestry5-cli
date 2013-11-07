package org.gambi.tapestry5.cli.data;

/**
 * This class contains all the information for defining Command line options. It
 * will be used later inside the CLIParser to instantiate the used CLIparsing
 * framework, i.e., Apache-commonsCLI
 * 
 * @author alessiogambi
 * 
 */
public class CLIOption {

	private String shortOpt;
	private String longOpt;
	private int nArgs;
	boolean required;
	private String description;

	public CLIOption(String shortOpt, String longOpt, int nArgs,
			boolean required, String description) {
		super();
		this.shortOpt = shortOpt;
		this.longOpt = longOpt;
		this.nArgs = nArgs;
		this.required = required;
		this.description = description;
	}


	public String getShortOpt() {
		return shortOpt;
	}

	public void setShortOpt(String shortOpt) {
		this.shortOpt = shortOpt;
	}

	public String getLongOpt() {
		return longOpt;
	}

	public void setLongOpt(String longOpt) {
		this.longOpt = longOpt;
	}

	public int getnArgs() {
		return nArgs;
	}

	public void setnArgs(int nArgs) {
		this.nArgs = nArgs;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
