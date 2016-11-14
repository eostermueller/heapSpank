package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcNew extends AbstractJStatMetricProvider implements JStatMetricProvider {

	private static final int INDEX_YGCT_Young_generation_garbage_collection_time = 10;
	private static final int POY_Young_generation_garbage_collection_overhead_percentage = 11;
	private static final String[] gcnew_metric_names = {
	    /** 00 */ "S0C_Current-survivor-space-0-capacity-(kB)",
	    /** 01 */ "S1C_Current-survivor-space-1-capacity-(kB)",
	    /** 02 */ "S0U_Survivor-space-0-utilization-(kB)",
	    /** 03 */ "S1U_Survivor-space-1-utilization-(kB)",
	    /** 04 */ "TT_Tenuring-threshold",
	    /** 05 */ "MTT_Maximum-tenuring-threshold",
	    /** 06 */ "DSS_Desired-survivor-size-(kB)",
	    /** 07 */ "EC_Current-eden-space-capacity-(kB)",
	    /** 08 */ "EU_Eden-space-utilization-(kB)",
	    /** 09 */ "YGC_Number-of-young-generation-GC-events",
	    /** 10 */ "YGCT_Young-generation-garbage-collection-time",
	    /** 11 */ "POY_Young-generation-garbage-collection-overhead-%_GS" //index=11 -- see customization below.
	};

		public String[] getJStatMetricNames() {
			return gcnew_metric_names;
		}

		/**
		 */
		@Override
		public String getColumnEnhanced(boolean skipCalculatedColumn, JStatLine current, JStatLine previous, long measurementIntervalInMilliSeconds, int columnIndex) {
			String rc = null;
			if ( columnIndex == POY_Young_generation_garbage_collection_overhead_percentage) {
				if (skipCalculatedColumn)
					rc = CALC_COLUMN_NOT_SUPPORTED;
				else {
					long newGcTimeMs_current = current.getRawColumnConvertSecondsToMsLong(INDEX_YGCT_Young_generation_garbage_collection_time);
					int percentage = 0;

					long newGcTimeMs_prev = previous.getRawColumnConvertSecondsToMsLong(INDEX_YGCT_Young_generation_garbage_collection_time);
					long numerator = newGcTimeMs_current-newGcTimeMs_prev;
					if (numerator > 0) 
						//Formula is ( New GC ms / processing duation ms) * 100 = % time in New GC.
						percentage = (int) ( numerator *100 / (measurementIntervalInMilliSeconds) );
					else
						percentage = 0;
					rc = String.valueOf(percentage);
				}
			} else {
				rc = current.getRawColumnData()[columnIndex]; //return whatever was in jstat stdout for this column.
			}
			return rc;
		}
		@Override
		public boolean metricsAvailableOnFirstJStatRun() {
			return false;
		}

		@Override
		public JVMVersion[] getSupportedVersions() {
			return new JVMVersion[]{JVMVersion.v1_8};
		}

		@Override
		public String getDescription() {
			return "Displays statistics of the behavior of the new generation.";
		}

		/**
		 * This is the header that indicates the jstat was launched with the "-gcnew" parameter
		 */
		@Override
		public String getOriginalHeader() {
			return "S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT";
		}
		@Override
		public String getEnhancedHeader() {
			return this.getOriginalHeader()+"    POY";
		}

		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gcnew;
		}

		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return POY_Young_generation_garbage_collection_overhead_percentage;
		}
		
}
