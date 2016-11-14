package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcMetaCapacity extends AbstractJStatMetricProvider implements JStatMetricProvider {

	private static final String[] gcmetacapacity = {
		"MCMN_Minimum-metaspace-capacity-(kB)",
		"MCMX_Maximum-metaspace-capacity-(kB)",
		"MC_Metaspace-capacity-(kB)",
		"CCSMN_Compressed-class-space-minimum-capacity-(kB)",
		"CCSMX_Compressed-class-space-maximum-capacity-(kB)",
		"YGC_Number-of-young-generation-GC-events",
		"FGC_Number-of-full-GC-events",
		"FGCT_Full-garbage-collection-time",
		"GCT_Total-garbage-collection-time"
		};	

		public String[] getJStatMetricNames() {
			return gcmetacapacity;
		}

		@Override
		public JVMVersion[] getSupportedVersions() {
			return new JVMVersion[]{JVMVersion.v1_8};
		}

		@Override
		public String getDescription() {
			return "Displays statistics about the sizes of the metaspace.";
		}

		@Override
		public String getOriginalHeader() {
			return "MCMN       MCMX        MC       CCSMN      CCSMX       CCSC     YGC   FGC    FGCT     GCT";
		}

		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gcmetacapacity;
		}
		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return -1;
		}

}
