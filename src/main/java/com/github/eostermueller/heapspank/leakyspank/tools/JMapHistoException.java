package com.github.eostermueller.heapspank.leakyspank.tools;

public class JMapHistoException extends Exception {

	String pid = null;
	private String testData;
	private String msg;
	public String getProcessId() {
		return pid;
	}
	public void setProcessId(String pid) {
		this.pid = pid;
	}
	public JMapHistoException(Exception e) {
		super(e);
	}
	public JMapHistoException() {
		// TODO Auto-generated constructor stub
	}
	public JMapHistoException(UnsupportedClassVersionError ucve) {
		super(ucve);
	}
	public void setTestData(String jmapResult) {
		this.testData = jmapResult;
	}
	public String getTestData() {
		return this.testData;
	}
	public void setMessage(String string) {
		msg = string;
	}
	

}
