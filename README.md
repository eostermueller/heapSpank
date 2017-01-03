**Download the executable jar**: [link](https://github.com/eostermueller/heapSpank/releases/download/v0.7/heapSpank-0.7.jar)  
**Forum**: [heapSpank@googlegroups.com](mailto:heapSpank@googlegroups.com)

heapSpank detects Java memory leaks fast!  Just download the jar and point to the process id (pid) of a running JVM that you want to monitor for leaks.  It is easy! Like this:

    java -jar heapSpank-0.7.jar 8173

Using data from [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH), heapSpank shows the percentage of time that byte counts are on the rise for the 10 classes most likely to be leaking.

![Quick Memory Leak Detection](https://cloud.githubusercontent.com/assets/175773/21078075/63990eb2-bf27-11e6-8b5e-5de636302fa6.png)



## Notes
* For best results, warm up an application for a few minutes at 'steady state' before launching heapSpank.
* Small leaks, as well as large ones, are identified.
* Do you run multi-hour tests to identify memory leak suspects?  No more!  heapSpank identifes leak suspects in just minutes.
* MD5 for v0.7 jar download: c9c9220ba3d766cd02a6b64fb2f6e18c
* Do you know someone who has expertise with the [IBM J9 JVM](http://www.ibm.com/developerworks/java/jdk/)?  Need help answering [this question on stackoverflow.com](http://stackoverflow.com/questions/41138610/programmatically-get-jmap-histo-data-from-ibm-j9) so heapSpank can support the J9.

## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.

## Configuration File
To configure heapSpank, just create heapSpank.properties in the same folder as the heapSpank.jar file.
There should be no need to change heapSpank.properties inside the heapSpank.jar, where all the default values are stored.

There are two other options for configuration:
 * Create heapSpank.properties in your home directory, like "C:\Users\Betty\heapSpank.properties" or "/Users/Betty/heapSpank.properties".
 * Pass in values as java system -D parameters, and override all values in config files.  Example:
 
        java -Dorg.heapspank.jmap.histo.interval.seconds=30 -jar heapSpank-0.7.jar 8173
        
## Configuration File Precedence
The following shows the order of precedence of the 4 configuration options:

1. Java -D System Properties override all configuration.
2. heapSpank.properties in same folder as heapSpank.jar
3. heapSpank.properties in User's home dir.
4. heapSpank.properties file in heapSpank.jar file provides defaults for all variables.
 
 As an example, a variable like "org.heapspank.jmap.histo.live=true" in heapSpank.properties in same folder as heapSpank.jar will override the value of that same variable both in your heapSpank.properties in your home dir and in heapSpank.properties in the heapSpank.jar file.
 
## Configuration Variables
    #The interval (in seconds) at which jmap -histo is invoked.
    org.heapspank.jmap.histo.interval.seconds=5
    
    #If true, jmap -histo is passed the '-live' parameter, 
    #which forces a full GC with every jmap -histo run.
    #Using true will identify leak suspects more quickly & accurately, but will incur extra GC overhead.  
    org.heapspank.jmap.histo.live=false
    
    #A 'window' is a group of jmap -histo 'runs', and this parameter defines the number of runs per window.
    #A larger value (more runs per window) provides results with higher confidence.
    #A smaller value provides results quicker.
    #See com.github.eostermueller.heapspank.leakyspank.LeakySpankContext for how this is used.
    org.heapspank.jmap.histo.count.per.window=4
    
    #Count of the 'leakiest' classes promoted from each window 
    #to an 'all time' list of leakest classes ever. 
    org.heapspank.suspect.count.per.window=15
    
    #Count of rows in main display, 1 row per class.
    org.heapspank.display.row.count=15
    
    org.heapspank.regex.exclusion.filter=
    
    org.heapspank.screen.refresh.interval.seconds=1
    
    org.heapspank.max.iterations=86000
    
    org.heapspank.view.class=com.github.eostermueller.heapspank.leakyspank.console.DefaultView

## Advanced Configuration
If the heapSpank.properties does not provide enough control for you, then just add your configuration in a [subclass](https://github.com/eostermueller/heapSpank/blob/master/src/main/java/com/github/eostermueller/heapspank/leakyspank/console/FifteenSecondJMapHistoInterval.java) of [DefaultConfig](https://github.com/eostermueller/heapSpank/blob/master/src/main/java/com/github/eostermueller/heapspank/leakyspank/console/DefaultConfig.java) and pass the name of your subclass on the command line using the '-config' option.  By default, jmap -histo is invoked every 5 seconds.  The following slows this down to every 15 seconds.

    java -jar heapSpank-0.7.jar 8173 -config com.github.eostermueller.heapspank.leakyspank.console.FifteenSecondJMapHistoInterval

## Forum
* Send your questions/feedback to heapSpank@googlegroups.com for discussion.  
* Forum history available [here](https://groups.google.com/forum/#!forum/heapspank).
* To report a bug or ask for an enhancement [open an issue here](https://github.com/eostermueller/heapSpank/issues). 

## Competition
In case heapSpank is not quite what you were looking for, here are a few similar tools that do memory comparisons in search of memory leaks:
* [This python script](http://alexpunnen.blogspot.com/2015/06/long-running-java-process-resource.html) compares two histogram (jmap -histo) dumps.
* [Eclipse MAT](https://wiki.eclipse.org/MemoryAnalyzer) will compare two heap dumps as shown [here](https://www.ibm.com/developerworks/community/blogs/kevgrig/entry/how_to_use_the_memory_analyzer_tool_mat_to_compare_heapdumps_and_system_dumps20?lang=en).

