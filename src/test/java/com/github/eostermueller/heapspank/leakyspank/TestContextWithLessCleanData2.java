package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;

/**
 * In this class, I added class names "fred" and "betty" and "wilma"
 * and neither class was in all 4 runs
 * but wilma has some rank increase, which means rank increase in consecutive runs.
 * @author erikostermueller
 *
 */
public class TestContextWithLessCleanData2 {
	Model run_1 = new Model(TEST_RUN_1);
	Model run_2 = new Model(TEST_RUN_2);
	Model run_3 = new Model(TEST_RUN_3);
	Model run_4 = new Model(TEST_RUN_4);
	private static final String TEST_RUN_1 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7"
			+ " 3: 35 350 red.herring.fred"
			+ " 4: 33 330 red.herring.wilma"
			+ " 5: 30 300 sun.reflect.GeneratedMethodAccessor6"
			+ " 6: 20 200 com.acme.Leak";

	private static final String TEST_RUN_2 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7"
			+ " 3: 38 380 red.herring.wilma"
			+ " 3: 35 350 com.acme.Leak"
			+ " 4: 35 350 red.herring.fred"
			+ " 5: 30 300 sun.reflect.GeneratedMethodAccessor6"
			+ " 6: 20 200 red.herring.betty";
	
	private static final String TEST_RUN_3 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8"
			+ " 2: 45 450 com.acme.Leak"
			+ " 3: 40 400 sun.reflect.GeneratedMethodAccessor7"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6"
			+ " 5: 20 200 red.herring.betty";
	
	private static final String TEST_RUN_4 = 
			  " 1: 55 550 com.acme.Leak"
			+ " 2: 50 500 sun.reflect.GeneratedMethodAccessor8"
			+ " 3: 40 400 sun.reflect.GeneratedMethodAccessor7"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6";
	
	@Test
	public void canFindLeakSuspects() {
		
		LeakySpankContext ldc = new LeakySpankContext(-1, 60, 4,0);
		
		ldc.addJMapHistoRun(run_1);
//		run_2.getAllOrderByMostUpwardlyMobileAsComparedTo(run_1);
		
		//The above call calculates the JMapHistoLine.rankIncrease for every line in the model
		//With rankings calculated, the LDC can do its work.
		
		ldc.addJMapHistoRun(run_2);
//		run_3.getAllOrderByMostUpwardlyMobileAsComparedTo(run_2);
		
		//Ditto, comment above.
		
		ldc.addJMapHistoRun(run_3);
//		run_4.getAllOrderByMostUpwardlyMobileAsComparedTo(run_3);
		
		//Ditto, comment above.
		
		ldc.addJMapHistoRun(run_4);
		
		LeakResult[] leakSuspects = ldc.getLeakSuspectsOrdered();
		
		assertEquals("Put a single leak into LeakDectectorContext, but didn't find right count", 1, leakSuspects.length);
	}
	
	

}
