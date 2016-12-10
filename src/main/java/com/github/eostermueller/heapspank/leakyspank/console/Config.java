package com.github.eostermueller.heapspank.leakyspank.console;

import java.util.List;

public interface Config {

	public abstract int getScreenRefreshIntervalSeconds();

	public abstract void setScreenRefreshIntervalSeconds(
			int screenRefreshIntervalSeconds);

	int getjMapHistoIntervalSeconds();

	void setjMapHistoIntervalSeconds(int jMapHistoIntervalSeconds);

	void setjMapCountPerWindow(int jMapCountPerWindow);

	int getjMapCountPerWindow();

	void setSuspectCountPerWindow(int suspectCountPerWindow);

	int getSuspectCountPerWindow();

	public abstract long getPid();

	void setViewClass(String viewClass);

	String getViewClass();

	public abstract int getMaxIterations();

	void setArgs(String[] args) throws CommandLineParameterException;

}