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

public class TestEnhancedConsoleOld {

	   
	  
	   	
	
	static String  GCOLD_ORIG_OUTPUT_HEADER =
				  "   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT";
	static String  GCOLD_ORIG_OUTPUT_LINE_1 =
		       " 48128.0  47097.0   5888.0   5637.9    161280.0     89161.4  12973    10    1.775   13.415";
	static String  GCOLD_ORIG_OUTPUT_LINE_2 =
		       "48128.0  47097.0   5888.0   5637.9    161280.0     89185.4  12975    10    2.775   14.415";
	private static String GCNEW_ORIG_OUTPUT = GCOLD_ORIG_OUTPUT_HEADER + "\n" + GCOLD_ORIG_OUTPUT_LINE_1  + "\n" + GCOLD_ORIG_OUTPUT_LINE_2;
	
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
	    		GCOLD_ORIG_OUTPUT_HEADER.trim()+"    POF    POT", 
	    		output[0].trim());
		
	    assertEquals(
	    		"line 1 of gcnew output wasn't correct for enhanced gcnew", 
	    		GCOLD_ORIG_OUTPUT_LINE_1+"    ??    ??", 
	    		output[1]);

	    assertEquals(
	    		"line 1 of gcnew output wasn't correct for enhanced gcnew", 
	    		GCOLD_ORIG_OUTPUT_LINE_2+"    20    20", 
	    		output[2]);
	}

}
