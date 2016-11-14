package com.github.eostermueller.heapspank.garbagespank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcNew;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcOld;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcUtil;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class CustomColumnsJStatLineTest_GcUtil_gcTimePerInterval {
	/** Output from the following:
	 * jstat -gcutil 30167  1000 1
	 */
	private static String PREVIOUS_GCUTIL_JSTAT_LINE = 
		/*	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT */   
			  "0.00 100.00  22.38  40.13  97.83  95.74   7259  218.792     0    1.100  218.792";

	/** Output from the following (repeated a few seconds later).
	 * jstat -gcutil 30167  1000 1
	 */
	private static String CURRENT_GCUTIL_JSTAT_LINE = 
			/*	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT */   
                  "0.00 100.00  31.67  72.78  97.83  95.74   7307  220.190     0    1.200  220.190";


	@Test
	public void canConvertMillisecondsToSeconds() {
		
		assertEquals("Could not convert milliseconds (fewer than 1000) to seconds", "0.123", Util.msToSeconds("123"));
		assertEquals("Could not convert milliseconds (fewer than 99) to seconds", "0.012", Util.msToSeconds("12"));
		assertEquals("Could not convert milliseconds (fewer than 10) to seconds", "0.002", Util.msToSeconds("2"));
		assertEquals("Could not convert milliseconds                           ", "0.0", Util.msToSeconds("0"));
		assertEquals("Could not convert milliseconds                           ", "0.0", Util.msToSeconds("00"));
		assertEquals("Could not convert milliseconds                           ", "0.0", Util.msToSeconds("000"));
		assertEquals("Could not convert milliseconds (Greater than 1000) to seconds", "1.234", Util.msToSeconds("1234"));
		
				
		
	}
	@Test
	public void canCalculateGcTimePerInterval() {
		JStatMetricProvider metric = new GcUtil();
		int measurementIntervalInSeconds = 5;
		JStatLinePrevious previous = new JStatLinePrevious("gs", metric, PREVIOUS_GCUTIL_JSTAT_LINE, measurementIntervalInSeconds);
		JStatLine  current = new JStatLine("gs", metric, CURRENT_GCUTIL_JSTAT_LINE, measurementIntervalInSeconds);
		current.setPrevious(previous);

		assertEquals("Couldn't find young GC time in a single interval","1.398", 
				metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 
						GcUtil.INDEX_YGCTI_Young_generation_garbage_collection_time_per_interval_GS)
				);

		assertEquals("Couldn't find full GC time in a single interval","0.100", 
				metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 
						GcUtil.INDEX_FGCTI_Full_garbage_collection_time_per_interval_GS)
				);
		
	}

}
