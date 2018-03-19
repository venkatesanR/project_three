//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\xmlutils\\Utils.java

package com.addval.xmlutils;

import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xpath.XPathAPI;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.OutputFormat;
import javax.xml.transform.TransformerFactory;
import org.apache.xml.serialize.Serializer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.StringWriter;
import com.addval.utils.LogFileMgr;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMResult;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXNotSupportedException;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class Utils
{
    private com.addval.utils.LogFileMgr _log = null;

    /**
    @param aLog
    @roseuid 3DCE882E0312
     */
    public Utils(com.addval.utils.LogFileMgr aLog)
    {
		_log = aLog;
    }

    /**
    @roseuid 3DCE882E0308
     */
    public Utils()
    {

    }

    /**
    @param xmlString
    @param namespaceAware
    @return org.w3c.dom.Document
    @roseuid 3DCE882E0362
     */
    public Document stringToDocument(String xmlString, boolean namespaceAware)
    {
		Document document = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating( false );
			//factory.setNamespaceAware(namespaceAware);

			DocumentBuilder	builder = factory.newDocumentBuilder();

			StringReader stringIn = new StringReader( xmlString );

			document = builder.parse( new InputSource( stringIn ) );
		}
		catch ( Exception e ) {
			if (_log != null)
			   _log.logError( e );
		}
		finally {
		 return document;
		}
    }

    /**
    @param document
    @return String
    @roseuid 3DCE882E03BE
     */
    public String documentToTextString(Document document)
    {
     	String xmlString = null;

		try {

			document.getDocumentElement().normalize();

			OutputFormat format = new OutputFormat( document );
			format.setPreserveSpace( false );

			StringWriter stringOut = new StringWriter();

			Serializer serial = SerializerFactory.getSerializerFactory("text").makeSerializer(stringOut, format);
			serial.asDOMSerializer().serialize( document.getDocumentElement() );

			xmlString =  stringOut.toString();
		}
		catch ( Exception e ) {
			if (_log != null)
			   _log.logError( e );
		}
		finally {
			return xmlString;
		}
    }

    /**
    @param document
    @return String
    @roseuid 3DCE882F0039
     */
    public String documentToXmlString(Document document)
    {
		String xmlString = null;

		try {

			document.getDocumentElement().normalize();

			OutputFormat format = new OutputFormat( document );
			format.setPreserveSpace( false );

			StringWriter stringOut = new StringWriter();

			Serializer serial = SerializerFactory.getSerializerFactory("xml").makeSerializer(stringOut, format);
			serial.asDOMSerializer().serialize( document.getDocumentElement() );

			xmlString =  stringOut.toString();
		}
		catch ( Exception e ) {
			if (_log != null)
			   _log.logError( e );
		}
		finally {
			return xmlString;
		}
    }

    public String documentToHtmlString(Document document)
    {
		String xmlString = null;

		try {

			document.getDocumentElement().normalize();

			OutputFormat format = new OutputFormat( document );
			format.setPreserveSpace( false );

			StringWriter stringOut = new StringWriter();

			Serializer serial = SerializerFactory.getSerializerFactory(org.apache.xml.serialize.Method.HTML).makeSerializer(stringOut, format);
			serial.asDOMSerializer().serialize( document.getDocumentElement() );

			xmlString =  stringOut.toString();
		}
		catch ( Exception e ) {
			if (_log != null)
			   _log.logError( e );
		}
		finally {
			return xmlString;
		}
    }

	/*
   public String documentToTextString(Document document)
    {
		String xmlString = null;

		try {

			document.getDocumentElement().normalize();

			OutputFormat format = new OutputFormat( document );
			format.setPreserveSpace( false );

			StringWriter stringOut = new StringWriter();

			Serializer serial = SerializerFactory.getSerializerFactory(org.apache.xml.serialize.Method.TEXT).makeSerializer(stringOut, format);
			serial.asDOMSerializer().serialize( document.getDocumentElement() );

			xmlString =  stringOut.toString();
		}
		catch ( Exception e ) {
			if (_log != null)
			   _log.logError( e );
		}
		finally {
			return xmlString;
		}
    }
	*/

    /**
    @param document
    @param xslString
    @return javax.xml.transform.dom.DOMResult
    @throws org.xml.sax.SAXException
    @throws javax.xml.transform.TransformerConfigurationException
    @throws javax.xml.transform.TransformerException
    @throws org.xml.sax.SAXNotSupportedException
    @throws java.io.IOException
    @roseuid 3DCE882F009D
     */
    public DOMResult applyXSL(Document document, String xslString) throws SAXException, TransformerConfigurationException, TransformerException, SAXNotSupportedException, IOException
    {
		DOMResult	domResult	= null;

		TransformerFactory tFactory = TransformerFactory.newInstance();

		if(tFactory.getFeature(DOMSource.FEATURE) && tFactory.getFeature(DOMResult.FEATURE)) {

			DOMParser parser = new DOMParser();
			parser.parse( xslString );

			Document	xslDocument		= parser.getDocument();
			DOMSource	xslDomSource	= new DOMSource( xslDocument );
			Transformer transformer		= tFactory.newTransformer( xslDomSource );
			DOMSource	xmlDomSource	= new DOMSource( document );
			domResult 					= new DOMResult();

			// Perform the transformation, placing the output in the DOMResult.
			transformer.transform( xmlDomSource, domResult );
		}
		else {

			throw new org.xml.sax.SAXNotSupportedException("DOM node processing not supported!");
		}

		return domResult;
    }


    public DOMResult applyXSL(Document document, InputSource xslSource) throws SAXException, TransformerConfigurationException, TransformerException, SAXNotSupportedException, IOException
    {
		DOMResult	domResult	= null;

		TransformerFactory tFactory = TransformerFactory.newInstance();

		if(tFactory.getFeature(DOMSource.FEATURE) && tFactory.getFeature(DOMResult.FEATURE)) {

			DOMParser parser = new DOMParser();
			parser.parse( xslSource );

			Document	xslDocument		= parser.getDocument();
			DOMSource	xslDomSource	= new DOMSource( xslDocument );
			Transformer transformer		= tFactory.newTransformer( xslDomSource );
			DOMSource	xmlDomSource	= new DOMSource( document );
			domResult 					= new DOMResult();

			// Perform the transformation, placing the output in the DOMResult.
			transformer.transform( xmlDomSource, domResult );
		}
		else {

			throw new org.xml.sax.SAXNotSupportedException("DOM node processing not supported!");
		}

		return domResult;
    }


    /**
    @param document
    @param xql
    @return org.w3c.dom.Node
    @throws javax.xml.transform.TransformerException
    @roseuid 3DCE883000F8
     */
    public Node getSingleNode(Document document, String xql) throws TransformerException
    {
		return  XPathAPI.selectSingleNode(document, xql);
    }

    /**
    @param document
    @param xql
    @return org.w3c.dom.NodeList
    @throws javax.xml.transform.TransformerException
    @roseuid 3DCE8830026B
     */
    public NodeList getNodeList(Document document, String xql) throws TransformerException
    {
		return  XPathAPI.selectNodeList(document, xql);
    }

    /**
    @param document
    @param xql
    @return org.w3c.dom.traversal.NodeIterator
    @throws javax.xml.transform.TransformerException
    @roseuid 3DCE883003D4
     */
    public NodeIterator getNodeIterator(Document document, String xql) throws TransformerException
    {
		return  XPathAPI.selectNodeIterator(document, xql);
    }

    /**
    @param document
    @param node
    @return String
    @roseuid 3DCE8831015E
     */
    public String nodeToXmlString(Document document, Node node)
    {
		String xmlString = null;

		try {

			//node.normalize();

			OutputFormat format = new OutputFormat( document );
			format.setPreserveSpace( false );

			StringWriter stringOut = new StringWriter();

			Serializer serial = SerializerFactory.getSerializerFactory("xml").makeSerializer(stringOut, format);
			serial.asDOMSerializer().serialize( (Element) node );

			xmlString =  stringOut.toString();
		}
		catch ( Exception e ) {
			if (_log != null)
			   _log.logError( e );
		}
		finally {
			return xmlString;
		}
    }
}
