package com.github.eostermueller.heapspank.garbagespank.jmeter;

import com.github.eostermueller.heapspank.garbagespank.GarbageSpank;
import com.github.eostermueller.heapspank.garbagespank.JStatLine;
import com.github.eostermueller.heapspank.garbagespank.JStatLinePrevious;
import com.github.eostermueller.heapspank.garbagespank.JStatOption;

import java.io.*;
import java.lang.ProcessBuilder;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;

/**
 * A JMeter sampler that executes jstat to get GC performance metrics.
 * @author erikostermueller
 *
 */
public class GarbageSpankSampler extends AbstractJavaSamplerClient {
	GarbageSpank garbageSpank = new GarbageSpank();
	private String propertyNameForPreviousMeasurement = null;

	private static final String P_PID = "pid";
	private static final String P_INTERVAL_IN_SECONDS = "interval_in_seconds";
	private static final String P_JSTAT_OPTION = "jstat_option";
	
	private static final String GARBAGE_SPANK = "gs_";

	private static final String PREVIOUS_JSTAT = "previous_jstat";
	private static final String LOG_PREFIX = "garbageSpank:";
	
	public GarbageSpankSampler () {
		getLogger().debug(LOG_PREFIX+"ctor of GarbageSpankSampler [" + this.hashCode() + "] threadId: [" + Thread.currentThread().getId() + "]");
	}
	@Override
	public Arguments getDefaultParameters() {
	    Arguments defaultParameters = new Arguments();
	    defaultParameters.addArgument(P_PID, "${PID}");
	    defaultParameters.addArgument(P_INTERVAL_IN_SECONDS, "${GARBAGE_SPANK_INTERVAL_IN_SECONDS}");
	    defaultParameters.addArgument(P_JSTAT_OPTION, "${JSTAT_OPTION}");
	    return defaultParameters;
	}
	public SampleResult runTest(JavaSamplerContext jmeterSamplerContext) {
		
		getLogger().debug(LOG_PREFIX + "Start of GarbageSpankSampler#runTest");
		SampleResult result = new SampleResult();
		result.setDataType(SampleResult.TEXT);
		if (garbageSpank.getPid()==-1) {  //init

			String pidToMonitor = jmeterSamplerContext.getParameter(P_PID);
			if (pidToMonitor == null || pidToMonitor.trim().length()==0) {
				String error = "Could not find JMeter variable named 'PID', which must have a value of the process id (pid); of the process you want to detect leaks in.";
				getLogger().error(LOG_PREFIX+error);
				result.setResponseData(GARBAGE_SPANK + "_JMETER_PID_VARIABLE_NOT_SET=99<BR>\n", "UTF-8");
				result.setResponseCode("500");
				result.sampleEnd();// time for all transformations
				return result;
			}
					
			garbageSpank.setPid( Long.parseLong(pidToMonitor) );
			garbageSpank.setIntervalInMilliSeconds(jmeterSamplerContext.getIntParameter(P_INTERVAL_IN_SECONDS) );
			String strJStatOption = jmeterSamplerContext.getParameter(P_JSTAT_OPTION);
			garbageSpank.setJStatOption( JStatOption.valueOf(strJStatOption) );
			propertyNameForPreviousMeasurement = PREVIOUS_JSTAT + "_" + garbageSpank.getJStatOption() + "_" + String.valueOf(Thread.currentThread().getId()).trim();

		}
		result.sampleStart(); // start stopwatch
		String previousJStatStdOut = null;
		try {

			String currentJStatProcessStdOut = executeJStat(garbageSpank.getPid(),result);
			if (currentJStatProcessStdOut == null || currentJStatProcessStdOut.trim().length()==0) {
				String error = "no jstat output.  Is PID [" + garbageSpank.getPid() + "] still alive?  Found that PID in the HEAPSPANK_PID variable in JMeter.  Also, be sure JAVA_HOME/bin in your OS's PATH variable, so I can find jstat.";
				getLogger().error(error);
				result.setSuccessful(false);
				result.setSamplerData("jstat -" + garbageSpank.getJStatOption() + " " + garbageSpank.getPid() );
				result.setResponseData(error, "UTF-8");
			} else {
				this.getLogger().debug(LOG_PREFIX + " jstat  current [" + garbageSpank.getJStatOption() + "] stdout [" + currentJStatProcessStdOut + "]");

				
				previousJStatStdOut = JMeterUtils.getProperty( propertyNameForPreviousMeasurement );
				this.getLogger().debug(LOG_PREFIX + " jstat previous [" + garbageSpank.getJStatOption() + "] stdout [" + previousJStatStdOut + "]");

				if ( (previousJStatStdOut!=null && previousJStatStdOut.trim().length()!=0) || garbageSpank.getMetric().metricsAvailableOnFirstJStatRun()) {
					JStatLine currentLine = new JStatLine(
							GARBAGE_SPANK,
							garbageSpank.getMetric(), 
							currentJStatProcessStdOut,
							garbageSpank.getIntervalInMilliSeconds() );

					JStatLinePrevious previousLine = null;				
					if (previousJStatStdOut!=null && previousJStatStdOut.trim().length()!=0) {
						previousLine = new JStatLinePrevious(
								this.GARBAGE_SPANK, 
								garbageSpank.getMetric(), 
								previousJStatStdOut, 
								garbageSpank.getIntervalInMilliSeconds() );
					}//previousLine == null when there are no calculated columns.
					currentLine.setPrevious(previousLine);
					
					String outputToPageDataExtractor = currentLine.getPageDataExtractorFormat();			
					this.getLogger().debug(LOG_PREFIX + "[" + garbageSpank.getJStatOption() + "] PageDataExtractor [" + garbageSpank.getJStatOption() + "] format: [" + outputToPageDataExtractor + "]");
					result.setResponseData(outputToPageDataExtractor, "UTF-8");
				} else { //else no reporting available on first run.  Why?  Two jstat runs must complete before our calculated columns are available.
					result.setResponseData("Full result will not be rendered until an additional jstat measurement (with this same 'option') is captured.", "UTF-8");
				}
				
			
				result.setSuccessful(true);
				result.setResponseCodeOK();
				result.setResponseOK();
				result.setSamplerData("jstat -" + garbageSpank.getJStatOption() + " " + garbageSpank.getPid() + "\n" + currentJStatProcessStdOut);
			
				//We'll need this for the next iteration.
				JMeterUtils.setProperty(propertyNameForPreviousMeasurement, currentJStatProcessStdOut);
				
				result.sampleEnd();// time for all transformations
				
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exception = sw.toString();
			getLogger().error(LOG_PREFIX+exception);

			result.setResponseData(exception, "UTF-8");
			result.setSuccessful(false);
		}
		return result;
	}
	private String executeJStat(long pidToMonitor, SampleResult result) throws IOException, InterruptedException {
		// String javaHome = System.getenv("JAVA_HOME");
		// String fullCmdPath = javaHome + File.separator + "bin" +
		// File.separator + "jstat";
		// log.info("JAVA_HOME: [" + javaHome + "]");
		// log.info("full [" + fullCmdPath + "]");

		List<String> processArgs = new ArrayList<String>();

		String fullCmdPath = "jstat";
		processArgs.add(fullCmdPath);
		
		// String pidToMonitor = arg0.getParameter("PID");
		//String pidToMonitor = JMeterUtils.getPropDefault("PID", null);
		//String pidToMonitor = "15350";
		
		processArgs.add( "-" + this.garbageSpank.getJStatOption() );
		processArgs.add( String.valueOf(pidToMonitor)); // process id

		this.getLogger().debug(LOG_PREFIX+
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