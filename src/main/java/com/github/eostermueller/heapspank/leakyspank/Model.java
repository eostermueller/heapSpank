package com.github.eostermueller.heapspank.leakyspank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;

/**
 *  Pojo representing parsed output of one execution of JAVA_HOME/bin/jmap -histo <myPid>
 */
public class Model 
{
	/**
	 * There are a lot of extraneous classes in the jmap -histo output that get in the way of quick troubleshooting.
	 * This list allows us to exclude certain java packages from the output.
	 */
//	public static String[] DEFAULT_STARTSWITH_EXCLUDE_FILTER = {"[B","[C","[I","[Z","[D","[F","[J","[L","[S","java"};
//	public static String[] DEFAULT_STARTSWITH_EXCLUDE_FILTER = {};
	private Hashtable<String,JMapHistoLine> htAllClasses = new Hashtable<String,JMapHistoLine>(); //<JMapHistoLine>
	private List<JMapHistoLine> alAllClasses = new ArrayList<JMapHistoLine>(); //<JMapHistoLine>
	private ClassNameFilter classNameExclusionFilter;
	
	public JMapHistoLine[] getAll() {
		return this.alAllClasses.toArray( new JMapHistoLine[0]);
	}
	
	public Model() {
	}
	public Model(String jMapHistoStdout) {
		this(jMapHistoStdout,null);
	}
	public void add(LeakResult[] toAdd) {
		for(LeakResult result : toAdd)
			this.put(result.line);
	}
	public Model(String jMapHistoStdout, ClassNameFilter classNameExclusionFilter) {
		this.classNameExclusionFilter = classNameExclusionFilter;
		
		for (int i = 0; i < 4; i++)
			jMapHistoStdout = jMapHistoStdout.replace("  "," ");  //Delimit columns using single space instead of multiple spaces.

		String[] lines = jMapHistoStdout.split("\\r?\\n");   // http://stackoverflow.com/questions/454908/split-java-string-by-new-line

		for(String line : lines) {
			if (
				   line.trim().length() > 0
				&& line.indexOf("num #instances #bytes class name") < 0
				&& line.indexOf("-------------------") < 0				
				&& !line.startsWith("Total ") //Exclude this:  Total       6309950      429858384
				) {
				JMapHistoLine jMapHistoLine = new JMapHistoLine(line);
				
				boolean ynExclude = false;
				if (this.classNameExclusionFilter!=null)
					if (this.classNameExclusionFilter.accept(jMapHistoLine.className))
						ynExclude = true;
				
				if (!ynExclude)
					put(jMapHistoLine);
			}
		}
	}
	public JMapHistoLine get(String className) {
		return this.htAllClasses.get(className);
	}
		
	public void put(JMapHistoLine line) {
		boolean ynExclude = false;
		
		if (this.classNameExclusionFilter!=null)
			if (this.classNameExclusionFilter.accept(line.className))
				ynExclude = true;
		
		if (!ynExclude && !this.htAllClasses.containsKey(line.className)) {
			this.htAllClasses.put(line.className, line);
			this.alAllClasses.add(line);
		}
		
	}
	public JMapHistoLine[] getAllOrderByBytes() {
		Collections.sort( this.alAllClasses, Model.BYTES_ORDER);
		return this.alAllClasses.toArray( new JMapHistoLine[0]);
	}
	
//	/**
//	 * 
//	 * @param m2
//	 * @return
//	 */
//	public JMapHistoLine[] getAllOrderByMostUpwardlyMobileAsComparedTo(Model m2) {
//		
//		for(JMapHistoLine l1 : this.alAllClasses) {
//			JMapHistoLine l2 = m2.get(l1.className);
//			if (l2!=null) {
//				l1.rankIncrease = (l2.num -l1.num);
//			} else {
//				l1.rankIncrease = 0;//Integer.MIN_VALUE;
//			}
//		}
//		Collections.sort( this.alAllClasses, this.UPWARDLY_MOBILE_ORDER);
//		return this.alAllClasses.toArray( new JMapHistoLine[0]);
//	}
	static final Comparator<JMapHistoLine> BYTES_ORDER = new Comparator<JMapHistoLine>() {
		public int compare(JMapHistoLine l1, JMapHistoLine l2) {
			return (int) (l1.bytes - l2.bytes);
		}
	};
	
//	static final Comparator<JMapHistoLine> UPWARDLY_MOBILE_ORDER = new Comparator<JMapHistoLine>() {
//			public int compare(JMapHistoLine l1, JMapHistoLine l2) {
//				return l1.rankIncrease - l2.rankIncrease;
//			}
//	};
	public String renderBytes(String prefix) {
		StringBuilder sb = new StringBuilder();
		for(JMapHistoLine l : this.alAllClasses) {
			sb.append(l.getBytesGraphLabel(prefix) );
		}
		return sb.toString();
	}
}
 