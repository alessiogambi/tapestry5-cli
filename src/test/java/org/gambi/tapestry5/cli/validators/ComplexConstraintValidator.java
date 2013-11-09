package org.gambi.tapestry5.cli.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.gambi.tapestry5.cli.annotations.ComplexValidationConstraint;
import org.gambi.tapestry5.cli.data.NestedBean;
import org.gambi.tapestry5.cli.services.ApplicationConfigurationSource;

/**
 * This class is an example of implementation of a complex constraint specified
 * over a NestedBean. The constrain applies to objects of the type:
 * {@link NestedBean}.
 * 
 * In this example, the constraint check that the length of the beta property
 * inside the bean BeanA summed to the length of the first element of the
 * 'vector' property of the VectorBean is less than 20.
 * 
 * The take away message here is that we can define constraints on multiple
 * properties as long as they are nested in the (same) application configuration
 * object. In this case, an instance of the NestedBean must be contributed to
 * {@link ApplicationConfigurationSource} in order for the library to give its'
 * properties the values and to check for their validity
 * 
 * @author alessiogambi
 * 
 */
public class ComplexConstraintValidator implements
		ConstraintValidator<ComplexValidationConstraint, NestedBean> {

	public void initialize(ComplexValidationConstraint object) {
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
