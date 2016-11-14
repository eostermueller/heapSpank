package com.github.eostermueller.heapspank.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory that names each thread with the provided group name.
 * <p>
 * Except lines with "elarson modified", copied from code 
 * Executors$DefaultThreadFactory -- Doug Lea, Copyright Oracle, et al.
 */
public class GroupNameThreadFactory implements ThreadFactory {
    private String groupname; /* elarson modified */
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public GroupNameThreadFactory(String groupname) {
        this.groupname = groupname; /* elarson modified */
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                              Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +
                       this.groupname + /* elarson modified */
                       poolNumber.getAndIncrement() +
                     "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                              namePrefix + threadNumber.getAndIncrement(),
                              0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}