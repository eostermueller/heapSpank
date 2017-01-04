package com.github.eostermueller.heapspank.leakyspank.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestUtil {
	public static void writeFile(File f, String text) throws IOException {
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		
		try {
			fileWriter = new FileWriter(f);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(text);
		} finally {
			if (bufferedWriter != null) bufferedWriter.close();
			if (fileWriter!=null) fileWriter.close();
		}
		
	}

}
