# heapSpank
Quickly identifies memory leaks in the JVM's heap.

## Install
1. Install [http://jmeter.apache.org/ JMeter] -- used to run/capture output from [https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH JAVA_HOME/bin/jmap -histo <myPid>].
2. Install [https://jmeter-plugins.org/wiki/PageDataExtractor/ this] JMeter Plugin.
3. Copy [this jar file https://github.com/eostermueller/heapSpank/releases/download/v0.1/heapSpank-0.1.jar] to JMETER_HOME/lib/ext
4. Save this .jmx file to your hard disk.  Start JMeter and open this .jmx file.
5. Using JAVA_HOME/bin/jps or similar, find the pid of the Java process in which you want to detect memory leaks.
6. Find the topmost node in JMeter -- named "heapSpank".  Set the HEAPSPANK_PID to the pid from the above step.
7. Select Run / Start JMeter menu item.
8. Drill down / select "leakySpank_Graph"

## Detect Memory Leaks
These charts graph the "#bytes" column from [https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH JAVA_HOME/bin/jmap -histo <myPid>]
Memory leaks show up as lines that
1. Have many dots
2. Show an upward trend in byte count.


