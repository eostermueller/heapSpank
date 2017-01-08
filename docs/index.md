---
title: "heapSpank"
layout: splash
date: 2017-01-06T11:48:41-04:00
header:
  overlay_color: "#000"
  overlay_filter: "0.3"
  overlay_image: /assets/images/wallpaper3.jpg
  cta_label: "Download"
  cta_url: "https://github.com/eostermueller/heapSpank/releases/download/v0.8/heapSpank-0.8.jar"
  caption: "Photo credit: [John Ostermueller](https://www.instagram.com/ostermuellerj/)"
excerpt: "Find memory leaks in minutes."
intro: 
  - excerpt: 'Nullam suscipit et nam, tellus velit pellentesque at malesuada, enim eaque. Quis nulla, netus tempor in diam gravida tincidunt, *proin faucibus* voluptate felis id sollicitudin. Centered with `type="center"`'
---

**Forum**: [heapSpank@googlegroups.com](mailto:heapSpank@googlegroups.com)

heapSpank detects Java memory leaks in minutes!  Just download the jar and point to the process id (pid) of a running JVM that you want to monitor for leaks.  ***No heapdump required** -- it's easy! Like this:

    java -jar heapSpank-0.8.jar 8173

Using data from [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH), heapSpank shows the percentage of time that byte counts are on the rise for the 15 classes most likely to be leaking.

Classes that reach "100%" are the most likely to be leaky.

![Quick Memory Leak Detection](http://g.recordit.co/IiBoJS6vkk.gif)

## Notes
* For best results, warm up an application for a few minutes at 'steady state' before launching heapSpank.
* Small leaks, as well as large ones, are identified.
* MD5 (heapSpank-0.8.jar) = 837f251eea760c11496cf03b65e7f58a
* Apache 2.0 license.
* Do you know someone who has expertise with the [IBM J9 JVM](http://www.ibm.com/developerworks/java/jdk/)?  Need help answering [this question on stackoverflow.com](http://stackoverflow.com/questions/41138610/programmatically-get-jmap-histo-data-from-ibm-j9) so heapSpank can support the J9.

## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.

## Forum
* Send your questions/feedback to heapSpank@googlegroups.com for discussion.  
* Forum history available [here](https://groups.google.com/forum/#!forum/heapspank).
* To report a bug or ask for an enhancement [open an issue here](https://github.com/eostermueller/heapSpank/issues). 

## Competition
In case heapSpank is not quite what you were looking for, here are a few similar tools that do memory comparisons in search of memory leaks:

* [This python script](http://alexpunnen.blogspot.com/2015/06/long-running-java-process-resource.html) compares two histogram (jmap -histo) runs.
* Many blogs/tools use heapDumps to diagnose memory leaks.
  * Open-source that auto-generates and auto-analyzes heap dumps, looking for leaks. [Github site](https://github.com/square/leakcanary) and [this blog](https://medium.com/square-corner-blog/leakcanary-detect-all-memory-leaks-875ff8360745#.docrwx62v).
  * [This blog](https://www.toptal.com/java/hunting-memory-leaks-in-java) recommends using JVisualVM to analyze a heapdump.
  * [Eclipse MAT](https://wiki.eclipse.org/MemoryAnalyzer) will compare two heap dumps as shown [here](https://www.ibm.com/developerworks/community/blogs/kevgrig/entry/how_to_use_the_memory_analyzer_tool_mat_to_compare_heapdumps_and_system_dumps20?lang=en).
