package org.gambi.tapestry5.cli.data;

import javax.validation.Valid;

import org.gambi.tapestry5.cli.annotations.ComplexValidationConstraint;

@ComplexValidationConstraint
public class NestedBean {

	// Force a cascade validation
	@Valid
	// @NotNull
	public BeanA beanA;

	// Force a cascade validation
	@Valid
	public VectorBean vectorBean;

	public NestedBean() {
	}

	public BeanA getBeanA() {
		return beanA;
	}

	public VectorBean getVectorBean() {
		return vectorBean;
	}

	public void setBeanA(BeanA beanA) {
		this.beanA = beanA;
	}

	public void setVectorBean(VectorBean vectorBean) {
		this.vectorBean = vectorBean;
	}
}
