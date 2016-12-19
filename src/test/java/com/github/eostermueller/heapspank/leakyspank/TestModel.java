package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.console.CommandLineParameterException;
import com.github.eostermueller.heapspank.leakyspank.console.Config;
import com.github.eostermueller.heapspank.leakyspank.console.DefaultConfig;

public class TestModel {
	private static final String TEST_LINE_01 = " 3514: 1 16 sun.reflect.GeneratedMethodAccessor8";
	private static final String TEST_LINE_02 = " 53555: 6 55191 org.acme.GeneratedMethodAccessor8";
	

    private static final String JMAP_HISTO_STDOUT_01 = 
    		" num     #instances         #bytes  class name\n"
          + "----------------------------------------------\n"
          + "   1:         71149       40150192  [B\n"
          + "   2:        109777       30225864  [C\n"
          + "   3:          6128        8916144  [I\n"
          + "   4:        105381        2529144  java.lang.String\n"
          + "   5:         22299        2497488  com.acme.SocksSocketImpl\n"
          + "3541:             1             16  sun.util.resources.LocaleData$LocaleDataResourceBundleControl\n"
          + "Total        940658      109766232";
	
    /**
     * This is a variaant of the above JMAP_HISTO_STDOUT_01.
     * The class com.acme.SocksSocketImpl has many more bytes,
     * and the 'num' rankings have been adjusted to reflect the change.
     */
    private static final String JMAP_HISTO_STDOUT_02 = 
    		" num     #instances         #bytes  class name\n"
          + "----------------------------------------------\n"
          + "   1:         71149       40150192  [B\n"
          + "   2:         22299       40150000  com.acme.SocksSocketImpl\n"
          + "   3:        109777       30225864  [C\n"
          + "   4:          6128        8916144  [I\n"
          + "   5:        105381        2529144  java.lang.String\n"
          + "3541:             1             16  sun.util.resources.LocaleData$LocaleDataResourceBundleControl\n"
          + "Total        940658      109766232";
	@Test
	public void testSingleLine() {
		Model m = new Model(JMAP_HISTO_STDOUT_01);
		
		JMapHistoLine l = m.get("com.acme.SocksSocketImpl");
		assertNotNull("Did not find JMap Histo row with given class name", l);
		assertEquals("Did not find correct num", 5, l.num);
		assertEquals("Did not find correct #instances", 22299, l.instances);
		assertEquals("Did not find correct #bytes",2497488,  l.bytes);
		assertEquals("Did not find correct className", "com.acme.SocksSocketImpl", l.className);
	}
	@Test
	public void testRoughCheckForAllLines_noFilter() {
		
		Model m1 = new Model(JMAP_HISTO_STDOUT_01, null);//This ctor uses no 'exclude filter', and thereby returns all the lines.
		
		int intNum_sum = 0;
		long longInstances_sum = 0;
		long longBytes_sum = 0;
		for(JMapHistoLine l : m1.getAllOrderByBytes()) {
			intNum_sum += l.num;
			longInstances_sum += l.instances;
			longBytes_sum += l.bytes;
		}
		assertEquals("Sum of all the 'num' values was not right.",3556,intNum_sum);
		assertEquals("Sum of all the 'instances' values was not right",314735,longInstances_sum);
		assertEquals("Sum of all the 'bytes' values was not right",84318848,longBytes_sum);
	}
	@Test
	public void testClassNames_withoutFilter() {
		boolean ynFound = false;

		Model modelWithString = new Model(JMAP_HISTO_STDOUT_01 );// zero filter
		for(JMapHistoLine l : modelWithString.getAllOrderByBytes()) {
			
			ynFound = l.className.startsWith("java.lang.String");
			if (ynFound)
				break;
		}
		assertTrue("Whoops...should have found this class -- there were no filters to exclude it.", ynFound);

	}
	@Test
	public void testClassNames_withFilter() throws CommandLineParameterException {
		boolean ynFound = true;
		
		Config c = new DefaultConfig();
		c.setArgs( new String[]{ "123" });
		
		Model modelWithOutString = new Model(JMAP_HISTO_STDOUT_01, c.getClassNameExclusionFilter() );//This ctor assigns a default filter and excludes some of the lines.
		for(JMapHistoLine l : modelWithOutString.getAllOrderByBytes()) {
			
			ynFound = l.className.startsWith("java.lang.String");
			if (ynFound)
				break;
			
		}
		assertFalse("Whoops...found package.class starting with java and we thought we had excluded it", ynFound);
	}
	@Test
	public void testClassNames_withFilterButNoMatchUsingConfigOverride() throws CommandLineParameterException {
		boolean ynFound = true;
		
		Config c = new DefaultConfig();
		c.setArgs( new String[]{ "123" });
		
		//This is where we override the criteria -- note that String is no longer here.
		c.setRegExExclusionFilter("(java.lang.Object|java.lang.Long)");
		
		Model modelWithString = new Model(JMAP_HISTO_STDOUT_01, c.getClassNameExclusionFilter() );//This ctor assigns a default filter and excludes some of the lines.
		for(JMapHistoLine l : modelWithString.getAllOrderByBytes()) {
			
			ynFound = l.className.startsWith("java.lang.String");
			if (ynFound)
				break;
		}
		assertTrue("Whoops...should have found this class -- there was  a filter but no match to exclude it.", ynFound);
	}
	@Test
	@Ignore
	public void testRoughCheckForAllLines_withFilter() {
		
		Model m1 = new Model(JMAP_HISTO_STDOUT_01);//This ctor assigns a default filter and excludes some of the lines.
		
		int intNum_sum = 0;
		long longInstances_sum = 0;
		long longBytes_sum = 0;
		for(JMapHistoLine l : m1.getAllOrderByBytes()) {
			intNum_sum += l.num;
			longInstances_sum += l.instances;
			longBytes_sum += l.bytes;
		}
		assertEquals("Sum of all the 'num' values was not right.",3546,intNum_sum);
		assertEquals("Sum of all the 'instances' values was not right",22300,longInstances_sum);
		assertEquals("Sum of all the 'bytes' values was not right",2497504,longBytes_sum);
	}
	
//	/**
//	 * The class that moved up the most in the "num" field ranking should be sorted to the very top.
//	 */
//	@Test
//	public void testUpwardMobility() {
//		
//		Model m1 = new Model(JMAP_HISTO_STDOUT_01);
//		Model m2 = new Model(JMAP_HISTO_STDOUT_02);
//		
//		JMapHistoLine[] all = m2.getAll();
//		assertNotEquals("Sanity check. This is a no brainer, just right class (com.acme.SocksSocketImpl) isn't inadvertently put into the right position [0]",
//				"com.acme.SocksSocketImpl", 
//				all[all.length-1].className);
//		
//		all = m2.getAllOrderByMostUpwardlyMobileAsComparedTo(m1);
//		assertEquals("The class that increased the most in size did not get sorted to the bottom as expected",
//				"com.acme.SocksSocketImpl", 
//				all[all.length-1].className);
//		
//	}
	@Test
	public void canAddToEmptyModel() {
		Model m = new Model();
		JMapHistoLine jmhl_01 = new JMapHistoLine(TEST_LINE_01);
		JMapHistoLine jmhl_02 = new JMapHistoLine(TEST_LINE_02);
		
		m.put(jmhl_01);
		m.put(jmhl_02);
		
		JMapHistoLine[] all = m.getAll();
		assertEquals("We added two to the model, but did not get two from Model#getAll", 2, all.length);
		
	}
	@Test
	public void canAddToEmptyModelAndRender() {
		Model m = new Model();
		JMapHistoLine jmhl_01 = new JMapHistoLine(TEST_LINE_01);
		JMapHistoLine jmhl_02 = new JMapHistoLine(TEST_LINE_02);
		
		m.put(jmhl_01);
		m.put(jmhl_02);
		String expectedResult = "bar_sun.reflect.GeneratedMethodAccessor8_bytes=16<BR>\n"
				+ "bar_org.acme.GeneratedMethodAccessor8_bytes=55191<BR>\n";
		
		String all = m.renderBytes("bar");
		assertEquals("We added two to the model, but did not get two from Model#renderNum", expectedResult, all);

		
	}
	@Test
	public void testRoughCheckForAllLinesRendered_noFilter() {
		
		Model m1 = new Model(JMAP_HISTO_STDOUT_01, null);//This ctor uses no 'exclude filter', and therby returns all the lines.
		String rendered = m1.renderBytes("foo");
		
		StringBuilder sbExpected = new StringBuilder();
		sbExpected.append("foo_[B_bytes=40150192<BR>\n");
		sbExpected.append("foo_[C_bytes=30225864<BR>\n");
		sbExpected.append("foo_[I_bytes=8916144<BR>\n");
		sbExpected.append("foo_java.lang.String_bytes=2529144<BR>\n");
		sbExpected.append("foo_com.acme.SocksSocketImpl_bytes=2497488<BR>\n");
		sbExpected.append("foo_sun.util.resources.LocaleData$LocaleDataResourceBundleControl_bytes=16<BR>\n");
		
		
		assertEquals("Did not find right rendered output", sbExpected.toString(), rendered);

	}
}
