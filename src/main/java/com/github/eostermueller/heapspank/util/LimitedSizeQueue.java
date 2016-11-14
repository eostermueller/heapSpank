package com.github.eostermueller.heapspank.util;

import java.util.LinkedList;

/**
 * Stolen from http://stackoverflow.com/questions/5498865/size-limited-queue-that-holds-last-n-elements-in-java
 * @author erikostermueller
 *
 * @param <E>
 */
public class LimitedSizeQueue<E> extends LinkedList<E> {
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

}


