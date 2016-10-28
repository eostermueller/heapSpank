package com.github.eostermueller.heapspank.leakyspank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeakySpankContext {

	private int currentRunCount  = 0;
	public void incrementRunCount() {
		this.currentRunCount++;
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
	private int rankIncreaseThreshold = 1;
	private int countPresentThreshold = 0;
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
		this.setRankIncreaseThreshold(0); //if JMap -histo 'num' is the same or higher than the previous run, then increment LeakResult.countRunsWithRankIncrease
		
	}
	public void addJMapRun(Model m) {
		this.recentJMapRuns.add(m);
	}
	public List<LeakResult> getLeakSuspectsUnOrdered() {
		// TODO Auto-generated method stub
		Model mostRecent = this.recentJMapRuns.peekLast();
		List<LeakResult> results = new ArrayList<LeakResult>();
		for(JMapHistoLine line : mostRecent.getAll())     {
			results.add(getResult(line));
		}
		return results;
	}
	/**
	 * Ordered from least likely to most likely a leak.
	 * @return
	 */
	public LeakResult[] getLeakSuspectsOrdered() {
		List suspects = this.getLeakSuspectsUnOrdered();
		Collections.sort( suspects, LeakySpankContext.LEAK_SCORE_ORDER);
		return (LeakResult[]) suspects.toArray( new LeakResult[0]);
	}
	public LeakResult getResult(JMapHistoLine line) {
		LeakResult result = new LeakResult();
		result.line  = line;
		JMapHistoLine priorRunLine = null;
		for(Model run : this.recentJMapRuns) {
			JMapHistoLine currentRunLine = run.get(line.className);
			if (currentRunLine !=null) {
				result.countRunsPresent++;

				if (priorRunLine != null) {
					if (priorRunLine.num - currentRunLine.num >= this.getRankIncreaseThreshold() )
						result.countRunsWithRankIncrease++;

					if (currentRunLine.bytes - priorRunLine.bytes > this.getBytesIncreaseThreshold() )
						result.countRunsWithBytesIncrease++;
					
					if (currentRunLine.instances - priorRunLine.instances > this.getInstancesIncreaseThreshold() )
						result.countRunsWithInstanceCountIncrease++;

					//				if (currentRunLine.rankIncrease > this.getRankIncreaseThreshold() )
//						result.countRunsWithRankIncrease++;
				}
				priorRunLine = currentRunLine;
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
	public static class LeakResult {
		public JMapHistoLine line = null;
		int countRunsPresent = 0;
		int countRunsWithRankIncrease = 0;
		int countRunsWithBytesIncrease = 0;
		int countRunsWithInstanceCountIncrease = 0;
		/**
		 * Higher score means more likely a leak
		 * @return
		 */
		int getLeakScore() {
			return this.countRunsPresent+this.countRunsWithRankIncrease+this.countRunsWithBytesIncrease+this.countRunsWithInstanceCountIncrease;
		}
		public String humanReadable() {
			StringBuilder sb = new StringBuilder();
			 sb.append("Class=" + line.className + "\n");
			 sb.append("\ncountRunsPresent=" + this.countRunsPresent + "\n");
			 sb.append("countRunsWithRankIncrease=" + this.countRunsWithRankIncrease + "\n");
			 sb.append("countRunsWithBytesIncrease=" + this.countRunsWithBytesIncrease + "\n");
			 sb.append("countRunsWithInstanceCountIncrease=" + this.countRunsWithInstanceCountIncrease + "\n");
			 return sb.toString();
		}
	}
	
}
