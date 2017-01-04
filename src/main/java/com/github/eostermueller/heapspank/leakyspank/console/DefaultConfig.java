package com.github.eostermueller.heapspank.leakyspank.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.eostermueller.heapspank.leakyspank.ClassNameFilter;


public class DefaultConfig implements Config {
	private boolean jmapHistoLive = false;
	private static final Object PARM_SELF_TEST = "-selfTest";
	private static final String HEAP_SPANK = "heapSpank: ";
	private ClassNameFilter classNameFilter = null;
	int displayRowCount = 10;
	
	@Override 
	public ClassNameFilter getClassNameExclusionFilter() {
		ClassNameFilter f = null;
		if (DefaultConfig.this.getRegExExclusionFilter()!=null) {
			f = new ClassNameFilter() {
				Pattern p = null;
				@Override
				public boolean accept(String proposedClassName) {
					if (p==null) {
						p = Pattern.compile( DefaultConfig.this.getRegExExclusionFilter() );
					}
					 Matcher m = p.matcher(proposedClassName);
					 return m.matches();
				}
			};
		}
		return f;
	}

	public void setClassNameFilter(ClassNameFilter classNameFilter) {
		this.classNameFilter = classNameFilter;
	}
	String viewClass = "com.github.eostermueller.heapspank.leakyspank.console.DefaultView";
	boolean runSelfTestAndExit = false;
	String startsWithExclusionFilter = null;
	
	@Override 
	public void setRegExExclusionFilter(String string) {
		this.startsWithExclusionFilter = string;
	}

	@Override 
	public String getRegExExclusionFilter() {
		return this.startsWithExclusionFilter;
	}
	
	@Override
	public boolean runSelfTestAndExit() {
		return runSelfTestAndExit;
	}
	@Override
	public void setRunSelfTestAndExit(boolean runSelfTestOnly) {
		this.runSelfTestAndExit = runSelfTestOnly;
	}
	@Override
	public String getViewClass() {
		return viewClass;
	}
	@Override
	public void setViewClass(String viewClass) {
		this.viewClass = viewClass;
	}
	int suspectCountPerWindow = -1;
	@Override
	public int getSuspectCountPerWindow() {
		return suspectCountPerWindow;
	}
	@Override
	public void setSuspectCountPerWindow(int suspectCountPerWindow) {
		this.suspectCountPerWindow = suspectCountPerWindow;
	}
	int screenRefreshIntervalSeconds = 1;
	int jMapCountPerWindow= -1;
	
	@Override
	public int getjMapCountPerWindow() {
		return jMapCountPerWindow;
	}
	@Override
	public void setjMapCountPerWindow(int jMapCountPerWindow) {
		this.jMapCountPerWindow = jMapCountPerWindow;
	}
	@Override
	public int getjMapHistoIntervalSeconds() {
		return jMapHistoIntervalSeconds;
	}
	@Override
	public void setjMapHistoIntervalSeconds(int jMapHistoIntervalSeconds) {
		this.jMapHistoIntervalSeconds = jMapHistoIntervalSeconds;
	}
	int jMapHistoIntervalSeconds = -1;
	long pid = -1;
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	
	private static void debug(String string) {
		System.out.println(HEAP_SPANK + string);
		
	}
	@Override
	public void setArgs(String[] args) throws CommandLineParameterException {
		
		if (args.length >=1) {
			this.pid = Long.parseLong(args[0]);
			this.setjMapHistoIntervalSeconds(5);
			this.setjMapCountPerWindow(4);
			this.setSuspectCountPerWindow(10);
			
			//If heapSpank fingered any of these as a problem, it would get you no 
			//closer to solving your leaks -- so suppress them from all processing.
			this.setRegExExclusionFilter("(java.lang.String|java.lang.Object)");
			
			for(String s : args) {
				if (s.equals(PARM_SELF_TEST)) {
					this.setRunSelfTestAndExit(true);
				}
			}
		} else {
			String error = "Add the pid of the java process you want to monitor for leaks.";
			CommandLineParameterException e = new CommandLineParameterException(error);
			throw e;
		}
	}
	/* (non-Javadoc)
	 * @see com.github.eostermueller.heapspank.leakyspank.console.IConfig#getScreenRefreshIntervalSeconds()
	 */
	@Override
	public int getScreenRefreshIntervalSeconds() {
		return screenRefreshIntervalSeconds;
	}
	/* (non-Javadoc)
	 * @see com.github.eostermueller.heapspank.leakyspank.console.IConfig#setScreenRefreshIntervalSeconds(int)
	 */
	@Override
	public void setScreenRefreshIntervalSeconds(int screenRefreshIntervalSeconds) {
		this.screenRefreshIntervalSeconds = screenRefreshIntervalSeconds;
	}
	/*
	 * To avoid running forever if forgotten
	 * @see com.github.eostermueller.heapspank.leakyspank.console.Config#getMaxIterations()
	 */
	@Override
	public int getMaxIterations() {
		return 86000;
	}

	@Override
	public int getDisplayRowCount() {
		return displayRowCount;
	}

	@Override
	public void setDisplayRowCount(int rows) {
		displayRowCount = rows;
	}

	@Override
	public boolean getJMapHistoLive() {
		return jmapHistoLive;
	}

	@Override
	public void setJMapHistoLive(boolean b) {
		jmapHistoLive = b;
	}
}
