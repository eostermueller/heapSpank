package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcCapacity extends AbstractJStatMetricProvider implements JStatMetricProvider {

	private static final String[] gccapacity_metric_names = {
		"NGCMN_Minimum-new-generation-capacity-(kB)",
		"NGCMX_Maximum-new-generation-capacity-(kB)",
		"NGC_Current-new-generation-capacity-(kB)",
		"S0C_Current-survivor-space-0-capacity-(kB)",
		"S1C_Current-survivor-space-1-capacity-(kB)",
		"EC_Current-eden-space-capacity-(kB)",
		"OGCMN_Minimum-old-generation-capacity-(kB)",
		"OGCMX_Maximum-old-generation-capacity-(kB)",
		"OGC_Current-old-generation-capacity-(kB)",
		"OC_Current-old-space-capacity-(kB)",
		"MCMN_Minimum-metaspace-capacity-(kB)",
		"MCMX_Maximum-metaspace-capacity-(kB)",
		"MC_Metaspace-capacity-(kB)",
		"CCSMN_Compressed-class-space-minimum-capacity-(kB)",
		"CCSMX_Compressed-class-space-maximum-capacity-(kB)",
		"CCSC_Compressed-class-space-capacity-(kB)",
		"YGC_Number-of-young-generation-GC-events",
		"FGC_Number-of-full-GC-events"
		};
	
		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gccapacity;
		}

		public String[] getJStatMetricNames() {
			return gccapacity_metric_names;
		}

		@Override
		public JVMVersion[] getSupportedVersions() {
			return new JVMVersion[]{ JVMVersion.v1_8 };
		}

		@Override
		public String getDescription() {
			return "Displays statistics about the capacities of the generations and their corresponding spaces.";
		}

		@Override
		public String getOriginalHeader() {
			return "NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC";
		}

		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return -1;
		}


}
