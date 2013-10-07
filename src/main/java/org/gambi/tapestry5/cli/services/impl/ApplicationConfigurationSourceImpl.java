package org.gambi.tapestry5.cli.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.gambi.tapestry5.cli.services.ApplicationConfiguration;
import org.gambi.tapestry5.cli.services.ApplicationConfigurationSource;

public class ApplicationConfigurationSourceImpl implements
		ApplicationConfigurationSource {

	// The properties to be validate... all of them have the validation
	// annotations
	private Map<String, Object> beans;

	// Apparently this cannot be inject
	// private PlasticManager pm;

	public ApplicationConfigurationSourceImpl(Map<String, Object> beans
	// ,PlasticManager pm
	) {
		this.beans = beans;
		// this.pm = pm;
	}

	// Shall we use advice for this ?
	public ApplicationConfiguration get(CommandLine parsedOptions) {
		return new ApplocationConfigurationImpl(this.beans.values());
	}

	protected class ApplocationConfigurationImpl implements
			ApplicationConfiguration {

		private Collection<Object> properties;

		public ApplocationConfigurationImpl(Collection<Object> properties) {
			properties = new ArrayList<Object>();
			this.properties.addAll(properties);
		}

		public void setProperties(Collection<Object> properties) {
			this.properties = properties;
		}

		public Collection<Object> getAllProperties() {
			return properties;
		}
	}
}
