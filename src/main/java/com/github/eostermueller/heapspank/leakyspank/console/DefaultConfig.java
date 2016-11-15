package com.github.eostermueller.heapspank.leakyspank.console;

public class DefaultConfig implements Config {
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
	public DefaultConfig(String[] args) {
		this.pid = Long.parseLong(args[0]);
		System.out.format("0: %s%n", args[0]);
		this.setjMapHistoIntervalSeconds(5);//should be 15 for release
		this.setjMapCountPerWindow(4);
		this.setSuspectCountPerWindow(10);
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
