package org.gambi.tapestry5.cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ParsingOption {

	public String opt();

	public String longOpt();

	public boolean hasArg() default true;

	public int nArgs() default 1;

	public String description() default "";

	public boolean isRequired() default false;
}