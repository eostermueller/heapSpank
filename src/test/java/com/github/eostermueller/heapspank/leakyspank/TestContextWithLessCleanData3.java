package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.JMapHistoLine;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.LimitedSizeQueue;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;

/**
 * In this class, I added class names "fred" and "betty" and "wilma"
 * and neither class was in all 4 runs
 * but wilma has some rank increase, which means rank increase in consecutive runs.
 * @author erikostermueller
 *
 */
public class TestContextWithLessCleanData3 {
	Model run_1 = new Model(TEST_RUN_1);
	Model run_2 = new Model(TEST_RUN_2);
	Model run_3 = new Model(TEST_RUN_3);
	Model run_4 = new Model(TEST_RUN_4);
	private static final String TEST_RUN_1 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 3: 35 350 red.herring.fred\n"
			+ " 4: 33 330 red.herring.wilma\n"
			+ " 5: 30 300 sun.reflect.GeneratedMethodAccessor6\n"
			+ " 6: 20 200 com.acme.Leak\n";

	private static final String TEST_RUN_2 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 3: 38 380 red.herring.wilma\n"
			+ " 3: 35 350 com.acme.Leak\n"
			+ " 4: 35 350 red.herring.fred\n"
			+ " 5: 30 300 sun.reflect.GeneratedMethodAccessor6\n"
			+ " 6: 20 200 red.herring.betty\n";
	
	private static final String TEST_RUN_3 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 2: 45 450 com.acme.Leak\n"
			+ " 3: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6\n"
			+ " 5: 20 200 red.herring.betty\n";
	
	private static final String TEST_RUN_4 = 
			  " 1: 55 550 com.acme.Leak\n"
			+ " 2: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 3: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6\n"
			+ " 5: 20 200 red.herring.wilma\n";
	
	@Test
	public void canFindLeakSuspects() {
		
		LeakySpankContext ldc = new LeakySpankContext(-1, 60, 4,0);
		
		//We are lowering the bar here, opening ourselves to more results 
		//which are very likely to clutter the graph with too many results to display.
		//...but some level of control is necessary.
		ldc.setCountPresentThreshold(2);
		ldc.setRankIncreaseThreshold(1);
		
		ldc.addJMapRun(run_1);
		run_2.getAllOrderByMostUpwardlyMobileAsComparedTo(run_1);
		
		//The above call calculates the JMapHistoLine.rankIncrease for every line in the model
		//With rankings calculated, the LDC can do its work.
		
		ldc.addJMapRun(run_2);
		run_3.getAllOrderByMostUpwardlyMobileAsComparedTo(run_2);
		
		//Ditto, comment above.
		
		ldc.addJMapRun(run_3);
		run_4.getAllOrderByMostUpwardlyMobileAsComparedTo(run_3);
		
		//Ditto, comment above.
		
		ldc.addJMapRun(run_4);
		
		LeakResult[] leakSuspects = ldc.getLeakSuspectsOrdered();
		
		assertEquals("Put a handful of single leak into LeakDectectorContext, but didn't find right count", 5, leakSuspects.length);
		for(LeakResult l : leakSuspects)
			System.out.println(l.humanReadable());
		
		LeakResult culprit = leakSuspects[leakSuspects.length-1];
		assertEquals("We accused the wrong class of being the leakiest!", "com.acme.Leak", culprit.line.className);
		
	}
	
	
	

}
