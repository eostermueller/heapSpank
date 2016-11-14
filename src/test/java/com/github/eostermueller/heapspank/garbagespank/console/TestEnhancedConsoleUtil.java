package com.github.eostermueller.heapspank.garbagespank.console;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.github.eostermueller.heapspank.garbagespank.GarbageSpank;
import com.github.eostermueller.heapspank.garbagespank.JStatHeaderException;

public class TestEnhancedConsoleUtil {

	
	static String  GCUTIL_ORIG_OUTPUT_HEADER =
				  "S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT";
	static String  GCUTIL_ORIG_OUTPUT_LINE_1 =
			"0.00 100.00  22.38  40.13  97.83  95.74   7259  218.792     0    1.100  218.792";
	static String  GCUTIL_ORIG_OUTPUT_LINE_2 =
			"0.00 100.00  31.67  72.78  97.83  95.74   7307  220.190     0    1.200  220.190";
	private static String GCNEW_ORIG_OUTPUT = GCUTIL_ORIG_OUTPUT_HEADER + "\n" + GCUTIL_ORIG_OUTPUT_LINE_1  + "\n" + GCUTIL_ORIG_OUTPUT_LINE_2;
	
	@Test
	public void test() throws IOException, JStatHeaderException {
		BufferedReader br = new BufferedReader( new StringReader(GCNEW_ORIG_OUTPUT) );
		GarbageSpankConsole jsc = new GarbageSpankConsole();
		jsc.getGarbageSpank().setIntervalInMilliSeconds(5000);
		jsc.setReader(br);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		jsc.setPrintStream(ps);
		int count = 0;
		
		jsc.processJStatLines();
		
		String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);		
		
	    String[] output = content.split("\n");
	    boolean ynHeader = false;
	    
	    assertEquals(
	    		"Header line wasn't correct for enhanced gcnew", 
	    		GCUTIL_ORIG_OUTPUT_HEADER.trim()+"    YGCI    FGCI    YGCTI    FGCTI    AYGCT    AFGCT", 
	    		output[0].trim());
		
	    assertEquals(
	    		"line 1 of gcnew output wasn't correct for enhanced gcnew", 
	    		GCUTIL_ORIG_OUTPUT_LINE_1+"    ??    ??    ??    ??    ??    ??", 
	    		output[1]);

	    assertEquals(
	    		"line 1 of gcnew output wasn't correct for enhanced gcnew", 
	    		GCUTIL_ORIG_OUTPUT_LINE_2+"    48     0 1.398 0.100 1.000 0.000", 
	    		output[2]);
	    
	}

}
