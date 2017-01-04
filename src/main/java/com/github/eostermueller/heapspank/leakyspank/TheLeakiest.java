package com.github.eostermueller.heapspank.leakyspank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;

public class TheLeakiest {

	private Map<String, LeakResult> theLeakiestMap = new Hashtable<String, LeakResult>();
	private List<LeakResult> theLeakiestList = new ArrayList<LeakResult>();
	
//	public Map<String, LeakResult> getTheLeakiest() {
//		return theLeakiestMap;
//	}
//	public void setTheLeakiest(Map<String, LeakResult> theLeakiest) {
//		this.theLeakiestMap = theLeakiest;
//	}
	private int displayRowCount = 10;

	public TheLeakiest(int val) {
		this.displayRowCount = val;
	}
	public LeakResult get(String key) {
		return this.theLeakiestMap.get(key);
	}
	private void addLeakResult(LeakResult oneToAdd) {
		LeakResult found = this.get(oneToAdd.line.className);
		if (found==null) 
			this.addLeakResult_(oneToAdd);
		else
			found.update(oneToAdd);
	}
	private void addLeakResult_(LeakResult result) {
		this.theLeakiestList.add(result);
		this.theLeakiestMap.put(result.line.className, result);
	}
	public void addLeakResults(LeakResult[] results) {
		
		for(LeakResult oneToAdd : results)
			this.addLeakResult(oneToAdd);
		/**
		 * The new items might have pushed us over our limit (getDisplayRowCount()).
		 * So, sort the newly updated list and remove everything 
		 * past the getDisplayRowCount().
		 */
		sortAndPrune();
	}
	/** If we've added more than the getDisplayRowCount() size parameter,
	 * discover which ones need to be deleted by sorting and
	 * removing 
	 */
	private void sortAndPrune() {
		
		Collections.sort( this.theLeakiestList, LeakySpankContext.PCT_OF_RUNS_WITH_UPWARD_TRENDING_BYTES );
		
		for(int i = this.theLeakiestList.size()-1;
				i > this.getDisplayRowCount()-1;
				i--) {
			this.theLeakiestMap.remove(this.theLeakiestList.get(i).line.className);
			this.theLeakiestList.remove(i);
		}
	}
	
	public LeakResult[] getTopResults() {
		return this.theLeakiestList.toArray( new LeakResult[]{});
	}	
	public int getDisplayRowCount() {
		return this.displayRowCount; 
	}
	public void setDisplayRowCount(int rows) {
		this.displayRowCount = rows;
	}
	
}
