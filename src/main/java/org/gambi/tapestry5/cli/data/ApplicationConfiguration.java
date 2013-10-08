package org.gambi.tapestry5.cli.data;

import java.util.ArrayList;
import java.util.Collection;

public class ApplicationConfiguration {

	private Collection<Object> properties;

	public ApplicationConfiguration(Collection<Object> properties) {
		this.properties = new ArrayList<Object>();
		this.properties.addAll(properties);
	}

	public void setProperties(Collection<Object> properties) {
		this.properties = properties;
	}

	public Collection<Object> getAllProperties() {
		return properties;
	}
}
