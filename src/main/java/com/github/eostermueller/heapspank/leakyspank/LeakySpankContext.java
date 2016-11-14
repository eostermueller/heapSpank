package com.github.eostermueller.heapspank.leakyspank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;

public class LeakySpankContext {

	private static final String LEAKY_SPANK = "leakySpank: ";
	private int currentRunCount  = 0;
	private LimitedSizeQueue<String> debugDisplayQ;
	public void incrementRunCount() {
		this.currentRunCount++;
	}
	public void setDisplayQueue(LimitedSizeQueue<String> display) {
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
		
		this.recentJMapRuns = new LimitedSizeQueue<Model>(this.runCountPerWindow);
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
		if (this.getDebugDisplayQ()!=null)
			this.getDebugDisplayQ().push(msg);
	}
	public List<LeakResult> getLeakSuspectsUnOrdered() {
		// TODO Auto-generated method stub
		Model mostRecent = this.recentJMapRuns.peekLast();
		debug(String.format("recent jmapRuns Count %d", this.recentJMapRuns.size()) );
		List<LeakResult> results = new ArrayList<LeakResult>();
		if (mostRecent!=null) {
			for(JMapHistoLine line : mostRecent.getAll())     {
				results.add(tallyLeakActivity(line));
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
		//Collections.sort( suspects, LeakySpankContext.LEAK_SCORE_ORDER);
		Collections.sort( suspects, LeakySpankContext.PCT_OF_RUNS_WITH_UPWARD_TRENDING_BYTES);
		return (LeakResult[]) suspects.toArray( new LeakResult[0]);
	}
	/**
	 * Given a single line from JMapHisto results, 
	 * tall counts of various stats.
	 * @param line
	 * @return
	 */
	public LeakResult tallyLeakActivity(JMapHistoLine line) {
		LeakResult result = new LeakResult();
		result.line  = line;
		JMapHistoLine priorRunLine = null;
		this.debug(String.format("in tally jmh runs [%d]", this.recentJMapRuns.size()));
		for(Model run : this.recentJMapRuns) {
			int visitedCount = 0;
			JMapHistoLine currentRunLine = run.get(line.className);
			
			if (currentRunLine !=null && currentRunLine.visited )
				visitedCount++;
			if (currentRunLine !=null && !currentRunLine.visited ) {
				result.countRunsPresent++;

				if (priorRunLine != null) {
					if (currentRunLine.timestampNanos < priorRunLine.timestampNanos)
						throw new RuntimeException("Perhaps iterating in the wrong direction?");
					
					/**
					 * jmap -histo outputs the 'num' column, which is the rank.
					 * If this line/class's num=100 (meaning there are 99 classes that use more memory thank this class) 
					 * and for the same class the previous num=101, then we're more likely to be leaky, so increment this counter.
					 */
					if (priorRunLine.num - currentRunLine.num >= this.getRankIncreaseThreshold() )
						result.countRunsWithLeakierRank++;

					//if (currentRunLine.bytes - priorRunLine.bytes > this.getBytesIncreaseThreshold() )
					if (currentRunLine.bytes - priorRunLine.bytes >= 0 )
						result.countRunsWithBytesIncrease++;
					
					if (currentRunLine.instances - priorRunLine.instances > this.getInstancesIncreaseThreshold() )
						result.countRunsWithInstanceCountIncrease++;

					currentRunLine.visited = true;
					//				if (currentRunLine.rankIncrease > this.getRankIncreaseThreshold() )
//						result.countRunsWithRankIncrease++;
				}
				priorRunLine = currentRunLine;
			}				
			this.debug(String.format("visited count [%d]", visitedCount));
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
	static final Comparator<LeakResult> LEAK_SCORE_ORDER = new Comparator<LeakResult>() {
		public int compare(LeakResult l1, LeakResult l2) {
			int compareResult = l1.getLeakScore() - l2.getLeakScore();
			//if the score is tied, then the one with the larger byte count wins.
			if (compareResult==0) {
				if (l1.line.bytes < l2.line.bytes)
					compareResult = -1;
				else
					if (l1.line.bytes > l2.line.bytes)
						compareResult = 1;
			}
			return ( compareResult );
		}
	};
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
			
			
			double totalRunCount = LeakySpankContext.this.getCurrentRunCount();
			if (this.countRunsWithBytesIncrease == 0 || totalRunCount == 0)
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
