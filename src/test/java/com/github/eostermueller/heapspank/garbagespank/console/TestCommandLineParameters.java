package com.github.eostermueller.heapspank.garbagespank.console;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCommandLineParameters {

	@Test
	public void testMilliSeconds() {
		
		String[] args = { "-i", "5000"};
		CommandLineParameters clp = new CommandLineParameters(args);

		assertEquals("Unexpectedly found errors while parsing", 0, clp.getErrors().size());
		assertEquals("command line parm for interval is wrong", 5000, clp.getIntervalInMilliseconds());
		
	}

	@Test
	public void testSeconds() {
		
		String[] args = { "-i", "5s"};
		CommandLineParameters clp = new CommandLineParameters(args);
		assertEquals("Unexpectedly found errors while parsing", 0, clp.getErrors().size());
		
		assertEquals("command line parm for interval is wrong", 5000, clp.getIntervalInMilliseconds());
		
	}
	@Test
	public void testTwoDigitSeconds() {
		
		String[] args = { "-i", "60s"};
		CommandLineParameters clp = new CommandLineParameters(args);
		assertEquals("Unexpectedly found errors while parsing", 0, clp.getErrors().size());
		
		assertEquals("command line parm for interval is wrong", 60000, clp.getIntervalInMilliseconds());
	}
	@Test
	public void testZeroArgs() {
		
		String[] args = { };
		CommandLineParameters clp = new CommandLineParameters(args);
		assertEquals("Should have found an error for invalid parameters", 1, clp.getErrors().size());
		
	}
	@Test
	public void testOneArg() {
		
		String[] args = { "-i" };
		CommandLineParameters clp = new CommandLineParameters(args);
		assertEquals("Should have found an error for invalid parameters", 1, clp.getErrors().size());
		
	}
	@Test
	public void testMissingDashI() {
		
		String[] args = { "5" };
		CommandLineParameters clp = new CommandLineParameters(args);
		assertEquals("Should have found an error for invalid parameters", 1, clp.getErrors().size());
	}
}
