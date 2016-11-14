package com.github.eostermueller.heapspank.garbagespank.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.github.eostermueller.heapspank.garbagespank.GarbageSpank;
import com.github.eostermueller.heapspank.garbagespank.JStatHeaderException;
import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

/**
 * Manages formatting issues with console output from jstat
 * @author erikostermueller
 *
 */
public class GarbageSpankConsole {
	
	private static final int LINE_INDEX_HEADER = 0;
	private static final int LINE_INDEX_FIRST_DATA = 1;
	private BufferedReader reader;
	private long interval;
	JStatLine currentLine = null;
	JStatLine previousLine = null;

	private GarbageSpank garbageSpank = new GarbageSpank();
	private PrintStream printStream;
	private String jstatHeader;
	public GarbageSpank getGarbageSpank() {
		return garbageSpank;
	}
	public void setGarbageSpank(GarbageSpank garbageSpank) {
		this.garbageSpank = garbageSpank;
	}
	public void setReader(BufferedReader r) {
		this.reader = r;
	}
	/**
	 * 
	 * @throws IOException
	 * @throws JStatHeaderException
	 */
	public void processJStatLines() throws IOException, JStatHeaderException {
	    int jstatLineIndex = 0;
	    String lineOfTextFromJStat;
	  //This readLine() will wait until jstat dumps out an additional line of text, 
	  //based on jstat interval parameter
	    while ((lineOfTextFromJStat = this.getReader().readLine()) != null ) 
	      this.getPrintStream().println(this.enhance(lineOfTextFromJStat,jstatLineIndex++));
		
	}
	public static void main(String args[]) throws IOException, JStatHeaderException {
		CommandLineParameters clp = new CommandLineParameters(args);
		GarbageSpankConsole jsc = new GarbageSpankConsole();
		int interval;
		
		if (!clp.valid()) {
			System.out.println( clp.getUsage() );
		} else {
			jsc.setPrintStream( System.out );
			jsc.setReader(new BufferedReader(new InputStreamReader(System.in)));

			jsc.getGarbageSpank().setIntervalInMilliSeconds(clp.getIntervalInMilliseconds());
		    
			jsc.processJStatLines();
		}
	}
	public void setPrintStream(PrintStream out) {
		this.printStream = out;
	}
	private PrintStream getPrintStream() {
		return this.printStream;
	}
	private String enhance(String s, int lineIndex) throws JStatHeaderException {
		StringBuilder rc = new StringBuilder();
		JStatMetricProvider metric  = null;
		if (lineIndex == LINE_INDEX_HEADER) {
			metric  = this.getGarbageSpank().getMetricByHeader(s.trim());
			this.jstatHeader = s;
			
			this.getGarbageSpank().setJStatOption(metric.getJStatOption());
			rc.append( metric.getEnhancedHeader() );
		} else {
			if (this.getGarbageSpank().getJStatOption()==null) 
				throw new JStatHeaderException("Unable to determine jstat's 'option' based on given jstat header [" + this.jstatHeader + "].  Perhaps jstat has added an unexpected metric or unexpected formatting?  See GarbageSpank.java for providing support paste java 1.8");
			else {
				metric = this.getGarbageSpank().getMetric();
			}
			
			currentLine = new JStatLine(
					GarbageSpank.GARBAGE_SPANK,
					garbageSpank.getMetric(), 
					s,
					garbageSpank.getIntervalInMilliSeconds() );
			
			if (lineIndex == LINE_INDEX_FIRST_DATA) { //many enhance columns require two lines of data,   so print place holders for 1st line.
				rc.append(s);//Add the given string of metrics, all in columns, but without the custom calculated columns (because they rely on a previous row of data which doesnt' yet exist);
				for(int i = metric.getIndexOfFirstEnhancedColumn(); i < metric.getJStatMetricNames().length; i++) {
					rc.append("    ??");
				}
			} else if (previousLine!=null) {  //first line after header will not pass this test.
				currentLine.setPrevious(previousLine);
				rc.append(s);//start with previous output....
				for(int i = metric.getIndexOfFirstEnhancedColumn(); i < metric.getJStatMetricNames().length; i++) {
					// then append new column(s)
					rc.append(
									metric.getColumnEnhancedAndFormatted(false, currentLine, previousLine, this.getGarbageSpank().getIntervalInMilliSeconds(), i)
							);
				}
			}
			previousLine = currentLine;
		}
			
		return rc.toString();
	}
	public BufferedReader getReader() {
		return this.reader;
	}
}
