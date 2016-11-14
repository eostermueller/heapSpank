package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public class GcUtil extends AbstractJStatMetricProvider implements JStatMetricProvider {
	private static final int INDEX_YGC_Number_of_young_generation_GC_events = 6;
	private static final int INDEX_YGCT_Young_generation_garbage_collection_time = 7;
	private static final int INDEX_FGC_Number_of_full_GC_events = 8;
	private static final int INDEX_FGCT_Full_garbage_collection_time = 9;
	private static final int INDEX_GCT_Total_garbage_collection_time = 10;
	public static final int INDEX_YGCI_Number_of_young_generation_GC_events_per_interval_GS = 11;
	public static final int INDEX_FGCI_Number_of_full_GC_events_per_interval_GS = 12;
	public static final int INDEX_YGCTI_Young_generation_garbage_collection_time_per_interval_GS = 13;
	public static final int INDEX_FGCTI_Full_garbage_collection_time_per_interval_GS = 14;
	public static final int AYGCT_Avg_Young_generation_garbage_collection_time_GS = 15;
	public static final int AFGCT_Avg_Full_garbage_collection_time_GS = 16;
//	 /* 15 */ "AYGCT_Avg-Young-generation-garbage-collection-time_GS",
//	 /* 16 */ "AFGCT_Avg-Full-garbage-collection-time_GS"
	 
	 
	private static final int INDEX_AYGCT_average_young_gen_time_ms_GS = 15;
	private static final int INDEX_AFGCT_average_full_time_ms_GS = 16;
	
	private static final String[] gcutil_metric_names = {
         /* 00 */ "S0_Survivor-space-0-utilization-as-a-percentage-of-the-space's-current-capacity",
		 /* 01 */ "S1_Survivor-space-1-utilization-as-a-percentage-of-the-space's-current-capacity",
		 /* 02 */ "E_Eden-space-utilization-as-a-percentage-of-the-space's-current-capacity",
		 /* 03 */ "O_Old-space-utilization-as-a-percentage-of-the-space's-current-capacity",
		 /* 04 */ "M_Metaspace-utilization-as-a-percentage-of-the-space's-current-capacity",
		 /* 05 */ "CCS_Compressed-class-space-utilization-as-a-percentage",
		 /* 06 */ "YGC_Number-of-young-generation-GC-events",
		 /* 07 */ "YGCT_Young-generation-garbage-collection-time",
		 /* 08 */ "FGC_Number-of-full-GC-events",
		 /* 09 */ "FGCT_Full-garbage-collection-time",
		 /* 10 */ "GCT_Total-garbage-collection-time",
		 /* 11 */ "YGCI_Number-of-young-generation-GC-events-per-interval_GS",
		 /* 12 */ "FGCI_Number-of-full-GC-events-per-interval_GS",
		 /* 13 */ "YGCTI_Young-generation-garbage-collection-time-per-interval_GS",
		 /* 14 */ "FGCTI_Full-garbage-collection-time-per-interval_GS",
		 /* 15 */ "AYGCT_Avg-Young-generation-garbage-collection-time_GS",
		 /* 16 */ "AFGCT_Avg-Full-garbage-collection-time_GS"
	};

		@Override
		public String[] getJStatMetricNames() {
			return gcutil_metric_names;
		}
		
		//TODO:
		//Create unit tests for these ones at a time, because subsequent ones build off of prior ones.
		//1: Add young events per interval
		//2: Add young full per interval
		//3: Add young GC time per interval
		//4: Add full GC time per interval
		//5: Add young average time per interval.
		//6: Add full average time per interval.
		
		@Override
		public String getColumnEnhanced(boolean skipCalculatedColumns, JStatLine current, JStatLine previous, long measurementIntervalInMilliSeconds, int columnIndex) {
			String rc = null;
			if (columnIndex==INDEX_YGCI_Number_of_young_generation_GC_events_per_interval_GS) {
				if (skipCalculatedColumns) {
					rc = CALC_COLUMN_NOT_SUPPORTED;
				} else {
					String prevYoungEventCount = previous.getRawColumnData()[INDEX_YGC_Number_of_young_generation_GC_events];
					String currentYoungEventCount = current.getRawColumnData()[INDEX_YGC_Number_of_young_generation_GC_events];
					long youngEventsPerInterval = Long.parseLong(currentYoungEventCount) - Long.parseLong(prevYoungEventCount);
					rc = String.valueOf(youngEventsPerInterval);
				}
			} else if (columnIndex==INDEX_FGCI_Number_of_full_GC_events_per_interval_GS) {
				if (skipCalculatedColumns) {
					rc = CALC_COLUMN_NOT_SUPPORTED;
				} else {
					String prevFullEventCount = previous.getRawColumnData()[INDEX_FGC_Number_of_full_GC_events];
					String currentFullEventCount = current.getRawColumnData()[INDEX_FGC_Number_of_full_GC_events];
					long fullEventsPerInterval = Long.parseLong(currentFullEventCount) - Long.parseLong(prevFullEventCount);
					rc = String.valueOf(fullEventsPerInterval);
				}
			} else if (columnIndex==INDEX_YGCTI_Young_generation_garbage_collection_time_per_interval_GS) {
				if (skipCalculatedColumns) {
					rc = CALC_COLUMN_NOT_SUPPORTED;
				} else {
					String prevYoungTime = previous.getRawColumnData()[INDEX_YGCT_Young_generation_garbage_collection_time];
					String currentYoungTime = current.getRawColumnData()[INDEX_YGCT_Young_generation_garbage_collection_time];
					
					double youngTimePerInterval = Double.parseDouble(currentYoungTime) - Double.parseDouble(prevYoungTime);
					rc = com.github.eostermueller.heapspank.garbagespank.Util.formatDoubleWith3(youngTimePerInterval);
				}
			} else if (columnIndex==INDEX_FGCTI_Full_garbage_collection_time_per_interval_GS) {
				if (skipCalculatedColumns) {
					rc = CALC_COLUMN_NOT_SUPPORTED;
				} else {
					String prevFullTime = previous.getRawColumnData()[INDEX_FGCT_Full_garbage_collection_time];
					String currentFullTime = current.getRawColumnData()[INDEX_FGCT_Full_garbage_collection_time];
					
					double fullTimePerInterval = Double.parseDouble(currentFullTime) - Double.parseDouble(prevFullTime);
					rc = com.github.eostermueller.heapspank.garbagespank.Util.formatDoubleWith3(fullTimePerInterval);
				}
			} else if (columnIndex == AYGCT_Avg_Young_generation_garbage_collection_time_GS) {
				String youngGenTimePerInterval = this.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, INDEX_YGCTI_Young_generation_garbage_collection_time_per_interval_GS);
				String youngGenEventCount = this.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, INDEX_YGCI_Number_of_young_generation_GC_events_per_interval_GS);

				double dblYoungGenTimePerInterval = Double.parseDouble(youngGenTimePerInterval);
				double dblYoungGenEventCountPerInterval = Double.parseDouble(youngGenEventCount);
				
				if (dblYoungGenEventCountPerInterval ==0 || dblYoungGenEventCountPerInterval==0 )
					rc = "0.000";
				else {
					double avgYoungTime = dblYoungGenEventCountPerInterval / Double.parseDouble(youngGenEventCount);
					rc = com.github.eostermueller.heapspank.garbagespank.Util.formatDoubleWith3(avgYoungTime);
				}
			} else if (columnIndex == AFGCT_Avg_Full_garbage_collection_time_GS) {
				String fullGcTimePerInterval = this.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, INDEX_FGCTI_Full_garbage_collection_time_per_interval_GS);
				String fullGcEventCount = this.getColumnEnhanced(false, current, previous, measurementIntervalInMilliSeconds, INDEX_FGCI_Number_of_full_GC_events_per_interval_GS);

				double dblFullGcTimePerInterval = Double.parseDouble(fullGcTimePerInterval);
				double dblFullGcEventCount = Double.parseDouble(fullGcEventCount);
				
				if (dblFullGcTimePerInterval==0 || dblFullGcEventCount ==0) 
					rc = "0.000";
				else {
					double avgFullTime = dblFullGcTimePerInterval / dblFullGcEventCount;
					rc = com.github.eostermueller.heapspank.garbagespank.Util.formatDoubleWith3(avgFullTime);
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
			return "Displays a summary about garbage collection statistics.";
		}

		@Override
		public String getOriginalHeader() {
			return "S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT";
		}
		@Override
		public String getEnhancedHeader() {
			return this.getOriginalHeader()+"    YGCI    FGCI    YGCTI    FGCTI    AYGCT    AFGCT";			
		}
		

		@Override
		public JStatOption getJStatOption() {
			return JStatOption.gcutil;
		}

		@Override
		public int getIndexOfFirstEnhancedColumn() {
			return INDEX_YGCI_Number_of_young_generation_GC_events_per_interval_GS;
		}
}
