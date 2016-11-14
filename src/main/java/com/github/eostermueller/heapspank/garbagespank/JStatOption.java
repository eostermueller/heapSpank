package com.github.eostermueller.heapspank.garbagespank;

/**
 * Supported variants of the -statOption documented here:
 * https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html
 * @author erikostermueller
 *
 */
public enum JStatOption {
	gc, 
	gccapacity, 
	gcnew, 
	gcnewcapacity, 
	gcold, 
	gcoldcapacity, 
	gcmetacapacity, 
	gcutil
}
