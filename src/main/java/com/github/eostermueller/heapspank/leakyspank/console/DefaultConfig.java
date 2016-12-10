package com.github.eostermueller.heapspank.leakyspank.console;


public class DefaultConfig implements Config {
	private static final String DEFAULT_CONFIG_IMPL = "com.github.eostermueller.heapspank.leakyspank.console.DefaultConfig";
	String viewClass = "com.github.eostermueller.heapspank.leakyspank.console.DefaultView";
	
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
	/**
	 * Create a new instance of com.github.eostermueller.heapspank.leakyspank.console.Config 
	 * @param args
	 * @return
	 * @throws CommandLineParameterException
	 */
	public static Config createNew(String[] args) throws CommandLineParameterException {
		Config rc = null;
		String proposedNameOfClass = getConfigClassName(args);
		Object configInstance = null;
				
		try {
			Class c = Class.forName(proposedNameOfClass);
			configInstance = c.newInstance();
		} catch (Exception e) {
			CommandLineParameterException x = new CommandLineParameterException("Unable to create [" + proposedNameOfClass + "].  Not in the classpath?", e);
			x.setProposedConfigClassName(proposedNameOfClass);
			throw x;
		}
		
		if (Config.class.isInstance(configInstance)) {
			rc = (Config) configInstance;
			rc.setArgs(args);
		} else {
			CommandLineParameterException x = new CommandLineParameterException("The -config class [" + proposedNameOfClass + "] must implement com.github.eostermueller.heapspank.leakyspank.console.Config");
			x.setProposedConfigClassName(proposedNameOfClass);
			throw x;
		}
			
		return rc;
	}
	private static String getConfigClassName(String[] args) throws CommandLineParameterException {
		String rc = null;
		for(int i = 0; i < args.length; i++) {
			if (args[i].equals("-config")) {
				if ( i+1 < args.length)
					rc = args[i+1];
				else {
					CommandLineParameterException x = new CommandLineParameterException("parameter after -config must be name of a class that implements com.github.eostermueller.heapspank.leakyspank.console.Config");
					x.setProposedConfigClassName(null);
					throw x;
				}
			}
		}
		if (rc==null)
			rc = DEFAULT_CONFIG_IMPL;
		return rc;
	}
	@Override
	public void setArgs(String[] args) throws CommandLineParameterException {
		
		if (args.length >=1) {
			this.pid = Long.parseLong(args[0]);
//			System.out.format("0: %s%n", args[0]);
			this.setjMapHistoIntervalSeconds(5);
			this.setjMapCountPerWindow(4);
			this.setSuspectCountPerWindow(10);
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
}
