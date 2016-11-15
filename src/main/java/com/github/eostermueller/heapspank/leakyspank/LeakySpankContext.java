package com.github.eostermueller.heapspank.leakyspank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.leakyspank.console.TheLeakiest;
import com.github.eostermueller.heapspank.util.BaseEvent;
import com.github.eostermueller.heapspank.util.EventListener;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;

public class LeakySpankContext {

	private List<EventListener> windowClosedListener = new ArrayList<EventListener>();
	public void addWindowClosedListener(EventListener e) {
		this.windowClosedListener.add(e);
	}
	public static class WindowClosedEvent extends BaseEvent {
		
	}
	private static final String LEAKY_SPANK = "leakySpank: ";
	private int currentRunCount  = 0;
	private LimitedSizeQueue<String> debugDisplayQ;
	public void incrementRunCount() {
		this.currentRunCount++;
		
		if ( (this.currentRunCount -1) % this.getRunCountPerWindow() == 0 ) {
			WindowClosedEvent wce = new WindowClosedEvent();
			for(EventListener el : this.windowClosedListener)
				el.onEvent(wce);
		}
	}
	public void setDebugDisplayQueue(LimitedSizeQueue<String> display) {
		this.debugDisplayQ = display;
	}
	public LimitedSizeQueue<String> getDebugDisplayQ() {
		return this.debugDisplayQ;
	}
	public int getCurrentRunCount() {
		return this.currentRunCount;
	}
	private int runCountPerWindow = -1;
	public int getRunCountPerWindow() {
		return runCountPerWindow;
	}
	public void setRunCountPerWindow(int runCountPerWindow) {
		this.runCountPerWindow = runCountPerWindow;
	}
	private int intervalInSeconds = -1;
	private long pid = -1;
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	private LimitedSizeQueue<Model> recentJMapRuns = null; 
	private int rankIncreaseThreshold;
	private int countPresentThreshold;
	private long bytesIncreaseThreshold;
	private long instancesIncreaseThreshold;
	private int topNSuspects;

	public int getTopNSuspects() {
		return topNSuspects;
	}
	public void setTopNSuspects(int topNSuspects) {
		this.topNSuspects = topNSuspects;
	}
	public int getCountPresentThreshold() {
		return countPresentThreshold;
	}
	public void setCountPresentThreshold(int countPresentThreshold) {
		this.countPresentThreshold = countPresentThreshold;
	}
	public int getRankIncreaseThreshold() {
		return rankIncreaseThreshold;
	}
	public void setRankIncreaseThreshold(int rankIncreaseThreshold) {
		this.rankIncreaseThreshold = rankIncreaseThreshold;
	}
	public LeakySpankContext(long pid, int interval_in_seconds, int interval_count, int topNSupsects) {
		this.pid = pid;
		this.intervalInSeconds = interval_in_seconds;
		this.runCountPerWindow = interval_count;
		this.setTopNSuspects(topNSupsects);
		this.setCountPresentThreshold(interval_count);
		
		this.recentJMapRuns = new LimitedSizeQueue<Model>(this.getRunCountPerWindow()+1);//the extra one will provide N comparisons between each of the N+1 items.
		this.setRankIncreaseThreshold(1); //if current JMap -histo 'num' is < prev, then increment LeakResult.countRunsWithRankIncrease
		
	}
	public LeakySpankContext clone(int multiplier) {
		
		LeakySpankContext lsc = new LeakySpankContext(
				this.pid,
				this.intervalInSeconds*multiplier,
				this.runCountPerWindow,
				this.getTopNSuspects() );
		return lsc;
		
	}
	public void addJMapHistoRun(Model m) {
		this.recentJMapRuns.add(m);
		incrementRunCount();
	}
	
	private void debug(String msg) {
//		System.out.println(msg);
//		if (this.getDebugDisplayQ()!=null)
//			this.getDebugDisplayQ().offer(msg);
	}
	public List<LeakResult> getLeakSuspectsUnOrdered() {
		Model mostRecent = this.recentJMapRuns.peekLast();//choose most recent run if the X runs in the window, because this is most up-to-date byte count.
		//debug(String.format("recent jmapRuns Count %d", this.recentJMapRuns.size()) );
		List<LeakResult> results = new ArrayList<LeakResult>();
		if (mostRecent!=null) {
			for(JMapHistoLine line : mostRecent.getAll())     {
				results.add(tallyLeakActivityFromPreviousRuns(line));
			}
		}
		return results;
	}
	/**
	 * Ordered from least likely to most likely a leak.
	 * @return
	 */
	public LeakResult[] getLeakSuspectsOrdered() {
		List suspects = this.getLeakSuspectsUnOrdered();
		Collections.sort( suspects, LeakySpankContext.PCT_OF_RUNS_WITH_UPWARD_TRENDING_BYTES);
		return (LeakResult[]) suspects.toArray( new LeakResult[0]);
	}
	/**
	 * Given a single line from JMapHisto results, 
	 * tall counts of various stats.
	 * @param lineToTally
	 * @return
	 */
	public LeakResult tallyLeakActivityFromPreviousRuns(JMapHistoLine lineToTally) {
		LeakResult result = new LeakResult();
		result.line  = lineToTally;
		
		Iterator<Model> modelIterator = this.recentJMapRuns.iterator();
		if (modelIterator.hasNext()) {
			Model previousModel = modelIterator.next();
			
			while( modelIterator.hasNext()) {
				Model currentModel = modelIterator.next();
				
				JMapHistoLine currentRunLine = currentModel.get(lineToTally.className);
				
				JMapHistoLine previousRunLine = previousModel.get(lineToTally.className);
				
				if (currentRunLine!=null && previousRunLine!=null) { //Not all classes show up in all JMapHisto outputs
					currentRunLine.visitCount++;
					this.debug(String.format("Visit count [%d] class [%s", currentRunLine.visitCount, currentRunLine.className));

					previousRunLine.visitCount++;
					this.debug(String.format("PrevVisit count [%d] class [%s", previousRunLine.visitCount, previousRunLine.className));
					
					result.countRunsPresent++;
					if ( currentRunLine.timestampNanos < previousRunLine.timestampNanos)
						throw new RuntimeException("Perhaps iterating in the wrong direction?");
					
					/**
					 * jmap -histo outputs the 'num' column, which is the rank.
					 * If currentRunLine's num=100 
					 * (meaning there are 99 classes that use more memory thank this class) 
					 * and for the same class the previous num=101, 
					 * then we're more likely to be leaky, so increment this counter.
					 */
					if (previousRunLine.num - currentRunLine.num >= this.getRankIncreaseThreshold() )
						result.countRunsWithLeakierRank++;

					//if (currentRunLine.bytes - priorRunLine.bytes > this.getBytesIncreaseThreshold() )
					if (currentRunLine.bytes - previousRunLine.bytes > 0 )
						result.countRunsWithBytesIncrease++;
					
					if (currentRunLine.instances - previousRunLine.instances > 0 )
						result.countRunsWithInstanceCountIncrease++;
				}
				previousModel = currentModel;
			}
		}
		return result;
	}
	public long getInstancesIncreaseThreshold() {
		return instancesIncreaseThreshold;
	}
	public void setInstancesIncreaseThreshold(long instancesIncreaseThreshold) {
		this.instancesIncreaseThreshold = instancesIncreaseThreshold;
	}
	private long getBytesIncreaseThreshold() {
		return bytesIncreaseThreshold;
	}
	public void setBytesIncreaseThreshold(long bytesIncreaseThreshold) {
		this.bytesIncreaseThreshold = bytesIncreaseThreshold;
	}
	public static final Comparator<LeakResult> PCT_OF_RUNS_WITH_UPWARD_TRENDING_BYTES = new Comparator<LeakResult>() {
		/**
		 * Sort descending, so biggest leak shows up at 0 index. 
		 */
		@Override
		public int compare(LeakResult o1, LeakResult o2) {
			int rc = 0;
			double diff = o2.getPercentageOfRunsWithUpwardByteTrend() - o1.getPercentageOfRunsWithUpwardByteTrend();
			if (diff < 0 )
				rc = -1;
			else if (diff > 0) 
				rc = 1;
			else if (diff==0) {
				long bytesDiff = o2.line.bytes - o1.line.bytes;
				if (bytesDiff<0 )
					rc = -1;
				else if (bytesDiff > 0)
					rc = 1;
				else if (bytesDiff==0) {
					long instanceCountDiff = o2.line.instances - o1.line.instances;
					if (instanceCountDiff<0)
						rc = -1;
					else if (instanceCountDiff>0)
						rc = 1;
					else if (instanceCountDiff==0) {
						rc = o2.line.className.compareToIgnoreCase(o1.line.className);
					}
				}
			}
			return rc;
		}
	};
	public class LeakResult {
		private long timestamp;
		LeakResult() {
			this.timestamp = System.currentTimeMillis();
		}
		public JMapHistoLine line = null;
		public int countRunsPresent = 0;
		public int countRunsWithLeakierRank = 0;
		public int countRunsWithBytesIncrease = 0;
		public int countRunsWithInstanceCountIncrease = 0;
		
		/**
		 * The percentage of 'runs' that the bytes increase from the previous run.
		 * @return a double between 0 and 1.  Multiply the result by to get a portion of 100%.
		 */
		public double getPercentageOfRunsWithUpwardByteTrend() {
			double rc = 0;
			
			double totalRunCount = LeakySpankContext.this.getCurrentRunCount() -1;
			if (this.countRunsWithBytesIncrease == 0 || totalRunCount == 0) // avoid divide by 0 problems.
				rc = 0;
			else
				rc = this.countRunsWithBytesIncrease / totalRunCount;
			
			return rc;
		}
		/**
		 * Higher score means more likely a leak
		 * @return
		 */
		int getLeakScore() {
			return this.countRunsPresent+this.countRunsWithLeakierRank+this.countRunsWithBytesIncrease+this.countRunsWithInstanceCountIncrease;
		}
		public String humanReadable() {
			StringBuilder sb = new StringBuilder();
			 sb.append("Class=" + line.className + "\n");
			 sb.append("\ncountRunsPresent=" + this.countRunsPresent + "\n");
			 sb.append("countRunsWithRankIncrease=" + this.countRunsWithLeakierRank + "\n");
			 sb.append("countRunsWithBytesIncrease=" + this.countRunsWithBytesIncrease + "\n");
			 sb.append("countRunsWithInstanceCountIncrease=" + this.countRunsWithInstanceCountIncrease + "\n");
			 return sb.toString();
		}
		/**
		 * update values in 'this' with values from the given LeakResult
		 * @param oneToAdd
		 */
		public void update(LeakResult oneToAdd) {
			/**
			 * Add given values to tally values.
			 */
			this.countRunsPresent+= oneToAdd.countRunsPresent;
			this.countRunsWithBytesIncrease += oneToAdd.countRunsWithBytesIncrease;
			this.countRunsWithInstanceCountIncrease += oneToAdd.countRunsWithInstanceCountIncrease;
			this.countRunsWithLeakierRank += oneToAdd.countRunsWithLeakierRank;
			
			/**
			 * display current values of these, instead of outdated values.
			 */
			this.line.bytes = oneToAdd.line.bytes;
			this.line.num = oneToAdd.line.num;
			this.line.instances = oneToAdd.line.instances;
		}
		
	}
	public LeakResult[] getTopResults() {
		Model resultsForWindow = new Model();
		
		LeakResult[] suspects = this.getLeakSuspectsOrdered();
		List<LeakResult> onlyTheLeakiest = new ArrayList<LeakResult>();

		int max = Math.min(suspects.length, this.getTopNSuspects());
		
		for (int i = 0; i < max; i++) 
			onlyTheLeakiest.add(suspects[i]);
		
		return onlyTheLeakiest.toArray(  new LeakResult[]{} );
	}
	
//	public LeakResult[] getTopResults() {
//		Model resultsForWindow = new Model();
//		
//		LeakResult[] suspects = this.getLeakSuspectsOrdered();
//		List<LeakResult> onlyTheLeakiest = new ArrayList<LeakResult>();
//		
//		int startIndex = (suspects.length - this.getTopNSuspects()) -1;
//		if (startIndex < 0) startIndex = 0; 
//		for(int i = startIndex; 
//				i < suspects.length; 
//				i++) {
//			onlyTheLeakiest.add(suspects[i]);
//		}
//		return onlyTheLeakiest.toArray(  new LeakResult[]{} );
//	}
}
