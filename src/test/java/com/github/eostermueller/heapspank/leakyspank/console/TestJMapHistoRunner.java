package com.github.eostermueller.heapspank.leakyspank.console;

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Ignore;
import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.JvmAttachException;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.tools.JMapHisto;
import com.github.eostermueller.heapspank.leakyspank.tools.JMapHistoException;
import com.github.eostermueller.heapspank.leakyspank.tools.ProcessIdDoesNotExist;
import com.github.eostermueller.heapspank.leakyspank.tools.VirtualMachineWrapper;
import com.github.eostermueller.heapspank.util.IOUtil;

public class TestJMapHistoRunner {
	private static final String JMAP_HISTO_COLUMN_HEADERS_1 = "num     #instances         #bytes  class name";
	private static final String JMAP_HISTO_COLUMN_HEADERS_2 = "----------------------------------------------";	

	@Test
	public void testTwoScheduledExecutions() throws InterruptedException, JvmAttachException, ProcessIdDoesNotExist, JMapHistoException {
		
		//Raw output of JMap -histo <myPid> will go here.  Each queue item is full output from one run.
		Queue<Model> outputQueue = new ConcurrentLinkedQueue<Model>();
		
		JMapHisto histo = new VirtualMachineWrapper(""+IOUtil.getMyPid());

		JMapHistoRunner jmapHistoRunner = null;
		try {
			jmapHistoRunner = new JMapHistoRunner(
							histo,
							1,
							outputQueue, null, false);
			
			jmapHistoRunner.launchJMapHistoExecutor();
			Thread.sleep(5000);
		} finally {
			if (jmapHistoRunner!=null)
				jmapHistoRunner.shutdown();
		}
		
		//One executes immediately, the other executes at 1 second.
		//This is faster than I recommend executing to avoid high overhead, 
		//but harmless for unit testing.
		assertTrue("JMap -hist did not run", jmapHistoRunner.getSuccessfulCount() > 2);
		assertEquals("Unexpected JMap -hist failure", 0, jmapHistoRunner.getFailedCount() );

		Model first = outputQueue.poll();
		assertTrue("Found way too few classes in JMap -histo output", first.getAll().length > 20);
		
		
		JMapHistoLine line = first.get(this.getClass().getName());
		assertTrue("Count of bytes for [" + this.getClass().getName() + "] should be greater than 0", line.bytes > 0);
		assertTrue("memory rank for [" + this.getClass().getName() + "] should be greater than 0", line.num > 0 );
		assertEquals("Instance count for [" + this.getClass().getName() + "] was not found", 1, line.instances );
		
		
		Model second = outputQueue.poll();
		line = second.get(this.getClass().getName());
		assertTrue("Found way too few classes in JMap -histo output", second.getAll().length > 20);
		assertTrue("Count of bytes for [" + this.getClass().getName() + "] should be greater than 0", line.bytes > 0);
		assertTrue("memory rank for [" + this.getClass().getName() + "] should be greater than 0", line.num > 0 );
		assertEquals("Instance count for [" + this.getClass().getName() + "] was not found", 1, line.instances );

		
//		Model third = outputQueue.poll();
//		assertNull("only had time to excute 2 JMap -histo.  Why is there a third?",third);
		
		
	}

}
