package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.console.Config;
import com.github.eostermueller.heapspank.leakyspank.console.DefaultConfig;

public class TestConfig {

	@Test
	public void testStartsWithExclusionFilter() {
		Config c = new DefaultConfig();
		
		c.setRegExExclusionFilter("(java.lang.String|java.lang.Object)");
		
		ClassNameFilter filterItems = c.getClassNameExclusionFilter();
		
		
		assertTrue( filterItems.accept("java.lang.String") );
		assertTrue( filterItems.accept("java.lang.Object") );
		assertFalse( filterItems.accept("bar") );
		
		
	}

}
