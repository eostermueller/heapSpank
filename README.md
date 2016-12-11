heapSpank detects Java memory leaks fast!  Just [download the jar](https://github.com/eostermueller/heapSpank/releases/download/v0.6/heapSpank-0.6.jar) and point to the process id (pid) of a running JVM like this:

    java -jar heapSpank-0.6.jar 8173

Using data from [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH), heapSpank shows the percentage of time that byte counts are on the rise for the 10 classes most likely to be leaking.

![Quick Memory Leak Detection](https://cloud.githubusercontent.com/assets/175773/21078075/63990eb2-bf27-11e6-8b5e-5de636302fa6.png)



## Notes
* [Apache 2.0 license](https://github.com/eostermueller/heapSpank/blob/master/LICENSE.txt)
* Make sure JAVA_HOME/bin is in your path (for jmap executable).
* For best results, warm up an application for a few minutes at 'steady state' before launching heapSpank.
* Support for MS-Windows coming soon! Tested on Linux and MacOs with HotSpot JDK (JRE is not enough).
* Small leaks, as well as large ones, are identified.
* Do you run multi-hour tests to identify memory leak suspects?  No more!  heapSpank identifes leak suspects in just minutes.

## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.

## Configuration
A configuration file is not yet available, so parameter tweaking must be done via code.  Just add your configuration in a [subclass](https://github.com/eostermueller/heapSpank/blob/master/src/main/java/com/github/eostermueller/heapspank/leakyspank/console/FifteenSecondJMapHistoInterval.java) of [DefaultConfig](https://github.com/eostermueller/heapSpank/blob/master/src/main/java/com/github/eostermueller/heapspank/leakyspank/console/DefaultConfig.java) and pass the name of your subclass on the command line using the '-config' option.  By default, jmap -histo is invoked every 5 seconds.  The following slows this down to every 15 seconds.

    java -jar heapSpank-0.6.jar 8173 -config com.github.eostermueller.heapspank.leakyspank.console.FifteenSecondJMapHistoInterval

## Forum
Got questions or feedback?  Discuss heapSpank by sending email to forum at heapSpank@googlegroups.com.  Forum history available [here](https://groups.google.com/forum/#!forum/heapspank).
