package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcOldCapacity extends AbstractJStatMetricProvider implements JStatMetricProvider {

	private static final String[] gcoldcapacity_metric_names = {
		"OGCMN_Minimum-old-generation-capacity-(kB)",
		"OGCMX_Maximum-old-generation-capacity-(kB)",
		"OGC_Current-old-generation-capacity-(kB)",
		"OC_Current-old-space-capacity-(kB)",
		"YGC_Number-of-young-generation-GC-events",
		"FGC_Number-of-full-GC-events",
		"FGCT_Full-garbage-collection-time",
		"GCT_Total-garbage-collection-time"
		};
	

		public String[] getJStatMetricNames() {
			return gcoldcapacity_metric_names;
		}

		@Override
		public JVMVersion[] getSupportedVersions() {
			return new JVMVersion[]{JVMVersion.v1_8};
		}

		@Override
		public String getDescription() {
			return "Displays statistics about the sizes of the old generation.";
		}

		@Override
		public String getOriginalHeader() {
			return "OGCMN       OGCMX        OGC         OC       YGC   FGC    FGCT     GCT";
		}

		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gcoldcapacity;
		}
		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return -1;
		}

}
