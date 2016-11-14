package com.github.eostermueller.heapspank.garbagespank.metricprovider;

import com.github.eostermueller.heapspank.garbagespank.JStatLine;

public abstract class AbstractJStatMetricProvider implements JStatMetricProvider  {
	@Override
	public String getColumnEnhanced(boolean skipCalculatedCol, JStatLine current, JStatLine previous, long measurementIntervalInMilliSeconds, int i) {
		return current.getRawColumnData()[i];
	}
	@Override
	public boolean metricsAvailableOnFirstJStatRun() {
		return true;
	}
	@Override
	public String getEnhancedHeader() {
		return null;
	}
	@Override
	public String getDescription() {
		return null;
	}
	@Override
	public String getOriginalHeader() {
		return null;
	}
	@Override
	public int getIndexOfFirstEnhancedColumn() {
		return -1;
	}
	
	@Override
	public String getColumnEnhancedAndFormatted(boolean skipCalculatedCol, JStatLine current, JStatLine previous, long measurementIntervalInMilliSeconds, int i) {
		String oneNumberInOneColumn = this.getColumnEnhanced(skipCalculatedCol, current, previous, measurementIntervalInMilliSeconds, i);
		return String.format("%6s", oneNumberInOneColumn);
	}
}
