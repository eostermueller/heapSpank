package com.github.eostermueller.heapspank.leakyspank.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.github.eostermueller.heapspank.leakyspank.console.ApacheCommonsConfigFile;
import com.github.eostermueller.heapspank.leakyspank.console.Config;
import com.github.eostermueller.heapspank.leakyspank.console.MultiPropertyException;
import com.github.eostermueller.heapspank.leakyspank.console.MultiPropertyException.PropertyException;

public class ReadInvalidConfigFromFileTest {
	 @Rule
	    public TemporaryFolder testFolder = new TemporaryFolder();
	 

	 File heapSpankPropertiesFile = null;
	 
	 @Before
	 public void setup() throws IOException {
		 heapSpankPropertiesFile = this.testFolder.newFile(Config.DEFAULT_FILE_NAME);
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append("jmap.histo.interval.seconds=AAA\n");

		 TestUtil.writeFile(this.heapSpankPropertiesFile, sb.toString() );
	 }
	@Test
	public void canDetectExceptionWithOnePropertyIssue() throws ConfigurationException {
		
		try {
			Config c = new ApacheCommonsConfigFile(this.heapSpankPropertiesFile);
		} catch (MultiPropertyException mpe) {
			
			assertEquals(2,mpe.size() );
			
			PropertyException pe = mpe.getPropertyExceptions()[0];
			assertEquals("jmap.histo.interval.seconds",pe.propertyName);
			assertEquals(Exception.class,pe.e.getClass());
		}
		
	}

}

