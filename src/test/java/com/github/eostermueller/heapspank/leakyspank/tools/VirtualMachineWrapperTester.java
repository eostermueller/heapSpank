package com.github.eostermueller.heapspank.leakyspank.tools;

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JvmAttachException;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.tools.VirtualMachineWrapper;
import com.github.eostermueller.heapspank.util.IOUtil;

public class VirtualMachineWrapperTester {

	@Test
	public void test() throws Exception {
		
		VirtualMachineWrapper wrapper = new VirtualMachineWrapper("" + IOUtil.getMyPid() );
		
		String histo = wrapper.heapHisto(true);
		
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("num"));
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("#instances"));
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("#bytes"));
		assertTrue("Could not find a column header in jmap -histo output", histo.contains("class name"));
		assertTrue("Could not find this test class in jmap -histo output", histo.contains(VirtualMachineWrapperTester.class.getName()));
		
		wrapper.detach();
	}
	@Test
	public void testInvalidPid() throws InterruptedException, JvmAttachException, JMapHistoException {
		//Raw output of JMap -histo <myPid> will go here.  Each queue item is full output from one run.
		Queue<Model> outputQueue = new ConcurrentLinkedQueue<Model>();
		long pidThatDoesNotExist = 999999;
		
		JMapHisto histo = null;
		try {
			histo = new VirtualMachineWrapper(""+pidThatDoesNotExist);
			fail("Bad pid should result in exception");
		} catch (ProcessIdDoesNotExist e) {
			// Got the exception we were expecting.
			assertEquals("Could not find specified pid", ""+pidThatDoesNotExist, e.getProcessId());
			assertEquals("Didn't find right cause for bad pid", "java.lang.reflect.InvocationTargetException", e.getCause().getClass().getName());
		}
		
	}

	@Test
	public void testTheSelfTest() throws ProcessIdDoesNotExist, JMapHistoException  {
		
		JMapHisto wrapper = new VirtualMachineWrapper("" + IOUtil.getMyPid() );
		
		try {
			wrapper.selfTest();
		} catch (JMapHistoException e) {
			fail("Unable to run jmapHisto by executing code in the JDK's tools.jar");
		}
	}
}
