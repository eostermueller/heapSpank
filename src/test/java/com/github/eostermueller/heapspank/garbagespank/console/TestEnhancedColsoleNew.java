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

public class TestEnhancedColsoleNew {

	static String  GCNEW_ORIG_OUTPUT_HEADER =
				  "S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT";
	static String  GCNEW_ORIG_OUTPUT_LINE_1 =
		       "12288.0 12288.0 2784.1    0.0 15  15 12288.0 1155072.0 710885.4   6912   13.415";
	static String  GCNEW_ORIG_OUTPUT_LINE_2 =
		       "11776.0 11776.0 4559.6    0.0 15  15 11776.0 1156096.0 1029187.1   6914   14.415";
	private static String GCNEW_ORIG_OUTPUT = GCNEW_ORIG_OUTPUT_HEADER + "\n" + GCNEW_ORIG_OUTPUT_LINE_1  + "\n" + GCNEW_ORIG_OUTPUT_LINE_2;
	
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
	    		GCNEW_ORIG_OUTPUT_HEADER+"    POY", 
	    		output[0]);
		
	    assertEquals(
	    		"line 1 of gcnew output wasn't correct for enhanced gcnew", 
	    		GCNEW_ORIG_OUTPUT_LINE_1+"    ??", 
	    		output[1]);

	    assertEquals(
	    		"line 1 of gcnew output wasn't correct for enhanced gcnew", 
	    		GCNEW_ORIG_OUTPUT_LINE_2+"    20", 
	    		output[2]);
	}

}
