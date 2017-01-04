package com.github.eostermueller.heapspank.leakyspank.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.github.eostermueller.heapspank.leakyspank.ClassNameFilter;
import com.github.eostermueller.heapspank.leakyspank.JvmAttachException;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.tools.JMapHisto;
import com.github.eostermueller.heapspank.util.GroupNameThreadFactory;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;


public class JMapHistoRunner implements Runnable {
	
	ClassNameFilter classNameExclusionFilter = null;
	private JMapHisto jMapHisto = null; 
	private static int MAX_NUM_ERROR_LINES = 20;
	long startTimestampOfLastSuccess = -1;
	AtomicLong successfulExecutionCount = new AtomicLong(0);
	AtomicLong failedExecutionCount = new AtomicLong(0);
	GroupNameThreadFactory threadFactory = null;
	ScheduledExecutorService jmapHistoScheduler = null;
	private LimitedSizeQueue<String> exceptionText = new LimitedSizeQueue<String>(MAX_NUM_ERROR_LINES);
	
	int intervalInSeconds = -2;
	private Queue<Model> outputQueue = null;
	private boolean jmapHistoLive;
	public boolean getJMapHistoLive() {
		return jmapHistoLive;
	}
	public void setJMapHistoLive(boolean b) {
		jmapHistoLive = b;
	}

	
	public String getExceptionText() {
		StringBuilder sb = new StringBuilder();
			
		synchronized (this.exceptionText) {
			Iterator<String> listIterator = this.exceptionText.iterator();
			while (listIterator.hasNext())
				sb.append(listIterator.next());
		}
		return sb.toString();
	}
	public long getSuccessfulCount() {
		return this.successfulExecutionCount.longValue();
	}
	public long getFailedCount() {
		return this.failedExecutionCount.longValue();
	}
	public JMapHistoRunner(
			JMapHisto jmapHisto, 
			int intervalInSeconds, 
			Queue<Model> outputQueue, 
			ClassNameFilter classNameFilter, 
			boolean jmapHistoLive) throws JvmAttachException {
		
		this.classNameExclusionFilter = classNameFilter;
		this.setJMapHisto(jmapHisto);
		
		this.intervalInSeconds = intervalInSeconds;
		this.outputQueue = outputQueue;
		this.threadFactory = new GroupNameThreadFactory(
				"leakySpankJMapHistoThread");
		this.jmapHistoScheduler = Executors.newScheduledThreadPool(1,
				threadFactory);
		
		this.setJMapHistoLive(jmapHistoLive);
	}

	public void shutdown() {
		this.jmapHistoScheduler.shutdown();
		try {
			this.jMapHisto.shutdown();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public void launchJMapHistoExecutor() {
		jmapHistoScheduler.scheduleAtFixedRate(this, 0, this.intervalInSeconds, TimeUnit.SECONDS);
	}

	
	/**
	 * @stolenfrom https://github.com/arturmkrtchyan/sizeof4j/blob/master/src/main/java/com/arturmkrtchyan/sizeof4j/calculation/hotspot/HotSpotHistogram.java 
	 * 
	 */
	@Override
	public void run() {

		long start = -1;
//        VirtualMachine vm = null;
		try {
			start = System.currentTimeMillis();

			/**
			 * returns a big String formatted like this:
			 * https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH
			 * 
			 */
			
			String histo = this.getJMapHisto().heapHisto( this.getJMapHistoLive() );

        	Model m = new Model(histo, this.classNameExclusionFilter);
			this.outputQueue.add(m);
			this.successfulExecutionCount.incrementAndGet();
			this.startTimestampOfLastSuccess = start;
		} catch (Throwable e) {
			Writer result = new StringWriter();
		    PrintWriter printWriter = new PrintWriter(result);
		    printWriter.println("Error executing HotSpotVirtualMachine.heapHist() for pid [" + this.getJMapHisto().getPid() + "] at time [" + start + "]");
		    printWriter.println(e.getMessage() + " " + e.getClass().getName() + " Cause:" + e.getCause().getMessage() + " " + e.getCause().getClass().getName() );
		    
		    //Some verbose log should get this full detail:
			//e.printStackTrace(printWriter);
			this.exceptionText.add(result.toString());
			this.failedExecutionCount.incrementAndGet();
		} 
	}
	public JMapHisto getJMapHisto() {
		return jMapHisto;
	}
	public void setJMapHisto(JMapHisto jMapHisto) {
		this.jMapHisto = jMapHisto;
	}
}
