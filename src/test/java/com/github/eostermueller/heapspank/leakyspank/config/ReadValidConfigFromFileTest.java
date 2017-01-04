package com.github.eostermueller.heapspank.leakyspank.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.eostermueller.heapspank.leakyspank.console.ApacheCommonsConfigFile;
import com.github.eostermueller.heapspank.leakyspank.console.Config;
import com.github.eostermueller.heapspank.leakyspank.console.MultiPropertyException;

public class ReadValidConfigFromFileTest {
	 @Rule
	    public TemporaryFolder testFolder = new TemporaryFolder();
	 
	//@Rule
	//public ExpectedException thrown = ExpectedException.none();

	 File heapSpankPropertiesFile = null;
	 
	 @Before
	 public void setup() throws IOException {
		 heapSpankPropertiesFile = this.testFolder.newFile(Config.DEFAULT_FILE_NAME);
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append("org.heapspank.jmap.histo.interval.seconds=30\n");
		 sb.append("org.heapspank.jmap.histo.count.per.window=8\n");

		 TestUtil.writeFile(this.heapSpankPropertiesFile, sb.toString() );
	 }
	@Test
	public void canOverrdieDefaultValuesFromFile() throws ConfigurationException, MultiPropertyException {
		
		Config c = new ApacheCommonsConfigFile(this.heapSpankPropertiesFile);
		String msg = "value in properties file was supposed to have overridden default value";
		assertEquals(msg, c.getjMapHistoIntervalSeconds(), 30);
		assertEquals(msg, c.getjMapCountPerWindow(), 8);

	}

}

