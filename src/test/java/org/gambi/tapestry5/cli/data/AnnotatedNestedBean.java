package org.gambi.tapestry5.cli.data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gambi.tapestry5.cli.annotations.ParsingOption;

public class AnnotatedNestedBean {

	// Force a cascade validation
	@Valid
	@NotNull(message = "Bean A null")
	public BeanA beanA;

	// Force a cascade validation == NOTE Without set/get this cannot be
	// evaluated by the library because this is not a valid Bean property
	@Valid
	@NotNull(message = "Annotated been null")
	public AnnotatedBean annotatedBean;

	// NOTE IF theta has no get/set method it will not be evaluated !
	@NotNull(message = "theta is null")
	@ParsingOption(opt = "tt", longOpt = "pappa", hasArg = true)
	public String theta;

	// NOTE IF zeta has no get/set method it will not be evaluated !
	@Size(min = 5)
	@ParsingOption(opt = "z", longOpt = "zeta-option", hasArg = true, isRequired = true)
	public String zeta;

	public AnnotatedNestedBean() {
	}

	public AnnotatedBean getAnnotatedBean() {
		System.out.println("AnnotatedNestedBean.getAnnotatedBean()");
		return annotatedBean;
	}

	public void setAnnotatedBean(AnnotatedBean annotatedBean) {
		System.out.println("AnnotatedNestedBean.setAnnotatedBean()");
		this.annotatedBean = annotatedBean;
	}

	public BeanA getBeanA() {

		System.out.println("NestedBean.getBeanA()");
		return beanA;
	}

	public void setBeanA(BeanA beanA) {
		System.out.println("NestedBean.setBeanA()");
		this.beanA = beanA;
	}

	public String getTheta() {
		return theta;
	}

	public String getZeta() {
		return zeta;
	}

	public void setTheta(String theta) {
		System.out.println("AnnotatedNestedBean.setTheta()");
		this.theta = theta;
	}

	public void setZeta(String zeta) {
		System.out.println("AnnotatedNestedBean.setZeta()");
		this.zeta = zeta;
	}

}
