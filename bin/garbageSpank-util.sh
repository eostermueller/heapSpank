dir=$(dirname "$0")


#One location for a dev environment, one location for binary packaging
export GS=$dir/../target/heapSpank-0.6.jar:$dir/heapSpank-0.6.jar

export PID_TO_MONITOR=$1
export INTERVAL=1s
export OPTION=-gcutil

$JAVA_HOME/bin/jstat $OPTION $PID_TO_MONITOR $INTERVAL | java -classpath $GS com.github.eostermueller.heapspank.garbagespank.console.GarbageSpankConsole -i $INTERVAL
