package com.github.eostermueller.heapspank.garbagespank.console;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.GarbageSpank;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class JStatHeaderTest {

	/**
	 * garbageSpank will use to header to detect the "option" passed to jstat, 
	 * so we'll know which metrics to add.
	 */
	@Test
	public void test() {
		GarbageSpank gs = new GarbageSpank();
		
		JStatMetricProvider mp = gs.getMetricByHeader("S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT");
		assertEquals("Cannot identify jstat 'gcnew' option given its header",
				JStatOption.gcnew, 
				mp.getJStatOption()
				);
		
		mp = gs.getMetricByHeader("MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT");
		assertEquals("Cannot identify jstat 'gcold' option given its header",
				JStatOption.gcold, 
				mp.getJStatOption()
				);

		mp = gs.getMetricByHeader("OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT");
		assertEquals("Cannot identify jstat 'gcold' option given its header",
				JStatOption.gcoldcapacity, 
				mp.getJStatOption()
				);
		
		
		mp = gs.getMetricByHeader("S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT");
		assertEquals("Cannot identify jstat 'gcold' option given its header",
				JStatOption.gcutil, 
				mp.getJStatOption()
				);
		
	}
	
	

}
