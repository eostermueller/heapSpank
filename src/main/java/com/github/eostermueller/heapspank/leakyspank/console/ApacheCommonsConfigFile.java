package com.github.eostermueller.heapspank.leakyspank.console;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;

/**
 * currently, setters (the inherited ones) will not behave as expected.
 * @author erikostermueller
 *
 */
public class ApacheCommonsConfigFile extends DefaultConfig {
	private static final String PROPERTIES_FILE = "heapSpank.properties";
	CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
 
	/**
	 * only used for unit testing -- loads
	 * @param heapSpankPropertiesFile
	 * @throws ConfigurationException
	 * @throws MultiPropertyException
	 */
	public ApacheCommonsConfigFile(File heapSpankPropertiesFile) throws ConfigurationException, MultiPropertyException {
		
		Configurations configurations = new Configurations();
		PropertiesConfiguration config = configurations.properties(heapSpankPropertiesFile);		
				
		this.compositeConfiguration.addConfiguration( config );
		
	}
	/**
	 * This ctor supports 4 levels of configuration.
	 * Levels higher in the list override levels lower in the list.
	 * <ol>
	 * 		<li>heapSpank properties passed in as java -D parameters, aka "Java System Properties" </li>
	 * 		<li>heapSpank.properties file in the current folder</li>
	 * 		<li>heapSpank.properties file in the current user's 'home' folder.</li>
	 * 		<li>com.github.eostermueller.heapspank.leakyspank.console.DefaultConfig</li>
	 * @throws ConfigurationException
	 */
	public ApacheCommonsConfigFile() throws ConfigurationException {
		this.compositeConfiguration.addConfiguration( new SystemConfiguration() );
		this.compositeConfiguration.addConfiguration( getCurrentFolderConfiguration() );
		this.compositeConfiguration.addConfiguration( getHomeFolderConfiguration() );
		this.compositeConfiguration.addConfiguration( getHeapSpankJarConfiguration() );
	}
	private Configuration getHeapSpankJarConfiguration() throws ConfigurationException {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
		    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
		    	.configure(params.properties()
		        .setFileName(PROPERTIES_FILE)
		        .setLocationStrategy(new ClasspathLocationStrategy())
		        );		
		Configuration config = builder.getConfiguration();		
		return config;
	}
	private Configuration getHomeFolderConfiguration() throws ConfigurationException {
		File homeDir = new File( System.getProperty("user.home"));
		
		File heapSpankProperties = new File(homeDir, this.PROPERTIES_FILE);
		Parameters params = new Parameters();
		
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
			    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
			    .configure(params.properties()
			        .setFile(heapSpankProperties));
		return builder.getConfiguration();
	}
	private Configuration getCurrentFolderConfiguration() throws ConfigurationException {
		
		URL location = ApacheCommonsConfigFile.class.getProtectionDomain().getCodeSource().getLocation();
		File heapSpankjarFile = new File(location.getFile());
		File dirOfHeapSpankjarFile = new File(location.getFile()).getParentFile();
		File heapSpankProperties = new File(dirOfHeapSpankjarFile, this.PROPERTIES_FILE);
		Parameters params = new Parameters();
		
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
			    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
			    .configure(params.properties()
			        .setFile(heapSpankProperties));
		return builder.getConfiguration();
	}
		//Configuration config = buildOther.getConfiguration();
		
//		AbsoluteNameLocationStrategy currentDirLocator = new AbsoluteNameLocationStrategy() {
//			/**
//			 * @stolenFrom http://stackoverflow.com/questions/4871051/getting-the-current-working-directory-in-java
//			 */
//			public URL locate(FileSystem fileSystem, FileLocator locator) {
//				URL location = ApacheCommonsConfigFile.class.getProtectionDomain().getCodeSource().getLocation();
//				return location;
//			}
//		};
//		Parameters params = new Parameters();
//		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
//		    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
//		    	.configure(params.properties()
//		        .setFileName(PROPERTIES_FILE)
//		        .setLocationStrategy(currentDirLocator)
//		        );		
//		Configuration config = builder.getConfiguration();		
//		return null;
//	}
	private Configuration getConfiguration() {
		return this.compositeConfiguration;
	}

	@Override
	public String getRegExExclusionFilter() {
		String rc = "<<uninit>>"; 
		String key = "org.heapspank.regex.exclusion.filter";

		rc = this.getConfiguration().getString(key);
		configDebug("heapSpank config: " + key + "=" + rc);

		return rc;
	}

	private void configDebug(String string) {
		//System.out.println(string);
	}
	@Override
	public String getViewClass() {
		String rc = "<<uninit>>"; 
		String key = "org.heapspank.view.class";

		rc = this.getConfiguration().getString(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
		
	}

	@Override
	public int getSuspectCountPerWindow() {
		int rc = -1; 
		String key = "org.heapspank.suspect.count.per.window";

		rc = this.getConfiguration().getInt(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}

	@Override
	public int getjMapCountPerWindow() {
		int rc = -1; 
		String key = "org.heapspank.jmap.histo.count.per.window";

		rc = this.getConfiguration().getInt(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}

	@Override
	public int getjMapHistoIntervalSeconds() {
		int rc = -1; 
		String key = "org.heapspank.jmap.histo.interval.seconds";

		rc = this.getConfiguration().getInt(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}

	@Override
	public int getScreenRefreshIntervalSeconds() {
		int rc = -1; 
		String key = "org.heapspank.screen.refresh.interval.seconds";

		rc = this.getConfiguration().getInt(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}

	@Override
	public int getMaxIterations() {
		int rc = -1; 
		String key = "org.heapspank.max.iterations";

		rc = this.getConfiguration().getInt(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}
	@Override
	public int getDisplayRowCount() {
		int rc = -1; 
		String key = "org.heapspank.display.row.count";

		rc = this.getConfiguration().getInt(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}
	@Override
	public boolean getJMapHistoLive() {
		boolean rc = false; 
		String key = "org.heapspank.jmap.histo.live";

		rc = this.getConfiguration().getBoolean(key);
		configDebug("heapSpank config: " + key + "=" + rc);
		
		return rc;
	}
}
