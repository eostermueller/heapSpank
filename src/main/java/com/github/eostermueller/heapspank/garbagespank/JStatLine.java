package com.github.eostermueller.heapspank.garbagespank;

import java.util.Arrays;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

/**
 * Reads in one row of this from $JAVA_HOME/bin/jstat
 * <PRE>
   S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
  0.00   0.00  63.97  86.27  98.13  96.64   4177   13.151    17    2.668   15.820
  </PRE>
  and parses the individual colums to be reformatted.
  
 * @author erikostermueller
 *
 */
public class JStatLine {
	private boolean skipCalculatedColumns;
	public void setSkipCalculatedColumns(boolean b) {
		this.skipCalculatedColumns = b;
	}
	public boolean getSkipCalculatedColumns() {
		return this.skipCalculatedColumns;
	}
	
	JStatLine previous = null;

	public JStatLine getPrevious() {
		return previous;
	}

	public void setPrevious(JStatLine previousLine) {
		this.previous = previousLine;
	}

	public String[] getRawColumnData() {
		return columnData;
	}

	public void setColumnData(String[] columnData) {
		this.columnData = columnData;
	}

	String[] columnData = null;
	String prefix = null;
	JStatMetricProvider metricProvider = null;
	private String jstatStdOut;
	private long measurementIntervalInMilliSeconds;
	public JStatLine(String prefix, JStatMetricProvider metricNames,
			String jstatStdOut, long measurementIntervalInMilliSeconds) {
		
		this.metricProvider = metricNames;
		this.prefix = prefix;
		this.jstatStdOut = jstatStdOut;
		this.measurementIntervalInMilliSeconds = measurementIntervalInMilliSeconds;
		
		parseColumns();
	}

	
	private void parseColumns() {
		String lineWithData = this.jstatStdOut;
		for(int x  = 0; x < 5; x++) 
			lineWithData = lineWithData.replace("  ", " ");
		
		this.columnData = lineWithData.trim().split(" ");
	}

	/**
	 * Let's say the number "15.820" is index 10.
	 * If you pass a 10 into this method, it will return a long with 15820, having converted to 15.820 seconds into 15,820 milliseconds.
	 * @param columnIndex
	 * @return
	 */
	public long getRawColumnConvertSecondsToMsLong(int columnIndex) {
		String strValInSeconds = this.getRawColumnData()[columnIndex];
		return getConvertSecondsToMsLong(strValInSeconds);
	}
	public long getConvertSecondsToMsLong(String strValInSeconds) {
		Double dblTimeSeconds = Double.parseDouble(strValInSeconds.trim());  
		long timeMs = (long) (dblTimeSeconds.doubleValue() * 1000);
		return timeMs;
	}
	public double getRawColumnConvertSecondsToMsDouble(int columnIndex) {
		String valInSeconds = this.getRawColumnData()[columnIndex];
		return getConvertSecondsToMsDouble(valInSeconds);
	}
	public double getConvertSecondsToMsDouble(String valInSeconds) {
		Double dblValInSeconds = Double.parseDouble(valInSeconds.trim());  
		double dblValInMs =  (dblValInSeconds.doubleValue() * 1000);
		return dblValInMs;
	}
	public String getConvertMsToSeconds(String val) {
		return val;
	}
	/**
	 * Format jstat data to be graphed by this component:
	 * https://jmeter-plugins.org/wiki/PageDataExtractor/
	 * @return
	 */
	public String getPageDataExtractorFormat() {
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < metricProvider.getJStatMetricNames().length; i++) {
			sb.append(prefix);
			sb.append( metricProvider.getJStatMetricNames()[i] );
			sb.append("=");
			sb.append( this.getDataForColumn(i));
			sb.append("<BR>\n");
		}
		return sb.toString();
	}

	private String getDataForColumn(int columnIndex) {
		return this.metricProvider.getColumnEnhanced(
				this.skipCalculatedColumns,
				this, 
				this.previous, 
				this.measurementIntervalInMilliSeconds, 
				columnIndex);
	}

}
