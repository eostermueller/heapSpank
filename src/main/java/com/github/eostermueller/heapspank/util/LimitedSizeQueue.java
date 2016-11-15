package com.github.eostermueller.heapspank.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Stolen from http://stackoverflow.com/questions/5498865/size-limited-queue-that-holds-last-n-elements-in-java
 * @author erikostermueller
 *
 * @param <E>
 */
//public class LimitedSizeQueue<E> extends LinkedList<E> {
public class LimitedSizeQueue<E> extends ConcurrentLinkedQueue<E> {
    private int limit;

    public LimitedSizeQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
           super.remove();
        }
        return added;
    }
    public E peekFirst() {
    	Iterator i = this.iterator();
    	E item = null;
    	if (i.hasNext())
    		item = (E) i.next();
    	return item;
    }
    public E peekLast() {
    	Iterator i = this.iterator();
    	E item = null;
    	while(i.hasNext()) {
    		item = (E) i.next();
    	}
    	return item;
    }

}


