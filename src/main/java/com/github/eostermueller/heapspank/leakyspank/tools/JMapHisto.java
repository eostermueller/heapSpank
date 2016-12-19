package com.github.eostermueller.heapspank.leakyspank.tools;


public interface JMapHisto {
	public static final String HEADER = "num     #instances         #bytes  class name";
	public String heapHisto(boolean live) throws JMapHistoException, ProcessIdDoesNotExist;

	public void setPid(String s);
	public String getPid();
	public String selfTest() throws JMapHistoException;

	public void shutdown();
}
