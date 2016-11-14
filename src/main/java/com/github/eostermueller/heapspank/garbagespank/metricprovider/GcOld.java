package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcOld extends AbstractJStatMetricProvider implements JStatMetricProvider {
	
	private static final int INDEX_FGCT_Full_garbage_collection_time =  8;
	private static final int INDEX_GCT_Total_garbage_collection_time =  9;
	private static final int INDEX_POF_Full_garbage_collection_overhead_percentage = 10;
	private static final int INDEX_FGCT_POT_Total_garbage_collection_overhead_percentage = 11;
	

	private static final String[] gcold_metric_names = {
		/** 00 */ "MC_Metaspace-capacity-(kB)",
		/** 01 */ "MU_Metaspace-utilization-(kB)",
		/** 02 */ "CCSC_Compressed-class-space-capacity-(kB)",
		/** 03 */ "CCSU_Compressed-class-space-used-(kB)",
		/** 04 */ "OC_Current-old-space-capacity-(kB)",
		/** 05 */ "OU_Old-space-utilization-(kB)",
		/** 06 */ "YGC_Number-of-young-generation-GC-events",
		/** 07 */ "FGC_Number-of-full-GC-events",
		/** 08 */ "FGCT_Full-garbage-collection-time",
		/** 09 */ "GCT_Total-garbage-collection-time",
		/** 10 */ "POF_Full-garbage-collection-overhead-%_GS",
		/** 11 */ "POT_Total-garbage-collection-overhead-%_GS"
		};
	
	@Override
	public String getEnhancedHeader() {
		return this.getOriginalHeader()+"    POF    POT";
	}

		public String[] getJStatMetricNames() {
			return gcold_metric_names;
		}
		
		/**
		 */
		@Override
		public String getColumnEnhanced(boolean skipCalculatedColumns, JStatLine current, JStatLine previous, long measurementIntervalInMilliSeconds, int columnIndex) {
			String rc = null;
			if (columnIndex == INDEX_POF_Full_garbage_collection_overhead_percentage) {
				if (skipCalculatedColumns) {
					rc = CALC_COLUMN_NOT_SUPPORTED;
				} else {
					long fullGcTimeMs_current = current.getRawColumnConvertSecondsToMsLong(INDEX_FGCT_Full_garbage_collection_time);
					int percentage = 0;

					long fullGcTimeMs_prev = previous.getRawColumnConvertSecondsToMsLong(INDEX_FGCT_Full_garbage_collection_time);
					long numerator = fullGcTimeMs_current-fullGcTimeMs_prev;
					if (numerator > 0) 
						//Formula is ( Full GC ms / processing duation ms) * 100 = % time in Full GC.
						percentage = (int) ( numerator *100 / (measurementIntervalInMilliSeconds) );
					else
						percentage = 0;
					rc = String.valueOf(percentage);
				}
			} else if (columnIndex == INDEX_FGCT_POT_Total_garbage_collection_overhead_percentage) {
				if (skipCalculatedColumns) {
					rc  = CALC_COLUMN_NOT_SUPPORTED;
				} else {
					long totalGcTimeMs_current = current.getRawColumnConvertSecondsToMsLong(INDEX_GCT_Total_garbage_collection_time);
					int percentage = 0;

					long totalGcTimeMs_prev = previous.getRawColumnConvertSecondsToMsLong(INDEX_GCT_Total_garbage_collection_time);
					long numerator = totalGcTimeMs_current-totalGcTimeMs_prev;
					if (numerator > 0) 
						//Formula is ( Total GC ms / processing duation ms) * 100 = % time in Total GC.
						percentage = (int) ( numerator *100 / (measurementIntervalInMilliSeconds) );
					else
						percentage = 0;
					rc = String.valueOf(percentage);
				}
			} else {
				rc = current.getRawColumnData()[columnIndex];
			}
			return rc;
		}
		
		@Override
		public boolean metricsAvailableOnFirstJStatRun() {
			return false;
		}

		@Override
		public JVMVersion[] getSupportedVersions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDescription() {
			return "Displays statistics about the behavior of the old generation and metaspace statistics.";
		}

		@Override
		public String getOriginalHeader() {
			return "MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT";
		}

		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gcold;
		}
		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return INDEX_POF_Full_garbage_collection_overhead_percentage;
		}

}
