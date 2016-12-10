heapSpank detects memory leaks fast!  Just point to the process id (pid) of a running process like this:

    java -jar heapSpank-0.6.jar 81731

Using data from [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH), heapSpank shows the percentage of time that byte counts are on the rise for the 10 classes most likely to be leaking.

![Quick Memory Leak Detection](https://cloud.githubusercontent.com/assets/175773/20299273/da86b044-aadf-11e6-98c9-0658af68ad85.png)



## Notes
* Make sure JAVA_HOME/bin is in your path (for jmap executable).
* For best results, warm up an application for a few minutes at 'steady state' before launching heapSpank.
* Support for MS-Windows coming soon! Tested on Linux and MacOs with HotSpot JDK (JRE is not enough).
* Small leaks, as well as large ones, are identified.

## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.
