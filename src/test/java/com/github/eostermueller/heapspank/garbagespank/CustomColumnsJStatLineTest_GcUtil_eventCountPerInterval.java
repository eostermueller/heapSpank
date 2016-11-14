package com.github.eostermueller.heapspank.garbagespank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcNew;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcOld;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcUtil;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class CustomColumnsJStatLineTest_GcUtil_eventCountPerInterval {
	/** Output from the following:
	 * jstat -gcold 30167  1000 1
	 */
	private static String PREVIOUS_GCUTIL_JSTAT_LINE = 
		/*	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT */   
			  "0.00 100.00  22.38  40.13  97.83  95.74   7259  218.792     0    0.000  218.792";

	/** Output from the following (repeated a few seconds later).
	 * jstat -gcold 30167  1000 1
	 */
	private static String CURRENT_GCUTIL_JSTAT_LINE = 
			/*	  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT */   
                  "0.00 100.00  31.67  72.78  97.83  95.74   7307  220.190     2    0.000  220.190";


	@Test
	public void test() {
		JStatMetricProvider metric = new GcUtil();
		int measurementIntervalInSeconds = 5;
		JStatLinePrevious previous = new JStatLinePrevious("gs", metric, PREVIOUS_GCUTIL_JSTAT_LINE, measurementIntervalInSeconds);
		JStatLine  current = new JStatLine("gs", metric, CURRENT_GCUTIL_JSTAT_LINE, measurementIntervalInSeconds);
		current.setPrevious(previous);

		assertEquals("Couldn't find counts/interval of young gen events per interval","48", 
				metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 
						GcUtil.INDEX_YGCI_Number_of_young_generation_GC_events_per_interval_GS)
				);

		assertEquals("Couldn't find counts/interval of full gen events per interval","2", 
				metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 
						GcUtil.INDEX_FGCI_Number_of_full_GC_events_per_interval_GS)
				);
		
//		assertEquals("Couldn't find data in the index 10","2.936",  current.getRawColumnData()[8]);
//		assertEquals("Couldn't find data in the index 10","0",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 10) );
//		
//		assertEquals("Couldn't find data in the index 10","16.415", previous.getRawColumnData()[9]);
//		assertEquals("Couldn't find data in the index 10","17.415",  current.getRawColumnData()[9]);
//		assertEquals("Couldn't find data in the index 10","20",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInSeconds, 11) );
	}

}
