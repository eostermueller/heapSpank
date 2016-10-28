package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.LimitedSizeQueue;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;

public class TestContext {
	Model run_1 = new Model(TEST_RUN_1);
	Model run_2 = new Model(TEST_RUN_2);
	Model run_3 = new Model(TEST_RUN_3);
	Model run_4 = new Model(TEST_RUN_4);
	private static final String TEST_RUN_1 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 3: 30 300 sun.reflect.GeneratedMethodAccessor6\n"
			+ " 4: 20 200 com.acme.Leak\n";

	private static final String TEST_RUN_2 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 2: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 3: 35 350 com.acme.Leak\n"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6\n";
	
	private static final String TEST_RUN_3 = 
			  " 1: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 2: 45 450 com.acme.Leak\n"
			+ " 3: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6\n";
	
	private static final String TEST_RUN_4 = 
			  " 1: 55 550 com.acme.Leak\n"
			+ " 2: 50 500 sun.reflect.GeneratedMethodAccessor8\n"
			+ " 3: 40 400 sun.reflect.GeneratedMethodAccessor7\n"
			+ " 4: 30 300 sun.reflect.GeneratedMethodAccessor6\n";
	
	@Test
	public void canFindLeakSuspects() {
		
		LeakySpankContext ldc = new LeakySpankContext(-1, 60, 4, 0);
		
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
		
		for(LeakResult l : leakSuspects)
			System.out.println(l.humanReadable());
		
		assertEquals("Put a four suspected leaks into LeakDectectorContext, but didn't find right count", 4, leakSuspects.length );
		
		LeakResult culprit = leakSuspects[leakSuspects.length-1];
		assertEquals("We accused the wrong class of being the leakiest!", "com.acme.Leak", culprit.line.className);
	}

	@Test
	public void canTheLinkedListIterateForward() {
		 LimitedSizeQueue<String> lsq = new LimitedSizeQueue<String>(3);
		 
		 String firstAdded = "foo";
		 String secondAdded = "bar";
		 String thirdAdded = "foobar";
		 String fourthAdded = "foobaz";
		 
		 lsq.add(firstAdded);
		 lsq.add(secondAdded);
		 lsq.add(thirdAdded);
		 lsq.add(fourthAdded);
		 
		 int count = 0;
		 for(String s : lsq) {
			 
			 switch(++count) {
			 case 1:
				 assertEquals("'for' loop iteration unable to find the earliest String added",secondAdded, s);
				 break;
			 case 2:
				 assertEquals("'for' loop iteration unable to find second earliest String added",thirdAdded, s);
				 break;
			 case 3:
				 assertEquals("'for' loop iteration unable to find most recent String added",fourthAdded, s);
				 break;
			 default:
				 fail("'for' loop is not behaving as expected on a linked list");
				 break;
			
			 }
			 
			 
			 //So the answer is yes.  The 'for' loop iterates from the oldest to the newest items in the queue.
		 }
		
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
