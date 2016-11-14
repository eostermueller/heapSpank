package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;

/**
 * In this class, I added class names "fred" and "betty",
 * and neither class was in all 4 runs.
 * Neither of these classes had any rank increase, so this is somewhat of an easy test.
 * A more complicated test will have some rank increase, but not present in all runs.
 * Check out test TestContextWithLessCleanData2 for this challenge.
 * @author erikostermueller
 *
 */
public class TestContextWithLessCleanData {
	Model run_1 = new Model(TEST_RUN_1);
	Model run_2 = new Model(TEST_RUN_2);
	Model run_3 = new Model(TEST_RUN_3);
	Model run_4 = new Model(TEST_RUN_4);
	private static final String TEST_RUN_1 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7"
			+ " 3: 35 350 red.herring.fred"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6"
			+ " 5: 20 200 com.acme.Leak";

	private static final String TEST_RUN_2 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7"
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
		
		LeakySpankContext ldc = new LeakySpankContext(-1, 60, 4, 0);
		
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
	
	@Test
	public void canTheQueueLimitItsSize() {
		 LimitedSizeQueue<String> lsq = new LimitedSizeQueue<String>(3);
		 
		 String firstAdded = "foo";
		 String secondAdded = "bar";
		 String thirdAdded = "foobar";
		 String fourthAdded = "foobaz";
		 
		 assertEquals("sanity check", 0, lsq.size());
		 
		 lsq.add(firstAdded);
		 lsq.add(secondAdded);
		 
		 assertEquals("Can't find the 2 strings I just added", 2, lsq.size());
		 
		 lsq.add(thirdAdded);
		 assertEquals("Can't find the 3 strings I just added", 3, lsq.size());
		 
		 lsq.add(fourthAdded);
		 assertEquals("Was expecting that q size to remain at three, even after adding the 4th string.",
				 3, lsq.size());
		 
		 
		 assertEquals("Can't find the most recent element added", fourthAdded, lsq.peekLast());
		 assertEquals("Can't find the oldest item in the queue", secondAdded, lsq.peekFirst() );
		 assertTrue("Can't find the middle of the queue", lsq.contains(thirdAdded));
		 
		 //so the answer is 'yes', the queue can limit its size
	}
	
	

}
