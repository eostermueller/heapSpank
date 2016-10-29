package com.github.eostermueller.heapspank.leakyspank.jmeter;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext.LeakResult;

import java.io.*;
import java.lang.ProcessBuilder;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;

public class LeakySpankSampler extends AbstractJavaSamplerClient {

	private static final String LEAKY_SPANKY = "ls";
	private static final String P_PID = "pid";
	private static final String P_INTERVAL_IN_SECONDS = "interval_in_seconds";
	private static final String P_INTERVAL_COUNT_PER_WINDOW = "interval_count_per_window";
	private static final String P_TOP_N_SUSPECTS_PER_WINDOW = "top_n_suspects_per_window";
	
	private static final ThreadLocal<LeakySpankContext> threadLocal = new ThreadLocal<LeakySpankContext>();
	@Override
	public Arguments getDefaultParameters() {
	    Arguments defaultParameters = new Arguments();
	    defaultParameters.addArgument(P_PID, "${PID}");
	    defaultParameters.addArgument(P_INTERVAL_IN_SECONDS, "${INTERVAL_IN_SECONDS}");
	    defaultParameters.addArgument(P_INTERVAL_COUNT_PER_WINDOW, "${INTERVAL_COUNT_PER_WINDOW}");
	    defaultParameters.addArgument(P_TOP_N_SUSPECTS_PER_WINDOW, "${TOP_N_SUSPECTS_PER_WINDOW}");
	    return defaultParameters;
	}
	public SampleResult runTest(JavaSamplerContext jmeterSamplerContext) {
		LeakySpankContext ctx = getLeakySpankContext(jmeterSamplerContext);
		getLogger().debug("Start of LeakySpankSampler#runTest");
		SampleResult result = new SampleResult();
		result.sampleStart(); // start stopwatch
		try {
			String pidToMonitor = String.valueOf(ctx.getPid());
			String jMapProcessOutput = executeJMapHisto(pidToMonitor,result);
			result.setSamplerData(jMapProcessOutput);
			this.getLogger().debug(
					"Length of jmap output ["
							+ jMapProcessOutput.toString().length() + "]");
			this.getLogger().debug(
					"jmap output [" + jMapProcessOutput.toString() + "]");

			Model currentModel = new Model(jMapProcessOutput.toString());
			ctx.addJMapRun(currentModel);
			ctx.incrementRunCount();
			
			/**
			 * Display some results, but only at the end of each 'window', not after every jmap -histo run.
			 * By default, you get 4 jmap -histo runs per 'window'.
			 */
			if (ctx.getCurrentRunCount() % ctx.getRunCountPerWindow()==0) {
				LeakResult[] suspects = ctx.getLeakSuspectsOrdered();
				Model resultsForWindow = new Model();
				//select the last N from the array -- the most likely suspects for this window.
				int startIndex = (suspects.length - ctx.getTopNSuspects()) -1;
				if (startIndex < 0) startIndex = 0; 
				for(int i = startIndex; 
						i < suspects.length; 
						i++) {
					resultsForWindow.put(suspects[i].line);
				}
				getLogger().info("Rendered output [" + resultsForWindow.renderBytes(LEAKY_SPANKY) + "]");
				//The formating of these results is tailored very to work with a
				//specially configured "jp@gc Page Data Extractor" from JMeterrPlugins.
				result.setResponseData(resultsForWindow.renderBytes(LEAKY_SPANKY), "UTF-8");
			}

			result.setDataType(SampleResult.TEXT);
			result.setResponseCodeOK();
			result.sampleEnd();// time for all transformations
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exception = sw.toString();
			getLogger().error(exception);

			result.setResponseData(exception, "UTF-8");
			result.setSuccessful(false);
		}
		return result;
	}
	private LeakySpankContext getLeakySpankContext(JavaSamplerContext arg0) {
		LeakySpankContext leakySpankContext = threadLocal.get();		
		if (leakySpankContext==null) {
			long pid = arg0.getLongParameter(P_PID);
			int intervalInSeconds = arg0.getIntParameter(P_INTERVAL_IN_SECONDS, 15);
			int intervalCountPerWindow = arg0.getIntParameter(P_INTERVAL_COUNT_PER_WINDOW, 4);
			int topNSuspectsPerWindow = arg0.getIntParameter(P_TOP_N_SUSPECTS_PER_WINDOW, 2);
			leakySpankContext = new LeakySpankContext(
					pid,
					intervalInSeconds,
					intervalCountPerWindow,
					topNSuspectsPerWindow);
			
			threadLocal.set(leakySpankContext);
		}
			
		return leakySpankContext;
	}
	private String executeJMapHisto(String pidToMonitor, SampleResult result) throws IOException, InterruptedException {
		// String javaHome = System.getenv("JAVA_HOME");
		// String fullCmdPath = javaHome + File.separator + "bin" +
		// File.separator + "jstat";
		// log.info("JAVA_HOME: [" + javaHome + "]");
		// log.info("full [" + fullCmdPath + "]");

		List<String> processArgs = new ArrayList<String>();

		String fullCmdPath = "jmap";
		processArgs.add(fullCmdPath);
		
		// String pidToMonitor = arg0.getParameter("PID");
		//String pidToMonitor = JMeterUtils.getPropDefault("PID", null);
		//String pidToMonitor = "15350";
		
		if (pidToMonitor == null || pidToMonitor.trim().length()==0) {
			String error = "Could not find JMeter variable named 'PID', which must have a value of the process id (pid); of the process you want to detect leaks in.";
			getLogger().error(error);
			result.setResponseData(error, "UTF-8");
			result.setResponseCode("500");
			result.sampleEnd();// time for all transformations
			return LEAKY_SPANKY + "_MISSING_PID=99<BR>\n";
		}
		processArgs.add("-histo");
		processArgs.add(pidToMonitor); // process id

		this.getLogger().info(
				"Args for invoking jmap: [" + processArgs.toString() + "]");
		ProcessBuilder processBuilder = new ProcessBuilder(processArgs);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		StringBuilder processOutput = new StringBuilder();

		BufferedReader processOutputReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

		String readLine;

		int lineCount = 0;
		while ((readLine = processOutputReader.readLine()) != null) {
			if (lineCount > 0)
				processOutput.append(readLine + System.lineSeparator());
			lineCount++;
		}

		process.waitFor();
		return processOutput.toString();
	}

}
/**
//Model finalModel = new Model();
//String previousJMapOutput = JMeterUtils.getProperty(JMAP_HISTO_PREVIOUS);
//Model previousModel = null;
//if (previousJMapOutput!=null && previousJMapOutput.trim().length() > 0) {
//	previousModel = new Model(previousJMapOutput);
//	JMapHistoLine upwardlyMobile[] = currentModel
//			.getAllOrderByMostUpwardlyMobileAsComparedTo(previousModel);
//	for (int i = upwardlyMobile.length; i >= 0 && i > (upwardlyMobile.length - 3); i--) {
//		finalModel.put(upwardlyMobile[i - 1]);
//	}
//}
//
//JMapHistoLine[] mostBytes = currentModel.getAllOrderByBytes();
//for (int i = mostBytes.length; i > (mostBytes.length - 3) && i >= 0; i--) {
//	finalModel.put(mostBytes[i-1]);
//}
//
//// Get ready for next invocation of jmap -histo
//JMeterUtils.setProperty(JMAP_HISTO_PREVIOUS, jMapProcessOutput.toString() );
*/