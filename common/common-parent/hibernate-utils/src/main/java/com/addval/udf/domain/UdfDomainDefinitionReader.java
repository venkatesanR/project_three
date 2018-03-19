package com.addval.udf.domain;

import com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinitionDocument;

/**
 * UdfDomainDefinitionReader is the interface implemented by a class that knows
 * how to read a UDF Domain definition, and produce a XUdfDomainDefinitionDocument from it.
 *
 * Known users:  UdfDomainManagerFactory
 *
 * Known implementations:  XmlUdfDomainReader, SqlUdfDomainReader(FUTURE)
 *
 */
public interface UdfDomainDefinitionReader
{
	/**
	 * Reads a UDF Domain Definition for the specified domain name,
	 * and returns it as a XUdfDomainDefinitionDocument instance.
	 */
	public XUdfDomainDefinitionDocument readXUdfDomainDefinitionDocument( String domainName );
}
