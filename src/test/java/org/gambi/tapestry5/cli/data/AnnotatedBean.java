package org.gambi.tapestry5.cli.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gambi.tapestry5.cli.annotations.ParsingOption;

public class AnnotatedBean {

	public AnnotatedBean() {
		// TODO Auto-generated constructor stub
	}

	@Size(min = 3)
	@ParsingOption(opt = "z", longOpt = "zeta-option", hasArg = true)
	public String zappa;

	@NotNull(message = "Field ZetaOption is NUll")
	// Had both annotations AND name matching. Annotations should be stronger.
	@ParsingOption(opt = "z", longOpt = "zeta-option", hasArg = true)
	public String zetaOption;

	public String getZetaOption() {
		return zetaOption;
	}

	public void setZetaOption(String zetaOption) {
		this.zetaOption = zetaOption;
	}

	public String getZappa() {
		return zappa;
	}

	public void setZappa(String zappa) {
		this.zappa = zappa;
	}

}
