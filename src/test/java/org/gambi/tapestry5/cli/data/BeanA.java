package org.gambi.tapestry5.cli.data;

import java.net.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BeanA {

	@Size(min = 3, max = 5, message = "Legnth of beta Must be between 3 and 5")
	private String beta;

	@NotNull
	private String gamma;
	@NotNull
	@Min(value = 1)
	private Integer delta;

	@Min(value = 10)
	private long epsilon;

	// @ValidURL
	private URL url;

	@NotNull
	// @ValidURL
	private String stringUrl;

	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public void setStringUrl(String stringUrl) {
		this.stringUrl = stringUrl;
	}

	public String getStringUrl() {
		return stringUrl;
	}

	public void setEpsilon(long epsilon) {
		System.out.println("BeanA.setEpsilon() " + epsilon);
		this.epsilon = epsilon;
	}

	public long getEpsilon() {
		System.out.println("BeanA.getEpsilon() " + epsilon);
		return epsilon;
	}

	public void setDelta(Integer delta) {
		this.delta = delta;
	}

	public Integer getDelta() {
		return delta;
	}

	public BeanA() {
	}

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
