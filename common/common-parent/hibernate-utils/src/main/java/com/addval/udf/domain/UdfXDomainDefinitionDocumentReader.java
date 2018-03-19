package com.addval.udf.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.addval.udf.api.UdfApplicationUsageException;
import com.addval.utils.FileUtl;
import com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinitionDocument;
//ParserUtils

/**
 * This implementation of UdfDomainDefinitionReader reads the UDF Domain definition
 * from a file containing a <XUdfDomainDefinition> document, as defined in
 * com.addval.udf.domain.v1.xbeans.* (xmlns=""http://www.addval.com/utils/udf/xmlschema/2011"").
 *
 * The input file must have a name of the form:
 *		UdfDomainDefinitionDocument.<domainName>.xml
 */
public class UdfXDomainDefinitionDocumentReader implements UdfDomainDefinitionReader
{
    private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(UdfXDomainDefinitionDocumentReader.class);


	/**
	 * Attempts to locate and read file named XUdfDomainDefinitionDocument.<domainName>.xml.
	 *
	 * If file exists, attempts to parse its contents into a XUdfDomainDefinitionDocument XmlObject.
	 *
	 * If successful, constructs a java UdfDomainDefinition instance from the XML document, and returns it.
	 */
	public XUdfDomainDefinitionDocument readXUdfDomainDefinitionDocument( String domainName )
	{
		String xmlFilename = "UdfDomainDefinitionDocument."+ domainName + ".xml"; 
		String locationPattern = "classpath:"+ xmlFilename; 
		PathMatchingResourcePatternResolver resourcePattern = new PathMatchingResourcePatternResolver();
		Resource resource = resourcePattern.getResource(locationPattern);
		if(resource == null){
			throw new UdfApplicationUsageException("UdfXDomainDefinitionDocumentReader: Could not find UDF domain definition file, " + xmlFilename);
		}

		
		//String inputFileName = getXmlFilenameFromDomainName(domainName);
		//java.io.File inputXmlFile = locateFile(inputFileName);

		_logger.info("UdfXDomainDefinitionDocumentReader, about to parse file: " + resource.getFilename());

		// parse the file's XML content into an instance of <XUdfDomainDefinitionDocument>
		org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
		xmlOptions.setLoadStripWhitespace();
		XUdfDomainDefinitionDocument xUdfDomainDefinitionDocument = null;
		try {
			xUdfDomainDefinitionDocument = XUdfDomainDefinitionDocument.Factory.parse( resource.getInputStream(), xmlOptions );
		}
		catch (Exception ex) {
			throw new UdfApplicationUsageException("UdfXDomainDefinitionDocumentReader: error parsing XML in file " + xmlFilename, ex);
		}

		if (xUdfDomainDefinitionDocument.schemaType() == null || xUdfDomainDefinitionDocument.schemaType().getJavaClass() == null )
		{
			throw new UdfApplicationUsageException("UdfXDomainDefinitionDocumentReader: unexpected XML parse results from " + xmlFilename);
		}

		// Check that domainName in the document matches
		String domainNameInFile = xUdfDomainDefinitionDocument.getXUdfDomainDefinition().getDomainName();
		if (domainNameInFile == null) {
			_logger.error("UdfXDomainDefinitionDocumentReader, domainName is not specified in input file: " + xmlFilename);
		}
		else if (!domainNameInFile.equals(domainName)) {
			_logger.error("UdfXDomainDefinitionDocumentReader, wrong domainName in file:  expected " + domainName + ", found " + domainNameInFile);
		}

		// Validate the XML.  Log any XML error messages, and throw a RuntimeException if there were any.
		List<String> xmlErrorMessages = validateXml(xUdfDomainDefinitionDocument);
		if (xmlErrorMessages != null && xmlErrorMessages.size() > 0) {
			_logger.error("XML errors detected in " + xmlFilename);
			for (String errMsg : xmlErrorMessages) {
				_logger.error(errMsg);
				System.out.println("XML error: " + errMsg);
			}
			throw new RuntimeException("Number of XML errors: " + xmlErrorMessages.size() );
		}

		return xUdfDomainDefinitionDocument;
	}

	private String getXmlFilenameFromDomainName(String domainName)
	{
    	final String _XML_INPUT_FILE_NAME_PREFIX = "UdfDomainDefinitionDocument";
		String xmlFilename = _XML_INPUT_FILE_NAME_PREFIX + "." + domainName + ".xml";
		return xmlFilename;
	}

	private java.io.File locateFile(String xmlFilename)
	{
		_logger.info("UdfXDomainDefinitionDocumentReader, attempting to locate file: " + xmlFilename);

		// find the file
		String fullInputFileName = null;
		java.io.File inputXmlFile = null;
 		try {
			fullInputFileName = FileUtl.locateResourceFile(UdfXDomainDefinitionDocumentReader.class, xmlFilename);
			if (fullInputFileName == null) throw new Exception("Locate failure");
			inputXmlFile = new java.io.File( fullInputFileName );
			if (inputXmlFile == null) throw new Exception("File create failure");
		}
		catch (Exception ex) {
			throw new UdfApplicationUsageException("UdfXDomainDefinitionDocumentReader: Could not find UDF domain definition file, " + xmlFilename);
		}
		return inputXmlFile;
	}

	/** Adapted from  C:\Projects\ngrm\versions\r4.4\cargobkg\source\com\addval\cargobkg\publicapi\XmlConversionException.java
	 *
	 * Validates the specified XmlObject.  If validation errors are detected, throws XmlConversionException.
	 * Validation error messages (if any) are retrievable via XmlConversionException's getValidationErrorMessages().
	 *
	 * Validation may be disabled for certain packages (specified in private attribute _DO_NOT_VALIDATE_PACKAGE_NAMES.
	 */
	public List<String> validateXml(XmlObject xmlObject)
	{
		List<String> errorMessages = null;
		ArrayList xmlErrorObjectList = new ArrayList();
		boolean isValid = false;

		try
		{
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setErrorListener( xmlErrorObjectList );
			isValid = xmlObject.validate( xmlOptions );
		}
		catch (NullPointerException ex)
		{
			String errMsg = "XBeanUtility.validate(xmlObject), NullPointerException occurred during attempt to validate xmlObject.\n\tThis is probably due to a CLASSPATH problem.\n\tMake sure that all .jar files for all relevant xbeans are on the classpath.\nxmlObject=\n" + xmlObject;
			_logger.error(errMsg, ex);
		}
		catch (Exception ex)
		{
			String errMsg = "XBeanUtility.validate(xmlObject), Exception occurred during attempt to validate xmlObject, exception=" + ex + "\nxmlObject=\n" + xmlObject;
			_logger.error(errMsg, ex);
		}

		if (!isValid)
		{
			errorMessages = new ArrayList<String>( xmlErrorObjectList.size() );
			for (int i=0; i < xmlErrorObjectList.size(); i++ )
			{
				XmlError xmlError = (XmlError) xmlErrorObjectList.get(i);
				errorMessages.add( xmlError.toString() );
			}
		}
		return errorMessages;
	}

}
