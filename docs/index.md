---
title: "heapSpank"
layout: splash
date: 2017-01-06T11:48:41-04:00
header:
  overlay_color: "#000"
  overlay_filter: "0.3"
  overlay_image: /assets/images/rain1.JPG
  cta_label: "Download"
  cta_url: "https://github.com/eostermueller/heapSpank/releases/download/v0.8/heapSpank-0.8.jar"
  caption: "Photo credit: [John Ostermueller](https://www.instagram.com/ostermuellerj/)"
excerpt: "Find memory leaks in minutes."
intro: 
  - excerpt: 'Nullam suscipit et nam, tellus velit pellentesque at malesuada, enim eaque. Quis nulla, netus tempor in diam gravida tincidunt, *proin faucibus* voluptate felis id sollicitudin. Centered with `type="center"`'
---

**Forum**: [heapSpank@googlegroups.com](mailto:heapSpank@googlegroups.com)

heapSpank detects Java memory leaks in minutes!  Just download the jar and point to the process id (pid) of a running JVM that you want to monitor for leaks.  It is easy! Like this:

    java -jar heapSpank-0.8.jar 8173

Using data from [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH), heapSpank shows the percentage of time that byte counts are on the rise for the 10 classes most likely to be leaking.

![Quick Memory Leak Detection](http://g.recordit.co/IiBoJS6vkk.gif)