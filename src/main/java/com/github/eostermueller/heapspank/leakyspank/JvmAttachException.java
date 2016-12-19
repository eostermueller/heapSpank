package com.github.eostermueller.heapspank.leakyspank;

public class JvmAttachException extends Exception {

	private long pid;

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public JvmAttachException(long pid, Exception e) {
		super(e);
		setPid(pid);
	}

}
