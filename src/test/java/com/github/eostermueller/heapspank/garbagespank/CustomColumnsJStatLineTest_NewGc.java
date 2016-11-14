package com.github.eostermueller.heapspank.garbagespank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.GcNew;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class CustomColumnsJStatLineTest_NewGc {
	private static String PREVIOUS_GCNEW_JSTAT_LINE = 
	 /* "S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT\n"+ */  
	   "4608.0 3584.0    0.0 3362.3 13  15 4608.0 401408.0 224419.4   4181   13.415";

	private static String CURRENT_GCNEW_JSTAT_LINE = 
		/*	 "  S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT\n"+ */  
			 " 4608.0 5120.0 3796.5    0.0 12  15 5120.0 399360.0 381087.8   4182   14.415";

	  
	

	@Test
	public void test() {
		JStatMetricProvider metric = new GcNew();
		long  measurementIntervalInMilliSeconds = 5000;
		JStatLinePrevious previous = new JStatLinePrevious("gs", metric, PREVIOUS_GCNEW_JSTAT_LINE, measurementIntervalInMilliSeconds);
		JStatLine  current = new JStatLine("gs", metric, CURRENT_GCNEW_JSTAT_LINE, measurementIntervalInMilliSeconds);
		current.setPrevious(previous);

		assertEquals("Couldn't find data in the index 10","13.415", previous.getRawColumnData()[10]);

		assertEquals("Couldn't find data in the index 10","14.415",  current.getRawColumnData()[10]);
		
		assertEquals("Couldn't find data in the index 10","20",  metric.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, 11) );
		
	}

}
