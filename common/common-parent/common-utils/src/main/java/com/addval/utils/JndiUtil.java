package com.addval.utils;


import javax.naming.*;
import java.io.*;
import java.util.*;

public class JndiUtil {
	private static Context jndiContext = null;
	private static Object  obj		   = null;


	public static Object lookup(String name) throws NamingException {

		if (jndiContext == null)
		{
			try {
				//
				// The following properties should be setup in jndi.properties file
				// jndi.properties file should be in the classpath
				//
				// java.naming.provider.url
				// java.naming.factory.initial

				jndiContext = new InitialContext();
			} catch (NamingException e) {
				System.out.println("Could not create JNDI context: " + e.toString());
				throw e;
			}
		}

		try {
			obj = jndiContext.lookup(name);
		} catch (NamingException e) {
			System.out.println("JNDI lookup failed: " + e.toString());

			throw e;
		}

		return obj;
	}
}
