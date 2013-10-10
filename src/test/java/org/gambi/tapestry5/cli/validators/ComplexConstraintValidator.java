package org.gambi.tapestry5.cli.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.gambi.tapestry5.cli.annotations.ComplexValidationConstraint;
import org.gambi.tapestry5.cli.data.NestedBean;

public class ComplexConstraintValidator implements
		ConstraintValidator<ComplexValidationConstraint, NestedBean> {

	public void initialize(ComplexValidationConstraint object) {
		// TODO Auto-generated method stub

	}

	public boolean isValid(NestedBean bean,
			ConstraintValidatorContext paramConstraintValidatorContext) {

		try {
			System.out.println("ComplexConstraintValidator.isValid() ? ");
			System.out.println(bean.getBeanA().getBeta().length() + "+"
					+ bean.getVectorBean().getVector()[0].length());
			return (bean.getBeanA().getBeta() + bean.getVectorBean()
					.getVector()[0]).length() < 20;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
