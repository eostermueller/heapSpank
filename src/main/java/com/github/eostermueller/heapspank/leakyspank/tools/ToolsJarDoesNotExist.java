package com.github.eostermueller.heapspank.leakyspank.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ToolsJarDoesNotExist extends JMapHistoException {
	List<File> attemptedLocations = new ArrayList<File>();
	private File javaHome;

	public void setJavaHome(File f) {
		this.javaHome = f;
	}
	public File getJavaHome() {
		return this.javaHome;
	}
	public void addAttemptedLocation(File f) {
		this.attemptedLocations.add(f);
	}
	public File[] getAttemptedLocations() {
		return this.attemptedLocations.toArray(new File[]{});
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("The file tools.jar that comes with the JDK was not found.\n");
		sb.append("Attempted these locations:\n");
		for(File f : this.attemptedLocations) 
			sb.append(f.getAbsolutePath()).append("\n");
		sb.append("...using the Java system property java.home [" + this.getJavaHome().getAbsolutePath() + "]\n");
		return sb.toString();
	}
}
