package org.gambi.tapestry5.cli.data;

import javax.validation.constraints.Min;

public class BeanFOO {

	@Min(value = 10)
	private int alfa;

	public int getAlfa() {
		return alfa;
	}

	public void setAlfa(int alfa) {
		this.alfa = alfa;
	}
}
