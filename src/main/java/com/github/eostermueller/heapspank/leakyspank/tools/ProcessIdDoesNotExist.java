package com.github.eostermueller.heapspank.leakyspank.tools;

import java.io.IOException;

public class ProcessIdDoesNotExist extends JMapHistoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 821111523L;
	@Override
	public String getMessage() {
		return "JVM process id (pid) not found [" + this.getProcessId() + "]";
	}
	public ProcessIdDoesNotExist(UnsupportedClassVersionError ucve) {
		super(ucve);
	}
	public ProcessIdDoesNotExist(IOException ioe) {
		super(ioe);
	}
	public ProcessIdDoesNotExist() {
		// TODO Auto-generated constructor stub
	}
	public ProcessIdDoesNotExist(Exception e) {
		super(e);
	}
//	public String getProcessId() {
//		return processId;
//	}
//
//	public void setProcessId(String processId) {
//		this.processId = processId;
//	}
}
