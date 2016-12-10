package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.console.CommandLineParameterException;
import com.github.eostermueller.heapspank.leakyspank.console.Config;
import com.github.eostermueller.heapspank.leakyspank.console.DefaultConfig;

public class TestCustomConfigClass {

	@Test
	public void test() throws CommandLineParameterException {

		String args[] = { "-config", "com.github.eostermueller.heapspank.leakyspank.TestConfig" };
		Config testConfig = DefaultConfig.createNew(args);
		assertEquals("Could not load test configuration class from classpath", 99, testConfig.getScreenRefreshIntervalSeconds());	
		
	}

	@Test
	public void testConfigClassNameNotInClasspath()  {

		String nameOfClassThatDoesNotExist = "com.github.eostermueller.heapspank.leakyspank.DoesNotExist";
		String args[] = { 
				"-config", 
				nameOfClassThatDoesNotExist 
				};
		
		
		try {
			DefaultConfig.createNew(args);
			fail("Should have thrown an exception because the class name after -config parm does not exist.");
		} catch (CommandLineParameterException e) {
			assertEquals( 
					"Could not find right message", 
					"Unable to create [" + e.getProposedConfigClassName() + "].  Not in the classpath?",
					e.getMessage() );
			assertEquals(nameOfClassThatDoesNotExist, e.getProposedConfigClassName());
			assertEquals("did not find correct cause", ClassNotFoundException.class, e.getCause().getClass());
		}
		
	}
	@Test
	public void testMissingClassName()  {

		String args[] = { 
				"-config", 
				/* "com.github.eostermueller.heapspank.leakyspank.TestConfig" */ 
				};
		
		Config testConfig;
		try {
			testConfig = DefaultConfig.createNew(args);
			fail("Should have thrown an exception because the class was missing as a command line argument.");
		} catch (CommandLineParameterException e) {
			assertEquals( 
					"Could not find right message", 
					"parameter after -config must be name of a class that implements com.github.eostermueller.heapspank.leakyspank.console.Config",
					e.getMessage() );
			assertNull(e.getProposedConfigClassName());
		}
	}
	@Test
	public void testClassNameWithoutCorrectImplementation()  {

	
		//The correct interface is com.github.eostermueller.heapspank.leakyspank.console.Config
		String nameOfClassThatDoesNotImplementCorrectInterface = "com.github.eostermueller.heapspank.leakyspank.TestCustomConfigClass";
		String args[] = { 
				"-config", 
				nameOfClassThatDoesNotImplementCorrectInterface 
				};
		
		try {
			Config testConfig = DefaultConfig.createNew(args);
			fail("Should have thrown an exception because the class name after -config parm does not exist.");
		} catch (CommandLineParameterException e) {
			assertEquals( 
					"Could not find right message", 
					"The -config class [" + e.getProposedConfigClassName() + "] must implement com.github.eostermueller.heapspank.leakyspank.console.Config",
					e.getMessage() );
			assertEquals(nameOfClassThatDoesNotImplementCorrectInterface, e.getProposedConfigClassName());
			assertNull("did not find correct cause",  e.getCause());
		}
		
	}
}
