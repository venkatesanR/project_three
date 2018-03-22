/*
 * FileUtl.java
 *
 * Created on April 19, 2007, 1:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.techmania.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * General file utility to read and write etc.
 * 
 * @author ravi
 */
public class FileUtl {

	/** Creates a new instance of FileUtl */
	public FileUtl() {
	}

	/**
	 * Returns file as string from a url
	 */
	public static String readFile(URL url) throws IOException {
		return readFile(url.openStream());
	}

	/**
	 * Returns file as string from a File
	 */
	public static String readFile(File file) throws IOException {
		return readFile(new FileInputStream(file));
	}

	/**
	 * Returns file as string from a complete file path
	 */
	public static String readFile(String filepath) throws IOException {
		return readFile(new File(filepath));
	}

	/**
	 * Returns file as string from an input stream. closes input stream
	 */
	public static String readFile(InputStream inputStream) throws IOException {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(inputStream);

			String s = null;
			StringBuffer sb = new StringBuffer();
			char[] chars = new char[256];
			int len = 0;
			while ((len = isr.read(chars)) > 0) {
				sb.append(chars, 0, len);
			}
			s = sb.toString();

			return s;
		} finally {
			inputStream.close();
			isr.close();
		}
	}

	/**
	 * Locates specified class resource file, returning fully-qualified
	 * path+filename, or null if file not found.
	 *
	 * The primary motivation for adding this method is to provide a standard
	 * way for junit Test classes to locate any test input file(s) that the test
	 * class needs to read, especially when running tests under Maven.
	 *
	 * This method looks in 3 places for the specified file, in this order: (1)
	 * The classloader's classpath. (When running under Maven, this will be the
	 * /test/test-classes folder.) (2) The specified class's location in
	 * classpath. (When running under Maven, this will be
	 * /test/test-classes/com/mycompany/myapp folder.)
	 *
	 * Input args: Class aClass - the caller's Class. String simpleFilename -
	 * simple (i.e. no path) filename for the resource file to locate.
	 *
	 * Example usage:
	 *
	 * package com.mycompany.myapp;
	 *
	 * import com.addval.utils.FileUtl; import java.io.*;
	 *
	 * public class MyTestClass extends junit.framework.TestCase { private
	 * static final String _INPUT_FILENAME = "MyTestClass.input.data";
	 *
	 * public void testFromInputFile() { String fullFilename =
	 * FileUtl.locateResourceFile( this.getClass(), _INPUT_FILENAME ); if
	 * (fullFilename == null) { fail("Test input file not found, " +
	 * _INPUT_FILENAME ); } try { BufferedReader br = new BufferedReader( new
	 * FileReader( fullFilename ) ); // read the file, perform tests, etc.....
	 *
	 */
	public static String locateResourceFile(Class aClass, String simpleFilename) {
		String fullFilename = null;
		try {
			// (1) First, search for the file on the class loader's class path
			URL url = aClass.getClassLoader().getResource(simpleFilename);

			// (2) If not found, search the class path for the specified class's
			// package name.
			if (url == null) {
				url = aClass.getResource(simpleFilename);
			}

			// If found by either (1) or (2), url.getPath will return
			// fully-qualified path+file name, but with a leading "/" in front.
			if (url != null) {
				fullFilename = url.getPath().substring(1); // Omit the leading
															// "/"
			}
		} catch (Exception e) {
		}
		return fullFilename;
	}

}
