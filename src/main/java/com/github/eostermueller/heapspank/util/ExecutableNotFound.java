package com.github.eostermueller.heapspank.util;

import java.io.IOException;

public class ExecutableNotFound extends RuntimeException {

	String executableName = null;
	private IOException ioException;

	public String getExecutableName() {
		return executableName;
	}

	public void setExecutableName(String executableName) {
		this.executableName = executableName;
	}

	public void setException(IOException e) {
		ioException = e;
	}
	public IOException getException() {
		return this.ioException;
	}
	
}
