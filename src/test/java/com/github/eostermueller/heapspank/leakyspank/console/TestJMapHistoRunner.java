package com.github.eostermueller.heapspank.leakyspank.console;

import static org.junit.Assert.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.util.ExecutableNotFound;

public class TestJMapHistoRunner {
	private static final String JMAP_HISTO_COLUMN_HEADERS_1 = "num     #instances         #bytes  class name";
	private static final String JMAP_HISTO_COLUMN_HEADERS_2 = "----------------------------------------------";	

	@Test
	public void testExceptionMessagesAreAvailable() throws InterruptedException {
		//Raw output of JMap -histo <myPid> will go here.  Each queue item is full output from one run.
		Queue<Model> outputQueue = new ConcurrentLinkedQueue<Model>();
		
		JMapHistoRunner jmapHistoRunner = null;
		try {
			jmapHistoRunner = new JMapHistoRunner(
							getMyPid(),
							1,
							outputQueue);
			jmapHistoRunner.setCommandPath("nonExistentExecutableName");
			
			jmapHistoRunner.launchJMapHistoExecutor();
			Thread.sleep(1500);
		} finally {
			jmapHistoRunner.shutdown();
		}
		
		assertEquals("Expected jmap -hist to fail, but stats don't show it", 0, jmapHistoRunner.getSuccessfulCount());
		
		/**
		 * Even though we waited long enough for two to execute
		 * the 2nd one never executes because it threw an ExecutableNotFound exception, which is not trap-able from this a separate thread.
		 */
		assertEquals("Expected jmap -hist to fail, but stats don't show it", 1, jmapHistoRunner.getFailedCount());
		
		assertTrue("Couldn't find details of why jmap didn't run", 
				jmapHistoRunner.getExceptionText()
				.contains("nonExistentExecutableName")); //this text might be JDK/locale specific
		
		
		assertTrue("Couldn't find details of why jmap didn't run", 
				jmapHistoRunner.getExceptionText()
				.contains("No such file or directory")); //this text might be JDK/locale specific
	}
	@Test
	public void testTwoScheduledExecutions() throws InterruptedException {
		
		//Raw output of JMap -histo <myPid> will go here.  Each queue item is full output from one run.
		Queue<Model> outputQueue = new ConcurrentLinkedQueue<Model>();
		
		JMapHistoRunner jmapHistoRunner = null;
		try {
			jmapHistoRunner = new JMapHistoRunner(
							getMyPid(),
							1,
							outputQueue);
			
			jmapHistoRunner.launchJMapHistoExecutor();
			Thread.sleep(1500);
			
		} finally {
			jmapHistoRunner.shutdown();
		}
		
		//One executes immediately, the other executes at 1 second.
		//This is faster than I recommend executing to avoid high overhead, 
		//but harmless for unit testing.
		assertEquals("JMap -hist did not run", 2, jmapHistoRunner.getSuccessfulCount());
		assertEquals("Unexpected JMap -hist failure", 0, jmapHistoRunner.getFailedCount() );

		Model first = outputQueue.poll();
		assertTrue("Found way too few classes in JMap -histo output", first.getAll().length > 20);
		
		
		JMapHistoLine line = first.get(this.getClass().getName());
		assertTrue("Count of bytes for [" + this.getClass().getName() + "] should be greater than 0", line.bytes > 0);
		assertTrue("memory rank for [" + this.getClass().getName() + "] should be greater than 0", line.num > 0 );
		assertEquals("Instance count for [" + this.getClass().getName() + "] was not found", 2, line.instances );
		
		
		Model second = outputQueue.poll();
		line = second.get(this.getClass().getName());
		assertTrue("Found way too few classes in JMap -histo output", second.getAll().length > 20);
		assertTrue("Count of bytes for [" + this.getClass().getName() + "] should be greater than 0", line.bytes > 0);
		assertTrue("memory rank for [" + this.getClass().getName() + "] should be greater than 0", line.num > 0 );
		assertEquals("Instance count for [" + this.getClass().getName() + "] was not found", 2, line.instances );

		
		Model third = outputQueue.poll();
		assertNull("only had time to excute 2 JMap -histo.  Why is there a third?",third);
		
		
	}
	/**
	 * @stolen from http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
	 * @return
	 */
	private static long getMyPid() {
		  String processName =
			      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			    return Long.parseLong(processName.split("@")[0]);		
	}

}
