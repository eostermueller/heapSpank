package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class Gc extends AbstractJStatMetricProvider implements JStatMetricProvider {

	private static final String gc_metric_names[] = { 
		/* 00 */ "S0C_Current-survivor-space-0-capacity-(kB)",
		/* 01 */ "S1C_Current-survivor-space-1-capacity-(kB)",
		/* 02 */ "S0U_Survivor-space-0-utilization-(kB)",
		/* 03 */ "S1U_Survivor-space-1-utilization-(kB)",
		/* 04 */ "EC_Current-eden-space-capacity-(kB)",
		/* 05 */ "EU_Eden-space-utilization-(kB)",
		/* 06 */ "OC_Current-old-space-capacity-(kB)",
		/* 07 */ "OU_Old-space-utilization-(kB)",
		/* 08 */ "MC_Metaspace-capacity-(kB)",
		/* 09 */ "MU_Metacspace-utilization-(kB)",
		/* 10 */ "CCSC_Compressed-class-space-capacity-(kB)",
		/* 11 */ "CCSU_Compressed-class-space-used-(kB)",
		/* 12 */ "YGC_Number-of-young-generation-garbage-collection-events",
		/* 13 */ "YGCT_Young-generation-garbage-collection-time",
		/* 14 */ "FGC_Number-of-full-GC-events",
		/* 15 */ "FGCT_Full-garbage-collection-time",
		/* 16 */ "GCT_Total-garbage-collection-time",
		/* 17 */ "NU_New-space-utilization-(kB)"
	};
	private static final int INDEX_S0U_Survivor_space_0_utilization_kB = 2;
	private static final int INDEX_S1U_Survivor_space_1_utilization_kB = 3;
	private static final int INDEX_EU_Eden_space_utilization_kB = 5;
	private static final int INDEX_NU_New_space_utilization_kB = 17;


	public String[] getJStatMetricNames() {
		return gc_metric_names;
	}
	@Override
	public String getColumnEnhanced(boolean skipCalculatedColumn, 
			JStatLine current, 
			JStatLine previous, 
			long measurementIntervalInMilliSeconds, 
			int columnIndex) {
		String rc = null;
		
		/** I used Double instead of Long below because I was getting "0.0" data points. 
		 * 2016/10/30 18:01:17 ERROR - jmeter.protocol.java.sampler.AbstractJavaSamplerClient: garbageSpank:java.lang.NumberFormatException: For input string: "0.0"
		 *	at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
         *
		 */
		if ( columnIndex == INDEX_NU_New_space_utilization_kB) {
			
				String s0_util_kb = current.getRawColumnData()[INDEX_S0U_Survivor_space_0_utilization_kB];
				String s1_util_kb = current.getRawColumnData()[INDEX_S1U_Survivor_space_1_utilization_kB];
				String eden_util_kb = current.getRawColumnData()[INDEX_EU_Eden_space_utilization_kB];
				double new_util_kb = 
						Double.parseDouble(s0_util_kb) 
						+ Double.parseDouble(s1_util_kb)
						+ Double.parseDouble(eden_util_kb);
				rc = String.valueOf(new_util_kb);
		} else {
			rc = current.getRawColumnData()[columnIndex];
		}
		return rc;
	}

	@Override
	public JVMVersion[] getSupportedVersions() {
		return new JVMVersion[]{JVMVersion.v1_8};
	}

	@Override
	public String getDescription() {
		return "Displays statistics about the behavior of the garbage collected heap.";
	}

	@Override
	public String getOriginalHeader() {
		return "S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT";
	}

	@Override
	public JStatOption getJStatOption() {
		return JStatOption.gc;
	}
	@Override
	public int getIndexOfFirstEnhancedColumn() {
		return -1;
	}

}
