# leakySpank

Just pass in the PID of your JVM.  Apply steady-state load and wait a few minutes . The names of the leakiest classes (high LKY%) will bubble up to the top of the console.

./leakySpank.sh 2351
![Quick Memory Leak Detection](https://cloud.githubusercontent.com/assets/175773/20299273/da86b044-aadf-11e6-98c9-0658af68ad85.png)

* leakySpank compares byte counts over time of all classes in your JVM.
* leakySpank's data provided by [JAVA_HOME/bin/jmap -histo myPid](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr014.html#BABJIIHH).
* Support for Windows coming soon! Tested on Linux and MacOs with HotSpot JDK (JRE is not enough).
* Small leaks, as well as large ones, are identified.



## Install
1. Make sure JAVA_HOME/bin is in your path (for jmap executable).
1. Unzip [heapSpank zip file](https://github.com/eostermueller/heapSpank/releases/download/v0.5/heapSpank-0.5.zip) (< 100k) to a new folder on your hard disk.
2. chmod +x leakySpank.sh
3. Apply steady state load to the java app you want to detect leaks in.  Be sure the app is warmed up, perhaps for 5 minutes.
4. Find the process id (pid) of your leaky app and enter "./leakySpank.sh myPid"


## Limitations
1. Only works with HotSpot JVM, because data is furnished by HotSpot's jmap -histo <myPid>
2. Curretly does not support jmap's connection to remote JVMs....but please create an issue if that feature would be helpful.
