package org.gambi.tapestry5.cli.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.tapestry5.ioc.annotations.AnnotationUseContext.SERVICE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.tapestry5.ioc.annotations.UseWith;

/**
 * Used to inject the value of an option passed on the CLI, via a reference to
 * it (short name, long name).
 * <p/>
 * The injected value may be coerced from string to an alternate type (defined
 * by the field or parameter to which the @CLIOption annotation is attached).
 * 
 * @CLIOption mimics @Symbol but applies only to Services
 */
@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
@UseWith({ SERVICE })
public @interface CLIOption {
	/**
	 * The name of the option to inject.
	 */
	String name();

	/**
	 * The long name of the option to inject.
	 */
	String longName();
}
