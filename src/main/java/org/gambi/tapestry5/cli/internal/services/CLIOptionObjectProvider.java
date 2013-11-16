package org.gambi.tapestry5.cli.internal.services;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.internal.services.SymbolObjectProvider;
import org.apache.tapestry5.ioc.services.Builtin;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.gambi.tapestry5.cli.annotations.CLIOption;
import org.gambi.tapestry5.cli.services.CLIOptionSource;

/**
 * Performs an injection based on a {@link CLIObject} annotation. This is a
 * replica of the (internal) {@link SymbolObjectProvider} service.
 */
public class CLIOptionObjectProvider implements ObjectProvider {

	private final CLIOptionSource optionSource;

	private final TypeCoercer typeCoercer;

	public CLIOptionObjectProvider(
			@Builtin CLIOptionSource optionSource,
			@Builtin TypeCoercer typeCoercer) {
		this.optionSource = optionSource;
		this.typeCoercer = typeCoercer;
	}

	public <T> T provide(Class<T> objectType,
			AnnotationProvider annotationProvider, ObjectLocator locator) {
		CLIOption annotation = annotationProvider
				.getAnnotation(CLIOption.class);

		if (annotation == null) {
			return null;
		}

		Object value = null;
		if (annotation.name() != null) {

			value = optionSource.valueForOption(annotation.name());
		} else if (annotation.longName() != null) {
			value = optionSource.valueForOption(annotation.longName());
		} else {
			throw new RuntimeException(
					"You must provide either a short or a long name for the CLIOption annotation to be used !");
		}

		IntermediateType it = annotationProvider
				.getAnnotation(IntermediateType.class);

		if (it != null) {
			value = typeCoercer.coerce(value, it.value());
		}

		return typeCoercer.coerce(value, objectType);
	}
}
