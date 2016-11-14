package com.github.eostermueller.heapspank.garbagespank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcNew;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcOld;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class CustomColumnsJStatLineTest_OldGc {
	/** Output from the following:
	 * jstat -gcold 30167  1000 1
	 */
	private static String PREVIOUS_GCOLD_JSTAT_LINE = 
	 /*"   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT\n"+ */  
	 " 56492.0  55483.4   6828.0   6598.6    114688.0     72361.6   4190    20    2.936   16.415";

	/** Output from the following (repeated a few seconds later).
	 * jstat -gcold 30167  1000 1
	 */
	private static String CURRENT_GCOLD_JSTAT_LINE = 
			 /*"     MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT\n"+ */  
			 "   56492.0  55529.1   6828.0   6604.2    114688.0     74706.5   4194    20    2.936   17.415";

	     
	

	@Test
	public void test() {
		JStatMetricProvider metric = new GcOld();
		int measurementIntervalInMilliSeconds = 5000;
		JStatLinePrevious previous = new JStatLinePrevious("gs", metric, PREVIOUS_GCOLD_JSTAT_LINE, measurementIntervalInMilliSeconds);
		JStatLine  current = new JStatLine("gs", metric, CURRENT_GCOLD_JSTAT_LINE, measurementIntervalInMilliSeconds);
		current.setPrevious(previous);

		assertEquals("Couldn't find data in the index 10","2.936", previous.getRawColumnData()[8]);
		assertEquals("Couldn't find data in the index 10","2.936",  current.getRawColumnData()[8]);
		assertEquals("Couldn't find data in the index 10","0",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, 10) );
		
		assertEquals("Couldn't find data in the index 10","16.415", previous.getRawColumnData()[9]);
		assertEquals("Couldn't find data in the index 10","17.415",  current.getRawColumnData()[9]);
		assertEquals("Couldn't find data in the index 10","20",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, 11) );
	}

}
