package com.addval.ui;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXException;
import java.net.URL;
import java.util.*;

/**
 * @author AddVal Technology Inc. This class helps in loading the Page Metadata needed by Tapestry 5 an XML file
 */
public class PageMetadataXmlLoader {
	private static final String _MODULE = "com.addval.ui.PageMetadataLoader";

	private static final String _RULESFILE = "com/addval/ui/pagemeta_rules.xml";

	/*
	 * this method loads a comma separated list of xml files. The xml files that appear later in the list will override the ones that appears first in the list For example if page metadata called PAGE123 is defined in filea.xml and fileb.xml If the filenames are defined as: filea.xml,fileb.xml -
	 * then the definition of the page in fileb.xml overrides that in filea.xml
	 * 
	 * This can be used to customize or override page definitions at a project level but use other page definitions at the product level
	 * 
	 * classpath*:com/addval/cargoopstap5/pages/*PageMetadata.xml,etc..
	 */
	public Hashtable loadPageMetadata(String inputFile) {
		Hashtable pageMetadataHash = null;
		if (!StrUtl.isEmptyTrimmed(inputFile)) {
			PathMatchingResourcePatternResolver resourcePattern = new PathMatchingResourcePatternResolver();
			String[] fileLocations = inputFile.split(",");

			if (fileLocations.length > 0) {
				pageMetadataHash = new Hashtable();

				Resource resources[] = null;
				Resource resource = null;
				String fileName = null;

				for (String fileLocation : fileLocations) {
					try {
						resources = resourcePattern.getResources(fileLocation);
						for (int j = 0; j < resources.length; j++) {
							resource = resources[j];
							pageMetadataHash.putAll(loadOnePageMetadataFile(_RULESFILE, resource.getInputStream()));
						}
					} catch (IOException ioe) {
						throw new XRuntime(_MODULE, ioe.getMessage());
					}
				}
			}
		}
		return pageMetadataHash;
	}

	/**
	 * @param rulesFile
	 * @param inputFile
	 * @return java.util.Hashtable
	 * @roseuid 3EA0670D032E
	 */
	private Hashtable loadOnePageMetadataFile(String rulesFile, InputStream inputStream) {

		try {
			URL rules = PageMetadataXmlLoader.class.getClassLoader().getResource(rulesFile);
			if (rules == null) {
				// lets try the threads class loader
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				rules = loader.getResource(rulesFile);
			}
			if (rules == null) {
				throw new XRuntime(_MODULE, "The Rules File could not be read" + rules);
			}
			Digester digester = DigesterLoader.createDigester(rules);
			digester.setNamespaceAware(false);
			digester.setValidating(false);
			PageMetadataConfiguration config = (PageMetadataConfiguration) digester.parse(inputStream);
			return config.getPageDefinitions();
		} catch (MalformedURLException mue) {

			mue.printStackTrace();
			throw new XRuntime(_MODULE, mue.getMessage());
		} catch (IOException ioe) {

			ioe.printStackTrace();
			throw new XRuntime(_MODULE, ioe.getMessage());
		} catch (SAXException se) {

			se.printStackTrace();
			throw new XRuntime(_MODULE, se.getMessage());
		}
	}

}
