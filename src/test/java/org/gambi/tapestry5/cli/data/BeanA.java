package org.gambi.tapestry5.cli.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BeanA {

	public BeanA() {
	}

	@Size(min = 3, max = 5, message = "Legnth of beta Must be between 3 and 5")
	private String beta;

	@NotNull
	private String gamma;

	public void setBeta(String beta) {
		this.beta = beta;
	}

	public String getBeta() {
		return beta;
	}

	public void setGamma(String gamma) {
		this.gamma = gamma;
	}

	public String getGamma() {
		return gamma;
	}
}
