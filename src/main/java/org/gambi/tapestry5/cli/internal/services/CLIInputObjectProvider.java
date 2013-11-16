package org.gambi.tapestry5.cli.internal.services;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ObjectProvider;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.internal.services.SymbolObjectProvider;
import org.apache.tapestry5.ioc.services.Builtin;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.gambi.tapestry5.cli.annotations.CLIInput;
import org.gambi.tapestry5.cli.services.CLIOptionSource;

/**
 * Performs an injection based on a {@link CLIInput} annotation. This is a
 * replica of the (internal) {@link SymbolObjectProvider} service.
 */
public class CLIInputObjectProvider implements ObjectProvider {

	private final CLIOptionSource optionSource;

	private final TypeCoercer typeCoercer;

	public CLIInputObjectProvider(@Builtin CLIOptionSource optionSource,
			@Builtin TypeCoercer typeCoercer) {
		this.optionSource = optionSource;
		this.typeCoercer = typeCoercer;
	}

	public <T> T provide(Class<T> objectType,
			AnnotationProvider annotationProvider, ObjectLocator locator) {

		CLIInput annotation = annotationProvider.getAnnotation(CLIInput.class);

		if (annotation == null) {
			return null;
		}

		if (annotation.position() < 0) {
			throw new ArrayIndexOutOfBoundsException(
					"You must provide a valid index for the CLIInput annotation to be used !");
		}

		Object value = optionSource.valueForInput(annotation.position());

		IntermediateType it = annotationProvider
				.getAnnotation(IntermediateType.class);

		if (it != null) {
			value = typeCoercer.coerce(value, it.value());
		}

		return typeCoercer.coerce(value, objectType);
	}
}
