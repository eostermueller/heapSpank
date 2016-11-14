package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;
import com.github.eostermueller.heapspank.garbagespank.JVMVersion;

public interface JStatMetricProvider {
	public static final String CALC_COLUMN_NOT_SUPPORTED = "<CalculatedColumnNotSupported>";	
	
	public int getIndexOfFirstEnhancedColumn();
	
	JVMVersion[] getSupportedVersions();
	String getDescription();
	String getOriginalHeader();
	String getEnhancedHeader();
	boolean metricsAvailableOnFirstJStatRun();
	JStatOption getJStatOption();
	String[] getJStatMetricNames();
	/**
   	 * What does enhanced mean?
	 * Indexes for most columns point to data straight from jstat.
	 * Other columns, however, require some calculations.
	 * @param skipCalculatedColumns 

	 * @param current
	 * @param previous
	 * @param mesasurementIntervalInSeconds
	 * @param i
	 * @return
	 */
	String getColumnEnhanced(boolean skipCalculatedColumns, JStatLine current, JStatLine previous, long mesasurementIntervalInMilliSeconds, int i);

	String getColumnEnhancedAndFormatted(boolean skipCalculatedCol,
			JStatLine current, JStatLine previous,
			long measurementIntervalInMilliSeconds, int i);
}
