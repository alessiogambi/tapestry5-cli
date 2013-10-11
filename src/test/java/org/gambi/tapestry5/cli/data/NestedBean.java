package org.gambi.tapestry5.cli.data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.gambi.tapestry5.cli.annotations.ComplexValidationConstraint;

@ComplexValidationConstraint
public class NestedBean {

	// Force a cascade validation
	@Valid
	@NotNull
	public BeanA beanA;

	// Force a cascade validation
	@Valid
	public VectorBean vectorBean;

	public NestedBean() {
	}

	public BeanA getBeanA() {
		// System.out.println("NestedBean.getBeanA()");
		return beanA;
	}

	public VectorBean getVectorBean() {
		// System.out.println("NestedBean.getVectorBean()");
		return vectorBean;
	}

	public void setBeanA(BeanA beanA) {
		// System.out.println("NestedBean.setBeanA()");
		this.beanA = beanA;
	}

	public void setVectorBean(VectorBean vectorBean) {
		// System.out.println("NestedBean.setVectorBean()");
		this.vectorBean = vectorBean;
	}
}
