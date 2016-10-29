# heapSpank
Quickly identifies memory leaks in the JVM's heap.
![leak-or-no-leak](https://cloud.githubusercontent.com/assets/175773/19831182/64f10ed8-9dc9-11e6-8775-07dc6cbfc276.png)


## Install
1. Install [JMeter](http://jmeter.apache.org/) -- used to run/capture output from [JAVA_HOME/bin/jmap -histo <myPid>](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH).
2. Install [this](https://jmeter-plugins.org/wiki/PageDataExtractor/) JMeter Plugin.
3. Copy [the heapSpank jar file](https://github.com/eostermueller/heapSpank/releases/download/v0.1/heapSpank-0.1.jar) to JMETER_HOME/lib/ext
4. Save [this .jmx file](https://raw.githubusercontent.com/eostermueller/heapSpank/387849c457cbf296b53c3ba48235ae43d6e895b2/src/main/resources/heapSpank.jmx) to your hard disk.  Start/restart JMeter.  Choose File / Open to open the .jmx.
5. Using JAVA_HOME/bin/jps or similar, find the pid of the Java process in which you want to detect memory leaks.
6. Find the topmost node in JMeter -- named "heapSpank".  Set the HEAPSPANK_PID to the pid from the above step.
7. Select Run / Start JMeter menu item.
8. Drill down / select "leakySpank_Graph"

## Detect Memory Leaks
heapSpank graphs the "#bytes" column from [JAVA_HOME/bin/jmap -histo <myPid>](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH).

To find your memory leak, look for lines that meet both of these criteria:

1. Have many dots over time.
2. Show an upward trend (in byte count).


