package org.gambi.tapestry5.cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.gambi.tapestry5.cli.validators.URLConstraintValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Constraint(validatedBy = URLConstraintValidator.class)
public @interface ValidURL {

	public abstract String message() default "{org.gambi.tapestry5.cli.validators.ValidURL.message}";

	public abstract Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
