package org.gambi.tapestry5.cli.data;

import javax.validation.constraints.NotNull;

import org.gambi.tapestry5.cli.annotations.ValidURL;

public class BeanWithURL {

	@NotNull
	@ValidURL
	private String theURL;

	public BeanWithURL() {
	}

	public String getTheURL() {
		return theURL;
	}

	public void setTheURL(String theURL) {
		this.theURL = theURL;
	}

}
