package com.github.eostermueller.heapspank.leakyspank.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JMapHistoProcessWrapper implements JMapHisto {

	private static final String NO_PID = "No such process";
	
	public JMapHistoProcessWrapper(String pid) {
		this.setPid(pid);
		this.setCommandPath("jmap");
	}
	String pid = null;
	private String commandPath;
	
	public void setCommandPath(String str) {
		this.commandPath = str;
	}
	public String getCommandPath() {
		return this.commandPath;
	}
	@Override
	public String heapHisto(boolean live) throws JMapHistoException, ProcessIdDoesNotExist {
		StringBuilder processOutput = new StringBuilder();
		StringBuilder processError = new StringBuilder();
		List<String> processArgs = new ArrayList<String>();

		processArgs.add(this.getCommandPath());
		processArgs.add("-histo");
		processArgs.add(this.getPid() );

		ProcessBuilder processBuilder = new ProcessBuilder(processArgs);
		//processBuilder.redirectErrorStream(true);

		Process process;
		long start = -1;
		try {
			start = System.currentTimeMillis();
			process = processBuilder.start();

			BufferedReader processOutputReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String readLine;

			int lineCount = 0;
			while ((readLine = processOutputReader.readLine()) != null) {
				if (lineCount > 0)
					processOutput.append(readLine + System.lineSeparator());
				lineCount++;
			}
			InputStream error = process.getErrorStream();
	         for (int i = 0; i < error.available(); i++) {
	            processError.append(error.read());
	         }			
//			BufferedReader processErrorReader = new BufferedReader(
//					new InputStreamReader(process.getErrorStream()));
//
//			String readErrorLine;
//
//			int errorLineCount = 0;
//			while ((readErrorLine = processErrorReader.readLine()) != null) {
//				if (errorLineCount > 0)
//					processError.append(readErrorLine + System.lineSeparator());
//				errorLineCount++;
//			}
			process.waitFor();
		} catch (IOException e) {
			
			if (e.getMessage().contains("No such file or directory")) {
				ExecutableNotFound  enf = new ExecutableNotFound(e);
				enf.setExecutableName(this.getCommandPath());
				throw enf;
			}
		} catch (InterruptedException e) {
			throw new JMapHistoException(e);
		}
		
		if (processError.toString().indexOf(NO_PID)>0) {
			ProcessIdDoesNotExist pe = new ProcessIdDoesNotExist();
			pe.setProcessId(this.getPid());
			throw pe;
		}
		return processOutput.toString();
	}

	@Override
	public void setPid(String s) {
		this.pid = s;
	}

	@Override
	public String getPid() {
		return pid;
	}

	/**
	 * 
	 * Verify we get at least these bits 
<pre>
num     #instances         #bytes  class name
----------------------------------------------
22:           615           9840  java.lang.Object
</pre>
	 * @throws JMapHistoException 
	 */
	@Override
	public String selfTest() throws JMapHistoException {
		boolean rc = false;
		String jmapResult = this.heapHisto(true);
		if (jmapResult.indexOf(JMapHisto.HEADER) >0) {
			/**
			 * If this name does not show up in list of loaded classes, 
			 * there is a problem.
			 */
			if (jmapResult.indexOf("java.lang.Object") > 0) {
				rc = true;
			}
		}
		if (!rc) {
			JMapHistoException e = new JMapHistoException();
			e.setTestData(jmapResult);
			e.setProcessId(this.getPid());
			throw e;
		}
		return jmapResult;
	}
	/**
	 * noop -- other implementations actually use this.
	 */
	@Override
	public void shutdown() {
		
		
	}
}
