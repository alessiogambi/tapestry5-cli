package org.gambi.tapestry5.cli.data;

import org.apache.commons.cli.OptionBuilder;

/**
 * This class contains all the information for defining Command line options. It
 * will be used later inside the CLIParser to instantiate the used CLIparsing
 * framework, i.e., Apache CommonsCLI
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

	@SuppressWarnings("static-access")
	@Override
	public String toString() {
		// TODO Maybe not the best way of doing this ;
		return OptionBuilder.withLongOpt(longOpt).hasArgs(nArgs)
				.isRequired(required).withDescription(description)
				.create(shortOpt).toString();
	}

	public String getShortOpt() {
		return shortOpt;
	}

	/**
	 * Return true if the two provided option conflicts with the object.
	 * 
	 * Conflicting definitions are identified by the same short notation but
	 * different long notation, or vice-versa; conflicting definitions are also
	 * identified when the notations match but the expected number of parameters
	 * differs
	 * 
	 * @param anotherOption
	 * @return
	 */
	public boolean conflicts(CLIOption anotherOption) {
		if (anotherOption == null) {
			return false;
		} else if (this.shortOpt.equals(anotherOption.shortOpt)
				&& !this.longOpt.equals(anotherOption.longOpt)) {
			return true;
		} else if (!this.shortOpt.equals(anotherOption.shortOpt)
				&& this.longOpt.equals(anotherOption.longOpt)) {
			return true;
		} else if (this.shortOpt.equals(anotherOption.shortOpt)
				&& this.longOpt.equals(anotherOption.longOpt)
				&& this.nArgs != anotherOption.nArgs) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Merge the CLIOption with the provided anotherOption.
	 * 
	 * Definitions that have different descriptions, or different required
	 * attributed are merged according to the following rule: Descriptions are
	 * appended one after the other, while stronger requirements are maintained.
	 * In other words, if two options with same notations and expected parameter
	 * have different required attribute, the library forces the strictest one,
	 * i.e., required.
	 * 
	 * @param anotherOption
	 */
	public void merge(CLIOption anotherOption) {
		if (anotherOption == null) {
			return;
		} else if (this.conflicts(anotherOption)) {
			return;
		} else {
			// Append the Descriptions
			this.description = String.format("%s. %s", this.description,
					anotherOption.description);
			// Pick the strongest requires
			this.required = (this.required || anotherOption.required);
		}
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
