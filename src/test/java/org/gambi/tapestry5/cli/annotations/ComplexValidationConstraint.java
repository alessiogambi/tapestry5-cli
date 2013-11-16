package org.gambi.tapestry5.cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.gambi.tapestry5.cli.validators.ComplexConstraintValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Constraint(validatedBy = ComplexConstraintValidator.class)
public @interface ComplexValidationConstraint {

	public abstract String message() default "The Complex Constraint was not valid";

	public abstract Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
