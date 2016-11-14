package com.github.eostermueller.heapspank.garbagespank.console;

import java.util.ArrayList;
import java.util.List;

public class CommandLineParameters {
	long interval = -1;
	String intervalUnitOfMeasure = "<uninit>";
	List<String> errors = new ArrayList<String>();
	public List<String> getErrors() {
		return errors;
	}
	private String[] args;
	CommandLineParameters(String[] args) {
		this.args = args;
		processArgs();
	}
	long getIntervalInMilliseconds() {
		return interval;
	}
	private void processArgs() {
		int multiplier = 1;
		
		if (args.length!=2)
			errors.add("Was expecting 2 args but found [" + args.length + "]");
		else if (!args[0].equals("-i") )
			errors.add("First argument must be -i");
		else if (errors.size()==0)    {
			try {
				String tmpInterval = args[1].trim();
				if (tmpInterval.endsWith("s")) { //Trying to support the 's' that comes with interval in jstat: http://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr017.html
					multiplier = 1000;
					tmpInterval = tmpInterval.substring(0, tmpInterval.length()-1);
				}
				
				interval = Integer.parseInt(tmpInterval);
				interval *= multiplier;
			} catch (NumberFormatException nfe) {
				errors.add("Was expecting the 2nd paramter to be a  long for the same 'interval' that was passed to jstat, but instead found [" + args[1] + "].  Default unit of measure is ms, but append an 's' to indicate seconds like '3s'.");
			}
		}
	}
	boolean valid() {
		return errors.size()==0;
	}
	public String getUsage() {
		StringBuilder sb = new StringBuilder();
		
		for(String error : this.errors) {
			sb.append(error).append("\n");
		}
		
		sb.append("\nGarbageSpank provides a few 'calculated' GC metrics by post-processing output from JAVA_HOME/bin/jstat");
		sb.append("\n\n[[ arguments for jstat ]] | gs.sh -i N");
		sb.append("\n[[ arguments for jstat ]] | gs.cmd -i N");
		sb.append("\nWhere N is the exact same interval passed into jstat");
		sb.append("\n\nExample:");
		sb.append("\njstat -gcnew 123 1s | gs.sh -i 1s");
		sb.append("\nWhere 123 is the pic you want to monitor and ");
		return sb.toString();
	}
	
}