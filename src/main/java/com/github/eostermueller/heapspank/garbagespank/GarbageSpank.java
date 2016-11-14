package com.github.eostermueller.heapspank.garbagespank;

import java.util.concurrent.ConcurrentHashMap;

import com.github.eostermueller.heapspank.garbagespank.metricprovider.*;

public class GarbageSpank {
	public static final String GARBAGE_SPANK = "gs_";
	private static ConcurrentHashMap<JStatOption,JStatMetricProvider> metricNames 
		= new ConcurrentHashMap<JStatOption,JStatMetricProvider>();
	private static ConcurrentHashMap<String,JStatMetricProvider> metricHeaders 
		= new ConcurrentHashMap<String,JStatMetricProvider>();
	
	static {
		addProvider( new Gc() );
		addProvider( new GcCapacity() );
		addProvider( new GcMetaCapacity() );
		addProvider( new GcNew() );
		addProvider( new GcNewCapacity() );
		addProvider( new GcOld() );
		addProvider( new GcOldCapacity() );
		addProvider( new GcUtil() );		
		/* add more providers below to provide support past JDK 1.8  */
	}

	public static ConcurrentHashMap<JStatOption, JStatMetricProvider> getAllMetricNames() {
		return metricNames;
	}
	public JStatMetricProvider getMetric() {
		return getAllMetricNames().get(this.getJStatOption());
	}
	public static void setMetricNames(
			ConcurrentHashMap<JStatOption, JStatMetricProvider> metricNames) {
		GarbageSpank.metricNames = metricNames;
	}
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public JStatOption getJStatOption() {
		return jstatOption;
	}
	public void setJStatOption(JStatOption jstatOption) {
		this.jstatOption = jstatOption;
	}
	public long getIntervalInMilliSeconds() {
		return intervalInMilliSeconds;
	}
	public void setIntervalInMilliSeconds(long intervalInMilliSeconds) {
		this.intervalInMilliSeconds = intervalInMilliSeconds;
	}
	private long pid = -1;
	private JStatOption jstatOption = null;
	private long intervalInMilliSeconds = -1;
	
	private static void addProvider(JStatMetricProvider jsmp) {
		metricNames.put(jsmp.getJStatOption(), jsmp);
		metricHeaders.put(jsmp.getOriginalHeader(), jsmp);
		
	}
	public JStatMetricProvider getMetricNamesByOption(JStatOption jstatOption) {
		return this.metricNames.get(jstatOption);
	}
	public JStatMetricProvider getMetricByHeader(String headerFromJStat) {
		return this.metricHeaders.get(headerFromJStat);
	}
}

