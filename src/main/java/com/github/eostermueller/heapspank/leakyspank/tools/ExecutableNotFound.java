package com.github.eostermueller.heapspank.leakyspank.tools;

import java.io.IOException;

public class ExecutableNotFound extends JMapHistoException {

	String exe = null;
	public ExecutableNotFound(IOException e) {
		super(e);
	}
	public void setExecutableName(String commandPath) {
		exe = commandPath;
		
	}

}
