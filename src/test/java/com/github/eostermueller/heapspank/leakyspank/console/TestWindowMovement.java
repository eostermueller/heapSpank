package com.github.eostermueller.heapspank.leakyspank.console;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.TheLeakiest;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.leakyspank.Model;

public class TestWindowMovement {
	private static final String[] LINES_FROM_12_JMAP_HISTO_RUNS = { 
			" 12:    1  1 com.foo.Bar",
			" 11:    2  2 com.foo.Bar",
			" 10:    3  3 com.foo.Bar",
			"  9:    4  4 com.foo.Bar",
			"  8:    5  5 com.foo.Bar",
			"  7:    6  6 com.foo.Bar",
			"  6:    7  7 com.foo.Bar",
			"  5:    8  8 com.foo.Bar",
			"  4:    9  9 com.foo.Bar",
			"  3:   10 10 com.foo.Bar",
			"  2:   11 11 com.foo.Bar",
			"  1:   12 12 com.foo.Bar"
	};
	List<Model> allJMapHistoRuns = new ArrayList<Model>();
	LeakySpankContext ctx = null;
	TheLeakiest theLeakiest = null;
	
	@Before
	public void setup() {
		
		this.theLeakiest = new TheLeakiest(10);
		
		long pid = 9999;
		int interval_in_seconds = 15;
		int interval_count  = 4;
		int topNSuspects = 2;
		
		ctx = new LeakySpankContext(
				pid,
				interval_in_seconds,
				interval_count,
				topNSuspects
				);
		
		for(String s : LINES_FROM_12_JMAP_HISTO_RUNS) {
			Model m = new Model(s);
			this.allJMapHistoRuns.add( m );
		}
	}
	
	@Test
	public void testWindows() {
		
		ctx.addJMapHistoRun( allJMapHistoRuns.get(0)); //this first one doesn't get counted.....just the delta between it and the next one
		ctx.addJMapHistoRun( allJMapHistoRuns.get(1));
		ctx.addJMapHistoRun( allJMapHistoRuns.get(2));
		ctx.addJMapHistoRun( allJMapHistoRuns.get(3));
		ctx.addJMapHistoRun( allJMapHistoRuns.get(4));
		assertEquals(5, ctx.getCurrentRunCount());
		
		LeakResult[] r = ctx.getTopResults();
		this.theLeakiest.addLeakResults(r);
		
		LeakResult bar = this.theLeakiest.get("com.foo.Bar");
		assertEquals(4, bar.countRunsPresent);
		assertEquals(4, bar.countRunsWithBytesIncrease);
		assertEquals(4, bar.countRunsWithInstanceCountIncrease);
		assertEquals(4, bar.countRunsWithInstanceCountIncrease);
		assertEquals( (double)1.0, bar.getPercentageOfRunsWithUpwardByteTrend(), .005 );
		
		ctx.addJMapHistoRun( allJMapHistoRuns.get(5));
		ctx.addJMapHistoRun( allJMapHistoRuns.get(6));
		ctx.addJMapHistoRun( allJMapHistoRuns.get(7));
		ctx.addJMapHistoRun( allJMapHistoRuns.get(8));
		assertEquals(9, ctx.getCurrentRunCount());
		
		r = ctx.getTopResults();
		this.theLeakiest.addLeakResults(r);
		
		bar = this.theLeakiest.get("com.foo.Bar");
		assertEquals(8, bar.countRunsPresent);
		assertEquals(8, bar.countRunsWithBytesIncrease);
		assertEquals(8, bar.countRunsWithInstanceCountIncrease);
		assertEquals(8, bar.countRunsWithInstanceCountIncrease);
		assertEquals( (double)1, bar.getPercentageOfRunsWithUpwardByteTrend(), .005 );

//		ctx.addJMapHistoRun( allJMapHistoRuns.get(9));
//		ctx.addJMapHistoRun( allJMapHistoRuns.get(10));
//		ctx.addJMapHistoRun( allJMapHistoRuns.get(11));
		
	}

}
