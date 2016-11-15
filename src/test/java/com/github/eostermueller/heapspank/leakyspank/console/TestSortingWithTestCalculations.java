package com.github.eostermueller.heapspank.leakyspank.console;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.leakyspank.Model;

public class TestSortingWithTestCalculations {
	/**
	 * one output line from jmap -histo <myPid>
	 */
	private static final String RUN_A_LINE_1 = " 3512: 1 15 sun.reflect.GeneratedMethodAccessor2";
	/**
	 * one output line from jmap -histo <myPid>
	 */
	private static final String RUN_A_LINE_2 = " 3513: 1 14 sun.reflect.GeneratedMethodAccessor1";
	/**
	 * one output line from jmap -histo <myPid>
	 */
	private static final String RUN_A_LINE_3 = " 3511: 1 16 sun.reflect.GeneratedMethodAccessor3";

	
	@Test
	public void testTheLeakiestWithLeakDataDecidedByTest() {
		JMapHistoLine line1a = new JMapHistoLine(RUN_A_LINE_1);
		JMapHistoLine line2a = new JMapHistoLine(RUN_A_LINE_2);
		JMapHistoLine line3a = new JMapHistoLine(RUN_A_LINE_3);
		
		//long pid, int interval_in_seconds, int interval_count, int topNSupsects
		LeakySpankContext ctx = new LeakySpankContext(1,1,1,5);

		int runCount = 5;
		for (int i = 0; i < 5; i++)
			ctx.incrementRunCount();
		
		List<LeakResult> leakCandidates = new ArrayList<LeakResult>();
		leakCandidates.add( ctx.tallyLeakActivityFromPreviousRuns(line1a));
		leakCandidates.add( ctx.tallyLeakActivityFromPreviousRuns(line2a));
		leakCandidates.add( ctx.tallyLeakActivityFromPreviousRuns(line3a));
		
		/**
		 * Sorting will be based on the following src/test/java calculations,
		 * NOT calculations made by src/main/java code.
		 */
		
		leakCandidates.get(1).countRunsPresent = runCount;
		leakCandidates.get(1).countRunsWithBytesIncrease = runCount;
		leakCandidates.get(1).countRunsWithInstanceCountIncrease = runCount;
		leakCandidates.get(1).countRunsWithLeakierRank = runCount;
		String keyForLeakiest = leakCandidates.get(1).line.className;
		leakCandidates.get(0).countRunsPresent = runCount-1;
		leakCandidates.get(0).countRunsWithBytesIncrease = runCount-1;
		leakCandidates.get(0).countRunsWithInstanceCountIncrease = runCount-1;
		leakCandidates.get(0).countRunsWithLeakierRank = runCount-1;
		String keyForSecondLeakiest = leakCandidates.get(0).line.className;
		leakCandidates.get(2).countRunsPresent = runCount-2;
		leakCandidates.get(2).countRunsWithBytesIncrease = runCount-2;
		leakCandidates.get(2).countRunsWithInstanceCountIncrease = runCount-2;
		leakCandidates.get(2).countRunsWithLeakierRank = runCount-2;
		String keyForThirdLeakiest = leakCandidates.get(2).line.className;
		
		
		TheLeakiest theLeakiest = new TheLeakiest(5);
		
		theLeakiest.addLeakResults( leakCandidates.toArray( new LeakResult[]{}));
		
		LeakResult[] allResults = theLeakiest.getTopResults();
		
		assertEquals("Put put in 3 JMap histo lines, should have gotten 3 back out", 3, allResults.length);
		
		assertEquals(keyForLeakiest, allResults[0].line.className);
		assertEquals(runCount, allResults[0].countRunsPresent);
		assertEquals(runCount, allResults[0].countRunsWithBytesIncrease);
		assertEquals(runCount, allResults[0].countRunsWithInstanceCountIncrease);
		assertEquals(runCount, allResults[0].countRunsWithLeakierRank);

		assertEquals(keyForSecondLeakiest, allResults[1].line.className);
		assertEquals(runCount-1, allResults[1].countRunsPresent);
		assertEquals(runCount-1, allResults[1].countRunsWithBytesIncrease);
		assertEquals(runCount-1, allResults[1].countRunsWithInstanceCountIncrease);
		assertEquals(runCount-1, allResults[1].countRunsWithLeakierRank);

		assertEquals(keyForThirdLeakiest, allResults[2].line.className);
		assertEquals(runCount-2, allResults[2].countRunsPresent);
		assertEquals(runCount-2, allResults[2].countRunsWithBytesIncrease);
		assertEquals(runCount-2, allResults[2].countRunsWithInstanceCountIncrease);
		assertEquals(runCount-2, allResults[2].countRunsWithLeakierRank);

	}
	

}
