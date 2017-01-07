---
title: "Quick-Start Guide"
permalink: /configuration/
excerpt: "How to use heapSpank.properties to configure heapSpank."
modified: 2017-01-07T10:01:43-04:00
---

Minimal Mistakes has been developed as a [Jekyll theme gem](http://jekyllrb.com/docs/themes/) for easier use. It is also 100% compatible with GitHub Pages --- just with a more involved installation process.

{% include toc %}


## Configuration File
To configure heapSpank, just create heapSpank.properties in the same folder as the heapSpank.jar file.
There should be no need to change heapSpank.properties inside the heapSpank.jar, where all the default values are stored.

There are two other options for configuration:
 * Create heapSpank.properties in your home directory, like "C:\Users\Betty\heapSpank.properties" or "/Users/Betty/heapSpank.properties".
 * Pass in values as java system -D parameters, and override all values in config files.  Example:
 
        java -Dorg.heapspank.jmap.histo.interval.seconds=30 -jar heapSpank-0.8.jar 8173
        
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

    java -jar heapSpank-0.8.jar 8173 -config com.github.eostermueller.heapspank.leakyspank.console.FifteenSecondJMapHistoInterval

