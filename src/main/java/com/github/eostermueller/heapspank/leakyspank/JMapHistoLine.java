package com.github.eostermueller.heapspank.leakyspank;

/**
 * pojo to hold a single 'data' line of output from $JAVA_HOME/bin/jmap -histo <myPid>
 * @author erikostermueller
 *
 */
public class JMapHistoLine {
	public int num = Integer.MAX_VALUE;

	/**  
	 * What was the 'num' value, a ranking of most memory 
	 * consumed with 1=most memory consumed, for a previous invocation of jmap -histo ?
	 */
	public int rankIncrease = Integer.MIN_VALUE; 
	public long instances = -1;
	public long bytes = -1;
	public String className;
	public JMapHistoLine(String spaceDelimitedJMapHistoLine) {
		spaceDelimitedJMapHistoLine = spaceDelimitedJMapHistoLine.trim();
		String[] columns = spaceDelimitedJMapHistoLine.split(" ");
		String num = columns[0];
		if (num.endsWith(":") && num.length() > 1) {
			num = num.substring(0,num.length()-1);
		}
		this.num = Integer.parseInt(num);
		this.instances = Long.parseLong(columns[1]);
		this.bytes = Long.parseLong(columns[2]);
		this.className = columns[3];
	}
	public String getBytesGraphLabel(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("_");
		sb.append(className);
		sb.append("_bytes=" + this.bytes);
		sb.append("<BR>\n");
		return sb.toString();
	}
	
}