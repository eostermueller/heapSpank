package com.github.eostermueller.heapspank.garbagespank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcNew;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcOld;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class CustomColumnsJStatLineTest_OldGc_noChangeInCurrPrev {
	/** Output from the following:
	 * jstat -gcold 30167  1000 1
	 */
	private static String PREVIOUS_GCOLD_JSTAT_LINE = 
	 /*"   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT\n"+ */  
	 " 56748.0  55546.2   6828.0   6599.1    114688.0    114228.4   5848  1525  234.831  261.218";

	/** Output from the following (repeated a few seconds later).
	 * jstat -gcold 30167  1000 1
	 */
	private static String CURRENT_GCOLD_JSTAT_LINE = 
			 /*"     MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT\n"+ */  
			 " 56748.0  55546.2   6828.0   6599.1    114688.0    114228.4   5848  1525  234.831  261.218";

	     
	

	@Test
	public void test() {
		JStatMetricProvider metric = new GcOld();
		int measurementIntervalInSeconds = 5;
		JStatLinePrevious previous = new JStatLinePrevious("gs", metric, PREVIOUS_GCOLD_JSTAT_LINE, measurementIntervalInSeconds);
		JStatLine  current = new JStatLine("gs", metric, CURRENT_GCOLD_JSTAT_LINE, measurementIntervalInSeconds);
		current.setPrevious(previous);

		assertEquals("Couldn't find data in the index 8","234.831", previous.getRawColumnData()[8]);
		assertEquals("Couldn't find data in the index 8","234.831",  current.getRawColumnData()[8]);
		assertEquals("Couldn't find data in the index 10","0",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 10) );
		
		assertEquals("Couldn't find data in the index 10","261.218", previous.getRawColumnData()[9]);
		assertEquals("Couldn't find data in the index 10","261.218",  current.getRawColumnData()[9]);
		assertEquals("Couldn't find data in the index 10","0",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 11) );
	}

}
