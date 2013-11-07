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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((longOpt == null) ? 0 : longOpt.hashCode());
		result = prime * result + nArgs;
		result = prime * result
				+ ((shortOpt == null) ? 0 : shortOpt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CLIOption)) {
			return false;
		}
		CLIOption other = (CLIOption) obj;
		if (longOpt == null) {
			if (other.longOpt != null) {
				return false;
			}
		} else if (!longOpt.equals(other.longOpt)) {
			return false;
		}
		if (nArgs != other.nArgs) {
			return false;
		}
		if (shortOpt == null) {
			if (other.shortOpt != null) {
				return false;
			}
		} else if (!shortOpt.equals(other.shortOpt)) {
			return false;
		}
		return true;
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
