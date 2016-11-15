package com.github.eostermueller.heapspank.leakyspank.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.util.ExecutableNotFound;
import com.github.eostermueller.heapspank.util.GroupNameThreadFactory;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;


public class JMapHistoRunner implements Runnable {
	private static int MAX_NUM_ERROR_LINES = 5000;
	long startTimestampOfLastSuccess = -1;
	AtomicLong successfulExecutionCount = new AtomicLong(0);
	AtomicLong failedExecutionCount = new AtomicLong(0);
	GroupNameThreadFactory threadFactory = null;
	ScheduledExecutorService jmapHistoScheduler = null;
	private LimitedSizeQueue<String> exceptionText = new LimitedSizeQueue<String>(MAX_NUM_ERROR_LINES);
	long pid = -1;
	int intervalInSeconds = -2;
	private Queue<Model> outputQueue = null;
	/** Assume this is in the path...obviously could use enhancement
	 *  to check in JAVA_HOME
	 */
	private String commandPath = "jmap";

	
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
	public JMapHistoRunner(long pid, int intervalInSeconds, Queue<Model> outputQueue) {
		this.pid = pid;
		this.intervalInSeconds = intervalInSeconds;
		this.outputQueue = outputQueue;
		this.threadFactory = new GroupNameThreadFactory(
				"leakySpankJMapHistoThread");
		this.jmapHistoScheduler = Executors.newScheduledThreadPool(1,
				threadFactory);
	}

	public void shutdown() {
		this.jmapHistoScheduler.shutdown();
	}

	public void launchJMapHistoExecutor() {
		jmapHistoScheduler.scheduleAtFixedRate(this, 0, this.intervalInSeconds, TimeUnit.SECONDS);
	}

	public void setCommandPath(String s) {
		this.commandPath = s;
	}
	
	public String getCommandPath() {
		return this.commandPath;
	}
	
	@Override
	public void run() {

		List<String> processArgs = new ArrayList<String>();

		processArgs.add(this.getCommandPath());
		processArgs.add("-histo");
		processArgs.add("" + this.pid); // process id

		ProcessBuilder processBuilder = new ProcessBuilder(processArgs);
		processBuilder.redirectErrorStream(true);

		Process process;
		long start = -1;
		try {
			start = System.currentTimeMillis();
			process = processBuilder.start();
			StringBuilder processOutput = new StringBuilder();

			BufferedReader processOutputReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String readLine;

			int lineCount = 0;
			while ((readLine = processOutputReader.readLine()) != null) {
				if (lineCount > 0)
					processOutput.append(readLine + System.lineSeparator());
				lineCount++;
			}
			process.waitFor();
			Model m = new Model(processOutput.toString());
			this.outputQueue.add(m);
			this.successfulExecutionCount.incrementAndGet();
			this.startTimestampOfLastSuccess = start;
		} catch (IOException e) {
			Writer result = new StringWriter();
		    PrintWriter printWriter = new PrintWriter(result);			
			e.printStackTrace(printWriter);
			this.exceptionText.add(result.toString());
			
			this.failedExecutionCount.incrementAndGet();
			if (e.getMessage().contains("No such file or directory")) {
				ExecutableNotFound  enf = new ExecutableNotFound();
				enf.setExecutableName(this.getCommandPath());
				enf.setException(e);
				throw enf;
			}
		} catch (InterruptedException e) {
			Writer result = new StringWriter();
		    PrintWriter printWriter = new PrintWriter(result);			
			e.printStackTrace(printWriter);
			this.exceptionText.add(result.toString());
			this.failedExecutionCount.incrementAndGet();
		}

	}
}
