package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcNewCapacity extends AbstractJStatMetricProvider implements JStatMetricProvider {

	private static final String[] gcnewcapacity_metric_names = {
		"NGCMN_Minimum-new-generation-capacity-(kB)",
		"NGCMX_Maximum-new-generation-capacity-(kB)",
		"NGC_Current-new-generation-capacity-(kB)",
		"S0CMX_Maximum-survivor-space-0-capacity-(kB)",
		"S0C_Current-survivor-space-0-capacity-(kB)",
		"S1CMX_Maximum-survivor-space-1-capacity-(kB)",
		"S1C_Current-survivor-space-1-capacity-(kB)",
		"ECMX_Maximum-eden-space-capacity-(kB)",
		"EC_Current-eden-space-capacity-(kB)",
		"YGC_Number-of-young-generation-GC-events",
		"FGC_Number-of-full-GC-events"
		};	
	
		public String[] getJStatMetricNames() {
			return gcnewcapacity_metric_names;
		}

		@Override
		public JVMVersion[] getSupportedVersions() {
			return new JVMVersion[]{ JVMVersion.v1_8 };
		}

		@Override
		public String getDescription() {
			return "Displays statistics about the sizes of the new generations and its corresponding spaces.";
		}

		@Override
		public String getOriginalHeader() {
			return "NGCMN      NGCMX       NGC      S0CMX     S0C     S1CMX     S1C       ECMX        EC      YGC   FGC";
		}

		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gcnewcapacity;
		}
		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return -1;
		}

}
