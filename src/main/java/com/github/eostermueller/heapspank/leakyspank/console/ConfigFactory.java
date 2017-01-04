package com.github.eostermueller.heapspank.leakyspank.console;

public class ConfigFactory {
	private static final String DEFAULT_CONFIG_IMPL = "com.github.eostermueller.heapspank.leakyspank.console.DefaultConfig";
	public static final String APACHE_CONFIG_IMPL = "com.github.eostermueller.heapspank.leakyspank.console.ApacheCommonsConfigFile";
	
	private String defaultConfigClass = DEFAULT_CONFIG_IMPL;
	/**
	 * Create a new instance of com.github.eostermueller.heapspank.leakyspank.console.Config 
	 * @param args
	 * @return
	 * @throws CommandLineParameterException
	 */
	public Config createNew(String[] args) throws CommandLineParameterException {
		Config rc = null;
		String proposedNameOfConfigClass = null;
		proposedNameOfConfigClass = getConfigClassName(args);
		Object configInstance = null;
		
		debug("Attempting to load config class [" + proposedNameOfConfigClass + "]");
		try {
			Class c = Class.forName(proposedNameOfConfigClass);
			configInstance = c.newInstance();
		} catch (Exception e) {
			CommandLineParameterException x = new CommandLineParameterException("Unable to create [" + proposedNameOfConfigClass + "].  Not in the classpath?", e);
			x.setProposedConfigClassName(proposedNameOfConfigClass);
			throw x;
		}
		
		if (Config.class.isInstance(configInstance)) {
			rc = (Config) configInstance;
			rc.setArgs(args);
		} else {
			CommandLineParameterException x = new CommandLineParameterException("The -config class [" + proposedNameOfConfigClass + "] must implement com.github.eostermueller.heapspank.leakyspank.console.Config");
			x.setProposedConfigClassName(proposedNameOfConfigClass);
			throw x;
		}
		
		debug("loaded config [" + rc.toString() + "]");
		return rc;
	}
	private void debug(String string) {
		System.out.println("heapSpank: " + string);
		
	}
	private String getConfigClassName(String[] args) throws CommandLineParameterException {
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
			rc = this.defaultConfigClass;
		return rc;
	}
	public String getDefaultConfigClass() {
		return defaultConfigClass;
	}
	public void setDefaultConfigClass(String defaultConfigClass) {
		this.defaultConfigClass = defaultConfigClass;
	}

}
