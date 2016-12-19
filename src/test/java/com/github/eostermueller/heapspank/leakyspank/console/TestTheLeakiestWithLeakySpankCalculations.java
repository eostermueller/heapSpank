package com.github.eostermueller.heapspank.leakyspank.console;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.TheLeakiest;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.leakyspank.Model;

public class TestTheLeakiestWithLeakySpankCalculations {
	private static final String RUN_A_LINE_1 = " 3512: 1 15 sun.reflect.GeneratedMethodAccessor2";
	private static final String RUN_A_LINE_2 = " 3513: 1 14 sun.reflect.GeneratedMethodAccessor1";
	private static final String RUN_A_LINE_3 = " 3511: 1 16 sun.reflect.GeneratedMethodAccessor3";

	private static final String RUN_B_LINE_1 = " 3514: 10 160 sun.reflect.GeneratedMethodAccessor1";
	private static final String RUN_B_LINE_2 = " 3513: 5 150 sun.reflect.GeneratedMethodAccessor2";

	private static final String RUN_C_LINE_1 = " 3514: 10 160 sun.reflect.GeneratedMethodAccessor1";
	private static final String RUN_C_LINE_2 = " 3512: 1 15 sun.reflect.GeneratedMethodAccessor2";
	//private static final String RUN_C_LINE_3 = " 3511: 1 16 sun.reflect.GeneratedMethodAccessor3";

	@Test
	public void testSimpleResultTransfer() {
		Model run_B = new Model();
		JMapHistoLine line1b = new JMapHistoLine(RUN_B_LINE_1);
		JMapHistoLine line2b = new JMapHistoLine(RUN_B_LINE_2);
		run_B.put(line1b);
		run_B.put(line2b);
		Model run_C = new Model();
		JMapHistoLine line1c = new JMapHistoLine(RUN_C_LINE_1);
		JMapHistoLine line2c = new JMapHistoLine(RUN_C_LINE_2);
		run_C.put(line1c);
		run_C.put(line2c);

		
		//long pid, int interval_in_seconds, int interval_count, int topNSupsects
		LeakySpankContext ctx = new LeakySpankContext(1,1,1,5);
		ctx.addJMapHistoRun(run_B);
		ctx.addJMapHistoRun(run_C);
		
		LeakResult[] results = ctx.getTopResults();
		TheLeakiest theLeakiest = new TheLeakiest(5);
		theLeakiest.addLeakResults(results);
		
		LeakResult[] allResults = theLeakiest.getTopResults();
		
		assertEquals("Put in 2 JMap histo lines, should have gotten 2 back out", 2, allResults.length);

		assertEquals(allResults[0].line.className , "sun.reflect.GeneratedMethodAccessor1");
		assertEquals(160, allResults[0].line.bytes);
		assertEquals(1, allResults[0].countRunsPresent);
		assertEquals(0, allResults[0].countRunsWithBytesIncrease);
		assertEquals(0, allResults[0].countRunsWithInstanceCountIncrease);
		assertEquals(0, allResults[0].countRunsWithLeakierRank);
		
		assertEquals(allResults[1].line.className , "sun.reflect.GeneratedMethodAccessor2");
		assertEquals(15, allResults[1].line.bytes);
		assertEquals(1, allResults[1].countRunsPresent);
		assertEquals(0, allResults[1].countRunsWithBytesIncrease);
		assertEquals(0, allResults[1].countRunsWithInstanceCountIncrease);
		assertEquals(1, allResults[1].countRunsWithLeakierRank);

	}
	
	@Test
	public void testThreeRuns() {
		JMapHistoLine line1a = new JMapHistoLine(RUN_A_LINE_1);
		JMapHistoLine line2a = new JMapHistoLine(RUN_A_LINE_2);
		JMapHistoLine line3a = new JMapHistoLine(RUN_A_LINE_3);
		Model run_A = new Model();
		run_A.put(line1a);
		run_A.put(line2a);
		run_A.put(line3a);

		JMapHistoLine line1b = new JMapHistoLine(RUN_B_LINE_1);
		JMapHistoLine line2b = new JMapHistoLine(RUN_B_LINE_2);
		Model run_B = new Model();
		run_B.put(line1b);
		run_B.put(line2b);
		JMapHistoLine line1c = new JMapHistoLine(RUN_C_LINE_1);
		JMapHistoLine line2c = new JMapHistoLine(RUN_C_LINE_2);
//		JMapHistoLine line3c = new JMapHistoLine(RUN_C_LINE_3);
		Model run_C = new Model();
		run_C.put(line1c);
		run_C.put(line2c);
	//	run_C.put(line3c);
		
		//long pid, int interval_in_seconds, int interval_count, int topNSupsects
		LeakySpankContext ctx = new LeakySpankContext(1,1,5,5);
		ctx.addJMapHistoRun(run_A);
		ctx.addJMapHistoRun(run_B);
		ctx.addJMapHistoRun(run_C);
		
		LeakResult[] results = ctx.getLeakSuspectsOrdered();
		TheLeakiest theLeakiest = new TheLeakiest(5);
		theLeakiest.addLeakResults(results);
		
		LeakResult[] allResults = theLeakiest.getTopResults();
		
		assertEquals("Only put 3 JMap histo lines in the latest run , so should have gotten exactly 2 back out", 2, allResults.length);
		
		assertEquals("sun.reflect.GeneratedMethodAccessor1", allResults[0].line.className  );
		//assertEquals(16, allResults[0].line.bytes);
		assertEquals(2, allResults[0].countRunsPresent);
		assertEquals(1, allResults[0].countRunsWithBytesIncrease);
		assertEquals(1, allResults[0].countRunsWithInstanceCountIncrease);
		assertEquals(0, allResults[0].countRunsWithLeakierRank);

		assertEquals("sun.reflect.GeneratedMethodAccessor2", allResults[1].line.className );
		assertEquals(15, allResults[1].line.bytes);
		assertEquals(2, allResults[1].countRunsPresent);
		assertEquals(1, allResults[1].countRunsWithBytesIncrease);
		assertEquals(1, allResults[1].countRunsWithInstanceCountIncrease);
		assertEquals(1, allResults[1].countRunsWithLeakierRank);
	}

}
