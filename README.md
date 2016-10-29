# heapSpank
Quickly identifies memory leaks in the JVM's heap.

![leak-or-no-leak](https://cloud.githubusercontent.com/assets/175773/19831182/64f10ed8-9dc9-11e6-8775-07dc6cbfc276.png)


## Install
1. Install [JMeter](http://jmeter.apache.org/) -- used to run/capture output from [JAVA_HOME/bin/jmap -histo <myPid>](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH).
2. Install [this](https://jmeter-plugins.org/wiki/PageDataExtractor/) JMeter Plugin.
3. Copy [the heapSpank jar file](https://github.com/eostermueller/heapSpank/releases/download/v0.2/heapSpank-0.2.jar) to JMETER_HOME/lib/ext
4. Save [this .jmx file](https://raw.githubusercontent.com/eostermueller/heapSpank/9aa541de4543e18689bea5a1d9fa256356ba11a7/src/main/resources/heapSpank.jmx) to your hard disk.  Start/restart JMeter.  Choose File / Open to open the .jmx.
5. Using JAVA_HOME/bin/jps or similar, find the pid of the Java process in which you want to detect memory leaks.
6. Find the topmost node in JMeter -- named "heapSpank".  Set the HEAPSPANK_PID to the pid from the above step.
7. Select Run / Start JMeter menu item.
8. Drill down / select "leakySpank_Graph"
9. Graph will remain blank for 60-90 seconds, and will refresh every 60-90 seconds (based on default parameters).

## Detect Memory Leaks
heapSpank graphs the "#bytes" column from [JAVA_HOME/bin/jmap -histo <myPid>](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH).

To find your memory leak, look for lines that meet both of these criteria:

1. Have many dots over time.
2. Show an upward trend (in byte count).

## Headless heapSpank
To detect memory leaks in a JVM on a headless OS, just run heapSpank on the same machine as the JVM being monitored for memory leaks, where heapSpank writes data to a .jtl file.  To view results after the test, copy the .jtl file back to the machine with the GUI, and view in heapSpank.

Here are the details:

1. With heapSpank.jmx on a GUI machine:
 * As shown in the screenshot below, configure file name where heapSpank writes text results.  leakySpank.jtl is used in this example.
 * Configure the HEAPSPANK_PID with the PID of the JVM on the **headless machine**.
 * Transfer this newly edited heapSpank.jmx to the remote machine.
2. Install heapSpank on headless OS, using the heapSpank.jmx edits from the above step.
3. Start heapSpank headlessly, like this:  $JMETER_HOME/bin/jmeter.sh -n -t heapSpank.jmx
  * ...confirm it is working by looking for new data added to "tail -f leakySpank.jtl".
4. Let heapSpank run for 10-20 minutes.
5. To view the results, transfer leakySpank.jtl back to a machine with a GUI where heapSpank is installed.
6. Use the Browse button, below, to locate the newly created leakySpank.jtl file....and your results will render in the graph.



![jtl_storage_annotated_01](https://cloud.githubusercontent.com/assets/175773/19831637/ab5b82da-9dd4-11e6-98eb-16310686439d.png)




## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.
