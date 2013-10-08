package org.gambi.tapestry5.cli.data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is merely a container to collect all the ValidationBean objects to
 * be passed to the JSR303 Validation framework
 * 
 * The values of each of the single properties of each contributed beans is
 * provided at runtime after the command line input is parsed.
 * 
 * @author alessiogambi
 * 
 */
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
