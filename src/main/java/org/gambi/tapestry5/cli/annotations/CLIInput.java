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
 * Used to inject the value of an input (not an Option!) passed on the CLI, via
 * a reference to its position (starting from 0).
 * <p/>
 * The injected value may be coerced from string to an alternate type (defined
 * by the field or parameter to which the @CLIInput annotation is attached).
 * 
 * @CLIInput is similar to @CLIOption and mimics @Symbol; however, it applies
 *           only to Services
 */
@Target({ PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
@UseWith({ SERVICE })
public @interface CLIInput {
	/**
	 * The name of the option to inject.
	 */
	int position();

}
