package com.addval.parser.xmlparser;

import com.addval.parser.Utils;
import com.addval.utils.CnfgFileMgr;
import org.apache.commons.beanutils.*;
import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.sql.ResultSet;
import java.util.*;

// Class holding mostly static methods usable in Parsers and Xml Parsers.
public class ParserUtils
{
	// friendly method to read a given fileName and giveout a single line text (removing line breaks)
	public static String getString(String fileName) throws FileNotFoundException, IOException
	{
		return getString( new File( fileName ) );
	}

	// friendly method to read a given file and giveout a single line text (removing line breaks)
	public static String getString(File file) throws FileNotFoundException, IOException
	{
        String input 			=	null;
        input = FileUtils.readFileToString(file, "UTF-8");
        return input;
	}

	// friendly method to convert the given Object into a map
	public static Map convertBean2Hash(Object bean) throws Exception
	{
		return convertBean2Hash( bean, null );
	}

    public static  Map convertBean2Hash(Object bean, Map hash) throws Exception
	{

		if (hash == null)
			hash = new HashMap();
		PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors( bean );
		for (int i = 0; i < origDescriptors.length; i++) {
		    String name = origDescriptors[i].getName();
			try {
		    	if (!PropertyUtils.isReadable( bean, name ))
					continue;
				boolean isSet = true;
				try {
					String methodName = "isSet" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );
					isSet = ((Boolean)MethodUtils.invokeExactMethod( bean, methodName, null )).booleanValue();
				}
				catch(Exception e ) {
				}
				if (isSet)
					hash.put( name, PropertyUtils.getSimpleProperty( bean, name ) );
				else
					hash.put( name, null );
//				System.out.println( " is *********\n" + name + "\t" + PropertyUtils.getSimpleProperty( bean, name ) );
		    }
	        catch(Exception e) {
            }
		}
		return hash;
	}

	// friendly method to convert the given <code>Map</code> (either a <code>HashMap</code> or <code>HashTable</code>)
	// in to an object of given name. (fully qualified name with package is to be specified)
	public static Object convertMap2Bean(Map map, String beanClassName) throws Exception
	{
        return convertMap2Bean( map, Class.forName( beanClassName ).newInstance() );
	}

	// friendly method to convert the given <code>Map</code> (either a <code>HashMap</code> or <code>HashTable</code>)
	// in to an object of given <code>Class</code>.
	public static Object convertMap2Bean(Map map, Class beanClass) throws Exception
	{
        return convertMap2Bean( map, beanClass.newInstance() );
	}

	// friendly method to convert the given <code>Map</code> (either a <code>HashMap</code> or <code>HashTable</code>)
	// in to an object of given <code>Bean Object</code>.
	public static Object convertMap2Bean(Map map, Object bean) throws Exception
	{
		if (bean == null || Utils.isNullOrEmpty( map ))
			return null;
		for (Iterator iter=map.keySet().iterator(); iter.hasNext();) {
			String key = (String)iter.next();
			Object value = map.get( key );
			if (PropertyUtils.isWriteable( bean, key ))
				PropertyUtils.setProperty( bean, key, value );
		}
		return bean;
	}

    // method To validate XmlBeans object resulting from a Xml file
    public static boolean isValidDocument(XmlObject doc)
	{
		return doc.validate();
	}

	// method To validate XmlBeans object resulting from a Xml file
	public static Collection validateDocument(XmlObject doc)
	{
		Collection errors = new ArrayList();
		XmlOptions validateOptions = new XmlOptions();
		validateOptions.put( XmlOptions.ERROR_LISTENER, errors );
		doc.validate( validateOptions );
		return errors;
	}

	// friendly method to prepare <code>DynaBeans</code> from a <code>ResultSet</code>
	// saves the usage of a data holder object
	public static DynaBean[] getDynaBeans(ResultSet rs) throws Exception
	{
		Collection results = new ArrayList();
		ResultSetDynaClass rsdc = new ResultSetDynaClass( rs );
		BasicDynaClass bdc = new BasicDynaClass( "objectName", BasicDynaBean.class, rsdc.getDynaProperties() );
		for (Iterator rows = rsdc.iterator(); rows.hasNext();) {
			DynaBean oldRow = (DynaBean)rows.next();
			DynaBean newRow = bdc.newInstance();
			PropertyUtils.copyProperties( newRow, oldRow );
			results.add( newRow );
		}
		return (DynaBean[])results.toArray( new DynaBean[results.size()] );

	}

	// friendly method to prepare <code>DynaBeans</code> from a <code>ResultSet</code>
	// saves the usage of a data holder object
	public static DynaBean[] getDynaBeans(ResultSet rs, String mappingFileName) throws Exception
	{
		return getDynaBeans( rs, new CnfgFileMgr( mappingFileName ).getProperties() );
	}

	// friendly method to prepare <code>DynaBeans</code> from a <code>ResultSet</code>
	// saves the usage of a data holder object
	public static DynaBean[] getDynaBeans(ResultSet rs, Map map) throws Exception
	{
		BasicDynaClass bdc = new BasicDynaClass( "objectName", BasicDynaBean.class, getDynaProperties( map.values() ) );
		Collection results = new ArrayList();
		while (rs.next()) {
			DynaBean newBean = bdc.newInstance();
			for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
				String dbName = (String)iter.next();
				PropertyUtils.setSimpleProperty( newBean, (String)map.get( dbName ), rs.getString( dbName ) );
			}
			results.add( newBean );
		}
		return (DynaBean[])results.toArray( new DynaBean[results.size()] );
	}

	// friendly method to prepare <code>DynaBeans</code> from a <code>ResultSet</code>
	public static Object[] getBeans(ResultSet rs, Object bean) throws Exception
	{
		if (bean == null)
			return null;
		Collection results = new ArrayList();
		ResultSetDynaClass rsdc = new ResultSetDynaClass( rs );
		Iterator rows = rsdc.iterator();
		while (rows.hasNext()) {
			Object newRow = bean.getClass().newInstance();
			PropertyUtils.copyProperties( newRow, rows.next());
			results.add( newRow );
		}
		return results.toArray( new Object[results.size()] );
	}

	// friendly method to prepare <code>DynaBeans</code> from a <code>ResultSet</code>
	// the mapping file may contain
	public static Object[] getBeans(ResultSet rs, Object bean, String mappingFileName) throws Exception
	{
		if (mappingFileName.endsWith( ".properties" ))
			mappingFileName = mappingFileName.substring( 0, mappingFileName.indexOf( ".properties" ));
		return getBeans( rs, bean, new CnfgFileMgr( mappingFileName ).getProperties() );
	}

	// friendly method to prepare <code>DynaBeans</code> from a <code>ResultSet</code>
	public static Object[] getBeans(ResultSet rs, Object bean, Map map) throws Exception
	{
		Collection results = new ArrayList();
		while (rs.next()) {
			Object newBean = bean.getClass().newInstance();
			for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
				String dbName = (String)iter.next();
				PropertyUtils.setSimpleProperty( newBean, (String)map.get( dbName ), rs.getString( dbName ) );
			}
			results.add( newBean );
		}
		return results.toArray( new Object[results.size()] );
	}

	private static DynaProperty[] getDynaProperties(Collection properties)
	{
		DynaProperty dynaProp[] = new DynaProperty[ properties.size() ];
		int i=0;
		for (Iterator iter = properties.iterator(); iter.hasNext();)
			dynaProp[i++] = new DynaProperty( (String)iter.next() );
		return dynaProp;
	}

	public static String getPrettyXml(String inputXml) throws Exception
	{
		XmlObject msgDoc = XmlObject.Factory.parse( inputXml );
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.put( XmlOptions.SAVE_PRETTY_PRINT );
		xmlOptions.put( XmlOptions.SAVE_PRETTY_PRINT_INDENT, 4 );
		return msgDoc.xmlText( xmlOptions );
	}
}
