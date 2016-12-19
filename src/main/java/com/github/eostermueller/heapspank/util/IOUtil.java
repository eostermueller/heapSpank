package com.github.eostermueller.heapspank.util;


import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * Stolen from:
 * https://github.com/arturmkrtchyan/sizeof4j/blob/master/src/main/java/com/arturmkrtchyan/sizeof4j/util/IOUtil.java
 * @author arturmkrtchyan
 *
 */
public class IOUtil {

    private IOUtil() {}

    public static String read(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte b[] = new byte[256];
        int n;
        do {
            n = in.read(b);
            if (n > 0) {
                builder.append(new String(b, 0, n, "UTF-8"));
            }
        } while (n > 0);
        in.close();
        return builder.toString();
    }
	/**
	 * @stolen from http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
	 * @return
	 */
	public static long getMyPid() {
		  String processName =
			      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			    return Long.parseLong(processName.split("@")[0]);		
	}

}