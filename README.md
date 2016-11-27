# heapSpank
* Find leaks fast in your JVM's heap, sometimes in less than 1 minute.  
* Just look for classes high LKY% and large byte counts.
* heapSpank compares byte counts over time of all classes in your JVM.  
* heapSpank's data provided by [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH).
* Only tested on MacOS with HotSpot JDK....a JRE is not enough b/c it lacks jmap executable.
* Support for Windows coming soon! Tested on Linux and MacOs.

![Quick Memory Leak Detection](https://cloud.githubusercontent.com/assets/175773/20299273/da86b044-aadf-11e6-98c9-0658af68ad85.png)


## Install
1. Make sure JAVA_HOME/bin is in your path (for jmap executable).
1. Unzip [heapSpank zip file](https://github.com/eostermueller/heapSpank/releases/download/v0.5/heapSpank-0.5.zip) (< 100k) to a new folder on your hard disk.
2. chmod +x leakySpank.sh
3. Apply steady state load to the java app you want to detect leaks in.  Be sure the app is warmed up, perhaps for 5 minutes.
4. Find the process id (pid) of your leaky app and enter "./leakySpank.sh myPid"


## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.
