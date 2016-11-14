package com.github.eostermueller.heapspank.garbagespank;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.JStatMetricProvider;

public class JStatLinePrevious extends JStatLine {


	public JStatLinePrevious(String prefix, JStatMetricProvider metricNames,
			String jstatStdOut, long measurementIntervalInMilliSeconds) {
		super(prefix, metricNames, jstatStdOut, measurementIntervalInMilliSeconds);
		this.setSkipCalculatedColumns(true);
	}


}
