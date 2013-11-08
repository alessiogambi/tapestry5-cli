package org.gambi.tapestry5.cli.data;

import java.net.URL;

import javax.validation.constraints.NotNull;

import org.gambi.tapestry5.cli.annotations.ValidURL;

public class BeanWithURL {

	@NotNull
	@ValidURL
	private URL theURL;

	public BeanWithURL() {
	}

	public URL getTheURL() {
		return theURL;
	}

	public void setTheURL(URL theURL) {
		this.theURL = theURL;
	}

}
