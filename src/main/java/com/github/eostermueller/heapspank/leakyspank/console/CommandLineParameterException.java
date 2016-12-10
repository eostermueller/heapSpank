package com.github.eostermueller.heapspank.leakyspank.console;

public class CommandLineParameterException extends Exception {

	private String proposedConfigClassName = null;
	public CommandLineParameterException(String string) {
		super(string);
	}
	public CommandLineParameterException(String string, Throwable t) {
		super(string, t);
	}
	public String getProposedConfigClassName() {
		return this.proposedConfigClassName;
	}
	public void setProposedConfigClassName(String val) {
		this.proposedConfigClassName = val;
	}

}
