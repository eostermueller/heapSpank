package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.jmeter.LeakySpankSampler;


public class TestSingleJMapHistoLine {

	/**
	 * Note that spaces have been minimized
	 */
	private static final String TEST_LINE = " 3514: 1 16 sun.reflect.GeneratedMethodAccessor8";

	@Test
	public void canParseSingleLineOfJMapHisto() {
		JMapHistoLine jmhl = new JMapHistoLine(TEST_LINE);
		
		assertEquals("Did not parse the 'num' correctly", 3514, jmhl.num);
		assertEquals("Did not parse the 'instances' correctly", 1, jmhl.instances);
		assertEquals("Did not parse the 'instances' correctly", 16, jmhl.bytes);
		assertEquals("Did not parse the 'class name' correctly", "sun.reflect.GeneratedMethodAccessor8", jmhl.className);
		
	}
	@Test
	public void canRenderSingleLine() {
		JMapHistoLine jmhl = new JMapHistoLine(TEST_LINE);
		
		String label = jmhl.getBytesGraphLabel("foo");
		assertEquals("Expected to render this single line of jmap -histo to JMeterPlugins-friendly format. ", "foo_sun.reflect.GeneratedMethodAccessor8_bytes=16<BR>\n", label);
		
	}

}
