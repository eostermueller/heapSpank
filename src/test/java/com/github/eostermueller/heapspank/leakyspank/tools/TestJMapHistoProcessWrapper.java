package com.github.eostermueller.heapspank.leakyspank.tools;

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JvmAttachException;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.util.IOUtil;

public class TestJMapHistoProcessWrapper {

	@Test
	public void test() throws Exception {
		
		JMapHisto wrapper = new JMapHistoProcessWrapper("" + IOUtil.getMyPid() );
		
		String histo = wrapper.heapHisto(true);
		
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("num"));
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("#instances"));
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("#bytes"));
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("class name"));
		assertTrue("Could not find this test class in jmap -histo output", histo.contains(TestJMapHistoProcessWrapper.class.getName()));
		
	}
	@Test
	public void testInvalidPid() throws InterruptedException, JvmAttachException {
		long pidThatDoesNotExist = 999999;
		
		JMapHisto histo = null;
		try {
			histo = new JMapHistoProcessWrapper(""+pidThatDoesNotExist);
			histo.selfTest();
			fail("Bad pid should result in exception");
		} catch (ProcessIdDoesNotExist jmhe) {
			// Got the exception we were expecting.
			assertEquals("Could not find specified pid", ""+pidThatDoesNotExist, jmhe.getProcessId());
		} catch (JMapHistoException jmhe) {
			// Got the exception we were expecting.
			assertEquals("Could not find specified pid", ""+pidThatDoesNotExist, jmhe.getProcessId());
		}
//		catch (ProcessIdDoesNotExist e) {
//			// Got the exception we were expecting.
//			assertEquals("Could not find specified pid", ""+pidThatDoesNotExist, e.getProcessId());
//			assertEquals("Didn't find right cause for bad pid", "java.lang.reflect.InvocationTargetException", e.getCause().getClass().getName());
//		}
		
	}

	@Test
	public void testTheSelfTest() {
		
		JMapHisto wrapper = new JMapHistoProcessWrapper("" + IOUtil.getMyPid() );
		
		try {
			wrapper.selfTest();
		} catch (JMapHistoException e) {
			fail("Unable to run jmapHisto by executing the jmap process on the file system that comes with the jdk.");
		}
	}

}
