package com.addval.wqutils.utils;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import java.util.Iterator;
import java.io.IOException;
import java.net.MalformedURLException;
import com.addval.utils.XRuntime;
import org.xml.sax.SAXException;
import java.net.URL;
import java.util.Hashtable;

public class WQParamLoader {
	private static final String _module = "com.addval.dbutils.DAOSQLLoader";
	public static final String _RULES_URL = "Workqueue.Rules.URL";
	public static final String _QUEUES_URL = "Queues.URL";

	public Hashtable loadQueue(String rulesFile, String inputFile) {

		try {

			URL rules = WQParamLoader.class.getClassLoader().getResource(rulesFile);
			if (rules == null) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				rules = loader.getResource(rulesFile);
			}

			URL input = WQParamLoader.class.getClassLoader().getResource(inputFile);
			if (input == null) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				input = loader.getResource(inputFile);
			}

			if (rules == null || input == null)
				throw new XRuntime(_module, "The Rules File and Input WQ Details File could not be read");

			Digester digester = DigesterLoader.createDigester(rules);
			digester.setNamespaceAware(false);
			digester.setValidating(false);
			WQParamConfiguration config = (WQParamConfiguration) digester.parse(input.openStream());
			return config.getWQQueues();
		} catch (MalformedURLException mue) {

			mue.printStackTrace();
			throw new XRuntime(_module, mue.getMessage());
		} catch (IOException ioe) {

			ioe.printStackTrace();
			throw new XRuntime(_module, ioe.getMessage());
		} catch (SAXException se) {

			se.printStackTrace();
			throw new XRuntime(_module, se.getMessage());
		}
	}
}
