package com.github.eostermueller.heapspank.leakyspank.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.github.eostermueller.heapspank.util.IOUtil;


//http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7u40-b43/sun/tools/attach/BsdVirtualMachine.java#BsdVirtualMachine
//http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8-b132/sun/tools/attach/HotSpotVirtualMachine.java/
//import sun.tools.attach.HotSpotVirtualMachine;

/** Getting tools.jar into the classpath from a "java -jar" implementation sounds difficult....
 * so this is a "add tools.jar to classloader" approach.
 * @stolenFrom https://github.com/odnoklassniki/one-nio/blob/master/src/one/nio/mgt/ThreadDumper.java
 * @stolenFrom https://github.com/arturmkrtchyan/sizeof4j/blob/master/src/main/java/com/arturmkrtchyan/sizeof4j/calculation/hotspot/HotSpotHistogram.java
 * @author erikostermueller
 *
 */
public class VirtualMachineWrapper implements JMapHisto {
	private static final String HOT_SPOT_VM_CLASS_NAME = "sun.tools.attach.HotSpotVirtualMachine";
	private static final String HEAP_SPANK = "heapSpank: ";
	
	/**
	 * Mangle the value of this to see if failure messages produce enough 
	 * troubleshooting info.
	 */
	private static final String LIB_FOLDER = "lib";
	static Method heapHistoMethod = null;
	static Method detachMethod = null;
	private static Method attachMethod = null;
	private static boolean debug = true;
	static {
		try {
			Class<?> vmClass = getVMClass();
			attachMethod = vmClass.getMethod("attach", String.class );
			Class[] parameterTypes = new Class[] { Object[].class };
			//Method[] m = vmClass.getMethods();
			heapHistoMethod = vmClass.getMethod("heapHisto", parameterTypes);
			detachMethod = vmClass.getMethod("detach", null);
		} catch (Exception e) {
			error("Unable to create [" + HOT_SPOT_VM_CLASS_NAME + "]");
			System.err.println(e.getMessage());
			//e.printStackTrace();//not sure if user will see this!
		}
	}
	Object vm = null;
	private String pid;
	public VirtualMachineWrapper(String pid) throws ProcessIdDoesNotExist, JMapHistoException  {
		this.setPid(pid);
		this.installShutdownHook();
		try {
			
			if (attachMethod!=null) {
				vm = attachMethod.invoke(null, pid);
				if (vm==null) {
					ProcessIdDoesNotExist pidne = new ProcessIdDoesNotExist();
					pidne.setProcessId( this.getPid() );
					throw pidne;
				}
			}
		} catch (InvocationTargetException e) {
			new JMapHistoException(e);
			ProcessIdDoesNotExist pidne = new ProcessIdDoesNotExist(e);
			pidne.setProcessId( this.getPid() );
			throw pidne;
		} catch(IllegalAccessException iae) {
			JMapHistoException jmhe = new JMapHistoException(iae);
			jmhe.setProcessId(this.getPid());
			throw jmhe;
		} catch (IllegalArgumentException iae2) {
			JMapHistoException jmhe = new JMapHistoException(iae2);
			jmhe.setProcessId(this.getPid());
			throw jmhe;
		}
		//Thread.sleep(100);
	}
	private static void error(String string) {
		System.out.println("##################### ERROR:" + string);
	}
	public void detach() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (this.vm!=null) {
			this.detachMethod.invoke(vm, new Object[]{});
		} 
	}
    private static File findToolsJar() throws ToolsJarDoesNotExist {
    	
        String javaHome = System.getProperty("java.home");
        File javaHomeFile = new File(javaHome);
        
        debug("Looking for tools.jar in/around java.home [" + javaHome + "]");

        File toolsJarFile = new File(javaHomeFile, LIB_FOLDER + File.separator + "tools.jar");
        File toolsJarFileIbmAndMac17andNewer = new File(javaHomeFile.getParentFile(), LIB_FOLDER + File.separator + "tools.jar");
        File toolsJarFileMac15and16 = new File(javaHomeFile.getParentFile(), "Classes" + File.separator + "classes.jar");

        if (debug) {
            debug("Candidate locations for tools.jar:");
            String format = "Exists? %5b Path: %s\n";
            debug(String.format(format, toolsJarFile.exists(), toolsJarFile.getAbsoluteFile()) );
            debug(String.format(format, toolsJarFileIbmAndMac17andNewer.exists(), toolsJarFileIbmAndMac17andNewer.getAbsoluteFile()));
            debug(String.format(format, toolsJarFileMac15and16.exists(), toolsJarFileMac15and16.getAbsoluteFile()));
        }
  
        File rc = null;
        
        if (toolsJarFile.exists())
        	rc = toolsJarFile;
        else if (toolsJarFileIbmAndMac17andNewer.exists())
        	rc = toolsJarFileIbmAndMac17andNewer;
        else if (toolsJarFileMac15and16.exists())
        	rc = toolsJarFileMac15and16;

        if (rc==null || !rc.exists()) {
        	ToolsJarDoesNotExist e = new ToolsJarDoesNotExist();
        	e.addAttemptedLocation(toolsJarFile);
        	e.addAttemptedLocation(toolsJarFileIbmAndMac17andNewer);
        	e.addAttemptedLocation(toolsJarFileMac15and16);
        	e.setJavaHome(javaHomeFile);
        	throw e;
        }
        if (debug) {
        	if (rc!=null)
        		debug("Using [" + rc.getAbsolutePath() + "]");
        }
        
        return rc;
    }	
	private static void debug(String string) {
		System.out.println(HEAP_SPANK + string);
	}
	/**
        if (!toolsJarFile.exists()) {
            // If we're on an IBM SDK, then remove /jre off of java.home and try again.
            if (javaHomeFile.getAbsolutePath().endsWith(File.separator + "jre")) {
                javaHomeFile = javaHomeFile.getParentFile();
                toolsJarFile = new File(javaHomeFile, "lib" + File.separator + "tools.jar");
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            	File toolsFolder = null;
            	if (javaHomeFile.getName().equalsIgnoreCase("jre"))
            		toolsFolder = new File(javaHomeFile, ".." + File.separator + "lib");
            	else
            		toolsFolder = new File(javaHomeFile, "lib");
            	toolsJarFile = new File(toolsFolder, "tools.jar");
            	
            	if (!toolsJarFile.exists()) {
                    // If we're on a 1.5 or 1.6 Mac, then change the search path to use ../Classes/classes.jar.
                    if (javaHomeFile.getAbsolutePath().endsWith(File.separator + "Home")) {
                        javaHomeFile = javaHomeFile.getParentFile();
                        toolsJarFile = new File(javaHomeFile, "Classes" + File.separator + "classes.jar");
                    }
            	}
            }
        }
     * @return
	 * @throws JMapHistoException 
	 * @throws Exception
     */
	 // tools.jar must be in class path when loading ThreadDumper implementation
    private static Class<?> getVMClass() throws JMapHistoException  {
    	File toolsJar = findToolsJar();
    	Class<?> c = null;
		URL[] urls;
		try {
			urls = new URL[] {
			        toolsJar.getCanonicalFile().toURI().toURL(),
					VirtualMachineWrapper.class.getProtectionDomain().getCodeSource().getLocation(),
			};
			try { Thread.sleep(1000);} catch (InterruptedException e) {}
			
			URLClassLoader loader = new URLClassLoader(urls, null);
			//Class<?> c = loader.loadClass("com.sun.tools.attach.VirtualMachine");
			c = loader.loadClass(HOT_SPOT_VM_CLASS_NAME);
		} catch (Exception e1) {
			JMapHistoException e2 = new JMapHistoException(e1);
			throw e2;
		}
        return c;
    }
	/**
	 * The following link shows that -live or -all must be passed to heapHist() with java 1.8 and greater. 
	 * http://cr.openjdk.java.net/~chegar/8153181/00/hotspot/src/share/vm/services/attachListener.cpp.rhs.html
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public String heapHisto(boolean live) throws JMapHistoException {
		String parm = null;
		if (live)
			parm = "-live";
		else 
			parm = "-all";
		Object[] arguments = new Object[] {  new String[] { parm }  } ; //trigger gc b4 cnt
		Object rc;
		String histo = null;
		try {
			if (this.heapHistoMethod!=null) {
				rc = this.heapHistoMethod.invoke(this.vm, arguments);
		        final InputStream in = (InputStream)rc;
		        histo = IOUtil.read(in);
			}
		//} catch (IllegalAccessException | IllegalArgumentException | IOException | InvocationTargetException e ) {
		} catch (Exception e ) {
			JMapHistoException jmhe = new JMapHistoException(e);
			jmhe.setProcessId(this.getPid());
			throw jmhe;
		} 
		return histo;
	}
	private void installShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	            try {
	                Thread.sleep(200);
	                VirtualMachineWrapper.this.detach();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });		
	}
	@Override
	public void setPid(String s) {
		this.pid = s;
	}
	@Override
	public String getPid() {
		return this.pid;
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
		if (jmapResult!=null) {
			if (jmapResult.indexOf(JMapHisto.HEADER) >0) {
				/**
				 * If Object does not show up in list of loaded classes, 
				 * there is a problem.
				 */
				if (jmapResult.indexOf("java.lang.Object") > 0) {
					rc = true;
				}
			}
		} 
		if (!rc) {
			JMapHistoException e = new JMapHistoException();
			e.setTestData(jmapResult);
		}
		return jmapResult;
	}
	@Override
	public void shutdown() {
		try {
			this.detach();
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
	}
}
