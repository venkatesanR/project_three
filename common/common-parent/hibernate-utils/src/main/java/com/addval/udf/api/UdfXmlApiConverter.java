package com.addval.udf.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;

import com.addval.utils.StrUtl;
import com.addval.utils.date.DateXBeanConverter;
import com.addval.utils.udf.xmlschema.x2011.XField;
import com.addval.utils.udf.xmlschema.x2011.XFieldMissingValueRequest;

/**
 * This class provides methods to convert XField to UdfField, and UdfField to XFlield.
 *
 * XML-to-Java:
 *			public void convertAndTransfer(XField[] xUdfFieldsArray, UdfEnabled theAppObject, String appXmlNamespace)
 *			public void convertAndTransfer(XField[] xUdfFieldsArray, UdfEnabled theAppObject)
 *
 * Java-to-XML:
 *			public XField[] convert(UdfEnabled theAppObject)
 *
 * NOTES:
 *	(1)	XmlObject "XField" is defined in C:\Projects\common\main\modules\avcommon-xbeans\src\main\xsd\UdfXmlApiXBeans.xsd
 *		under the namespace "http://www.addval.com/utils/udf/xmlschema/2011"
 *	(2) com.addval.udf.api.UdfField is the java "POJO" object that corresponds to XField.

 */
public class UdfXmlApiConverter
{
    private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(UdfXmlApiConverter.class);

    public static final String UDF_XMLNS = "http://www.addval.com/utils/udf/xmlschema/2011";

    private DateXBeanConverter _dateXBeanConverter = null;

	/**
         * Constructor.
         */
    public UdfXmlApiConverter()
    {
        _dateXBeanConverter = new DateXBeanConverter();
	}


	//--------------------------------  XML-to-Java methods --------------------------------

	public void convertAndTransfer(XField[] xUdfFieldsArray, UdfEnabled theAppObject, String appXmlNamespace) {
		if (appXmlNamespace != null) {
			xUdfFieldsArray = convertXFieldNamespace( xUdfFieldsArray, appXmlNamespace,UDF_XMLNS);
		}
		convertAndTransfer( xUdfFieldsArray, theAppObject);
	}

	public void convertAndTransfer(XField[] xUdfFieldsArray, UdfEnabled theAppObject) {
		if (xUdfFieldsArray != null) {
			// First, clear the contents of theAppObject's UdfHolder
			theAppObject.getUdfHolder().clear();

			// Convert and transfer each <Field> into theAppObject's UdfHolder
			for (XField xField : xUdfFieldsArray) {
				if (xField != null) {
					UdfField udfField = convert(xField);
					theAppObject.getUdfHolder().put( udfField.getName(), udfField );
				}
			}
		}
	}

	/**
	 * PROBLEM:
	 * XField complexType is declared in av common namespace, "http://www.addval.com/utils/udf/xmlschema/2011".
	 * But it is used within the app's XML namespace, such as for cargores <Booking> with <Field> array.
	 * The problem is that the child elements of <Field> will appear to be "not set", unless we do some
	 * special processing.
	 */
	public XField[] convertXFieldNamespace(XField[] xFieldsIn, String fromNamespace,String toNamespace ) {
        List<XField> xFieldsOut = new ArrayList<XField>();
		if (xFieldsIn != null && xFieldsIn.length >0 ) {
			XmlOptions xmlOptions = new XmlOptions();
			HashMap<String,String> params = new HashMap<String,String>();
			params.put(fromNamespace, toNamespace);
			xmlOptions.setLoadSubstituteNamespaces(params);

			XField toXField = null;
			for(XField xField:xFieldsIn){
				try {
					toXField = XField.Factory.parse(xField.toString(),xmlOptions);
					xFieldsOut.add(toXField);
				}
				catch (Exception ex) {
					_logger.error("UdfXmlApiConverter.convertXFieldNamespace, exception occurred; xFieldArrayIn="+xFieldsIn+", appXmlNamespace="+fromNamespace+", ex="+ex);
				}
			}
		}
		return (XField[])xFieldsOut.toArray(new XField[0]);
	}

	protected UdfField convert(XField xField) {
		UdfField jField = new UdfField();

		jField.setName( xField.getName() );
		jField.setType( xField.getType() );

		// convert the Value, if any

		if (xField.isSetString() ) {
			jField.setValue( xField.getString() );
		}
		else if (xField.isSetBoolean() ) {
			jField.setValue( xField.getBoolean() );
		}
		else if (xField.isSetInteger() ) {
			jField.setValue( xField.getInteger() );
		}
		else if (xField.isSetDouble() ) {
			jField.setValue( xField.getDouble() );
		}
		else if (xField.isSetDate() ) {
			Date dateValue = null;
			try {
				dateValue = _dateXBeanConverter.toDate(xField.getDate());
			}
			catch( java.text.ParseException ex ) {
				_logger.error("UdfXmlApiConverter.convert(XField): ParseException during Date conversion, ignoring Date value for UdfField " + xField.getName());
			}
			jField.setValue( dateValue );
		}

		// convert the MissingValueRequest, if any

		if (xField.isSetMissingValueRequest()) {
			UdfFieldMissingValueRequest jMissingValueRequest = convert( xField.getMissingValueRequest() );
			jField.setMissingValueRequest( jMissingValueRequest );
		}

		return jField;
	}

	protected UdfFieldMissingValueRequest convert(XFieldMissingValueRequest xMissingValueRequest) {
		UdfFieldMissingValueRequest jMissingValueRequest = new UdfFieldMissingValueRequest();
		if (xMissingValueRequest.isSetRequestOrigin() && !StrUtl.isEmptyTrimmed(xMissingValueRequest.getRequestOrigin() ) ) {
			jMissingValueRequest.setRequestOrigin( xMissingValueRequest.getRequestOrigin() );
		}
		if (xMissingValueRequest.isSetRequesterId() && !StrUtl.isEmptyTrimmed(xMissingValueRequest.getRequesterId() ) ) {
			jMissingValueRequest.setRequesterId( xMissingValueRequest.getRequesterId() );
		}
		if (xMissingValueRequest.isSetRequestExplanation() && !StrUtl.isEmptyTrimmed(xMissingValueRequest.getRequestExplanation() ) ) {
			jMissingValueRequest.setRequestExplanation( xMissingValueRequest.getRequestExplanation() );
		}
		return jMissingValueRequest;
	}


	//--------------------------------  Java-to-XML methods --------------------------------

	public XField[] convert(UdfEnabled theAppObject) {
		List<XField> xFieldList = new ArrayList<XField>();

		// Convert and transfer each UdfField in theAppObject's UdfHolder, into the XField list.
		for (UdfField udfField : theAppObject.getUdfHolder().values()) {
			XField xField = convert(udfField);
			xFieldList.add(xField);
		}
		return (XField[]) xFieldList.toArray( new XField[ xFieldList.size() ] );
	}

	protected  XField  convert(UdfField jField) {

		XField xField = XField.Factory.newInstance();

		xField.setName( jField.getName() );
		xField.setType( jField.getType().toString() );

		// convert the Value, if any

		if(jField.isSetStringValue()) {
			xField.setString( jField.getStringValue() );
		}
		else if (jField.isSetBooleanValue() ) {
			xField.setBoolean( jField.getBooleanValue() );
		}
		else if (jField.isSetIntegerValue() ) {
			xField.setInteger( jField.getIntegerValue() );
		}
		else if (jField.isSetDoubleValue() ) {
			xField.setDouble( jField.getDoubleValue() );
		}
		else if (jField.isSetDateValue() ) {
			xField.setDate( _dateXBeanConverter.toDateString( jField.getDateValue(), false, 0) );
		}

		// convert the MissingValueRequest, if any

		if (jField.isSetMissingValueRequest()) {
			XFieldMissingValueRequest xMissingValueRequest = convert( jField.getMissingValueRequest() );
			xField.setMissingValueRequest( xMissingValueRequest );
		}

		return xField;
	}

	protected  XFieldMissingValueRequest  convert(UdfFieldMissingValueRequest jMissingValueRequest) {
		XFieldMissingValueRequest xMissingValueRequest = XFieldMissingValueRequest.Factory.newInstance();

		if (jMissingValueRequest.isSetRequestOrigin() ) {
			xMissingValueRequest.setRequestOrigin( xMissingValueRequest.getRequestOrigin() );
		}
		if (jMissingValueRequest.isSetRequesterId() ) {
			xMissingValueRequest.setRequesterId( xMissingValueRequest.getRequesterId() );
		}
		if (jMissingValueRequest.isSetRequestExplanation() ) {
			xMissingValueRequest.setRequestExplanation( jMissingValueRequest.getRequestExplanation() );
		}
		return xMissingValueRequest;
	}


}



