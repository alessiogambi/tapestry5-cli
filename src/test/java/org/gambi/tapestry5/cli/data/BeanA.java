package org.gambi.tapestry5.cli.data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BeanA {

	@NotNull
	@Min(value = 1)
	private Integer delta;

	@Min(value = 10)
	private long epsilon;

	@Size(min = 3, max = 5, message = "Legnth of beta Must be between 3 and 5")
	private String beta;

	@NotNull
	private String gamma;

	public BeanA() {
	}

	public String getBeta() {
		return beta;
	}

	public Integer getDelta() {
		return delta;
	}

	public long getEpsilon() {
		return epsilon;
	}

	public String getGamma() {
		return gamma;
	}

	public void setBeta(String beta) {
		this.beta = beta;
	}

	public void setDelta(Integer delta) {
		this.delta = delta;
	}

	public void setEpsilon(long epsilon) {
		this.epsilon = epsilon;
	}

	public void setGamma(String gamma) {
		this.gamma = gamma;
	}
}
