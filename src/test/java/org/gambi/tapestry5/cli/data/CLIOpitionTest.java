package org.gambi.tapestry5.cli.data;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CLIOpitionTest {

	private ArrayList<CLIOption> options;

	@Before
	public void createObjects() {
		options = new ArrayList<CLIOption>();
	}

	@Test
	public void containsTest() {
		CLIOption o1 = new CLIOption("1", "bb", 0, true, "description");
		CLIOption o2 = new CLIOption("3", "dasdsabb", 0, true, "description");
		CLIOption o3 = new CLIOption("1", "bb", 0, true, "bibi description");

		options.add(o1);
		options.add(o2);

		Assert.assertTrue(options.contains(o1));
		Assert.assertTrue(options.contains(o3));

	}

	@Test
	public void equalsTest() {

		CLIOption o1 = new CLIOption("1", "bb", 0, true, "description");
		CLIOption o3 = new CLIOption("1", "bb", 0, true, "bibi description");

		Assert.assertTrue(o1.equals(o3));
		Assert.assertTrue(o3.equals(o1));

	}
}
