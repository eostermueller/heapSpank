package com.github.eostermueller.heapspank.leakyspank;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.github.eostermueller.heapspank.util.LimitedSizeQueue;

public class TestLimitedSizeQueue {

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
	@Test
	public void canPredictOrderWhenIterating() {
		 LimitedSizeQueue<String> lsq = new LimitedSizeQueue<String>(3);
		 
		 String firstAdded = "foo";
		 String secondAdded = "bar";
		 String thirdAdded = "foobar";
		 
		 lsq.add(firstAdded);
		 lsq.add(secondAdded);
		 lsq.add(thirdAdded);
		 
		 Iterator<String> i = lsq.iterator();
		 int count = 0;
		 while(i.hasNext()) {
			 switch(++count) {
			 	case 1:
			 		assertEquals(firstAdded, i.next());
			 		break;
			 	case 2:
			 		assertEquals(secondAdded, i.next());
			 		break;
			 	case 3:
			 		assertEquals(thirdAdded, i.next());
			 		break;
			 	default:
			 		fail();
			 }
		 }
	}
}
