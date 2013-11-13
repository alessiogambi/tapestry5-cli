package org.gambi.tapestry5.cli.data;

import javax.validation.constraints.Size;

public class VectorBean {

	@Size(min = 1, max = 4)
	private String[] vector;

	public VectorBean() {
		// TODO Auto-generated constructor stub
	}

	public String[] getVector() {
		return vector;
	}

	public void setVector(String[] vector) {
		this.vector = vector;
	}
}
