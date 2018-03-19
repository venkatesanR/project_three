//Source file: c:\\projects\\common\\src\\client\\source\\com\\addval\\environment\\EJBEnvironment.java

//Source file: C:\\Projects\\Common\\src\\client\\source\\com\\addval\\environment\\EJBEnvironment.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.environment;

import javax.rmi.PortableRemoteObject;
import com.addval.utils.AVConstants;
import com.addval.utils.CnfgFileMgr;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.MissingResourceException;

public class EJBEnvironment extends Environment
{

	/**
	 * The configuration file with JNDI properties for the environment.
	 */
	protected CnfgFileMgr _jndiCnfgFile = null;
	private static final String _JNDI_RESOURCE_BUNDLE_NAME = "env.jndiResourceBundleName";
	protected String _jndiResourceBundleName = null;


	/**
	 * @roseuid 3BEC78C903C8
	 */
	protected EJBEnvironment()
	{

	}


	public static String getClassName()
	{
		return "com.addval.environment.EJBEnvironment";
	}


	public void setJndiResourceBundleName(String aBundle) {
		_jndiResourceBundleName = aBundle;

		if (getJndiResourceBundleName() != null ) {
			_jndiCnfgFile = new CnfgFileMgr( getJndiResourceBundleName() );
			if ( _jndiCnfgFile != null && ( _jndiCnfgFile.getProperties() == null || _jndiCnfgFile.getProperties().size() == 0) )
				_jndiCnfgFile = null;
		} else {
			_jndiCnfgFile = null;
		}
	}


	public String getJndiResourceBundleName() {
		return _jndiResourceBundleName;
	}


	public void setJndiResourceProperties(Properties prop) {
		if (prop != null ) {
			_jndiCnfgFile = new CnfgFileMgr( prop );
			if ( _jndiCnfgFile != null && ( _jndiCnfgFile.getProperties() == null || _jndiCnfgFile.getProperties().size() == 0) )
				_jndiCnfgFile = null;
		} else {
			_jndiCnfgFile = null;
		}
	}


	public Properties getJndiResourceProperties() {
		if (_jndiCnfgFile != null) {
			return _jndiCnfgFile.getProperties();
		} else {
			return null;
		}
	}



	/**
	 * @param config
	 * @roseuid 3AE8D33C0085
	 */
	protected EJBEnvironment(String config)
	{
		super( config );
	}

	/**
	 * @return javax.naming.InitialContext
	 * @throws javax.naming.NamingException
	 * @roseuid 3B60B08C02A8
	 */
	public InitialContext getSubsystemContext() throws NamingException
	{
		if ( _jndiCnfgFile != null ) {
			return new InitialContext( _jndiCnfgFile.getProperties() );
		}
		else {
			return new InitialContext();
		}
	}

	/**
	 * @param subSystem
	 * @return javax.naming.InitialContext
	 * @throws javax.naming.NamingException
	 * @roseuid 3F1F73C10019
	 */
	public static InitialContext getContext(String subSystem) throws NamingException
	{
		return ((EJBEnvironment)Environment.getInstance( subSystem )).getSubsystemContext();
	}

	/**
	 * @param propsFile
	 * @return javax.naming.InitialContext
	 * @throws javax.naming.NamingException
	 * @roseuid 3BF2D68E03CF
	 */
	public static InitialContext getContextUsingBundle(String propsFile) throws NamingException
	{
	   return getContext( new CnfgFileMgr( propsFile ).getProperties() );
	}

	/**
	 * @param name
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3BEAED0B004E
	 */
	public Object lookupSubsystemJndiName(String name) throws NamingException
	{
		return getSubsystemContext().lookup( name );
	}

	/**
	 * @param name
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3BEAED0B004E
	 */
	public static Object lookupContextUsingBundle(String propsFile, String name) throws NamingException
	{
		InitialContext context = getContextUsingBundle( propsFile );
		return lookupContext( context, name );
	}

	/**
	 * @param name
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3BEAED0B004E
	 */
	public static Object lookupLocalContext(String name) throws NamingException
	{
		InitialContext context = new InitialContext();
		return lookupContext( context, name );
	}
	
	/**
	 * @param name
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3BEAED0B004E
	 */
	protected static Object lookupContext(InitialContext context, String name) throws NamingException
	{
	   	if (name.startsWith( AVConstants._ENC_PREFIX )) 
			return context.lookup( name );
		return context.lookup( AVConstants._ENC_PREFIX + name );
	}
	
	

	/**
	 * @param subSystem
	 * @param jndiName
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F748800DD
	 */
	public static Object lookupJndiName(String subSystem, String jndiName) throws NamingException, ClassCastException, ClassNotFoundException
	{
		return ((EJBEnvironment)Environment.getInstance( subSystem )).lookupSubsystemJndiName( jndiName );
	}

	/**
	 * @param jndiName
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F64FF0193
	 */
	public Object lookupSubsystemEJBInterface(String jndiName, Class localClass) throws NamingException, ClassCastException, ClassNotFoundException
	{
		String prefix = "";
		if (_jndiCnfgFile == null)
			prefix = AVConstants._EJB_ENC_PREFIX;
		return PortableRemoteObject.narrow( lookupSubsystemJndiName( prefix + jndiName ), localClass );
	}

	/**
	 * @param subSystem
	 * @param jndiName
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F3BCD00FA
	 */
	public static Object lookupEJBInterface(String subSystem, String jndiName, Class localClass) throws NamingException, ClassCastException, ClassNotFoundException
	{
		String jndiName2 = ((EJBEnvironment)Environment.getInstance( subSystem )).prefixJNDIName(jndiName);
        //try the qualified name based on the subsystem first. In WebSphere, there seems to be an
        //issue with java:comp/env/ejb namespace sometimes within a transaction
        //so if the qualified namespace search fails we'll look for unqualified name- i.e., without the namespace
        //This issue may be eventually traced to our own entity bean implementation.
        //At that time, we'll no longer need the additional try
        //As the additional lookup is only tried when the initial name lookup (whether it includes namespace or not) fails,
        //the cost is minimal for most cases.
        try{
            return com.addval.servicelocator.ServiceLocator.getInstance(subSystem).getHome(jndiName2, localClass);
        }
        catch(com.addval.servicelocator.ServiceLocatorException sle){
            if(!jndiName2.equals(jndiName) && jndiName2.startsWith(AVConstants._EJB_ENC_PREFIX) && !jndiName.startsWith(AVConstants._EJB_ENC_PREFIX)){
                return com.addval.servicelocator.ServiceLocator.getInstance(subSystem).getHome(jndiName, localClass);
            }
            else throw sle;
        }
	}

	private String prefixJNDIName(String jndiName)
	{
		if (_jndiCnfgFile == null)return AVConstants._EJB_ENC_PREFIX + jndiName;
		return jndiName;
	}

	/**
	 * @param jndiName
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F3BCD00FA
	 */
	public static Object lookupLocalContextEJBInterface(String jndiName, Class localClass) throws NamingException, ClassCastException, ClassNotFoundException
	{
	   	if ( jndiName.startsWith( AVConstants._ENC_PREFIX ) ) {
			return lookupLocalContextEJBInterfaceWithPrefix( jndiName, "", localClass);
		}
		else {
			return lookupLocalContextEJBInterfaceWithPrefix( jndiName, AVConstants._EJB_ENC_PREFIX, localClass);
		}
	}

	/**
	 * @param jndiName
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F3BCD00FA
	 */
	public static Object lookupLocalContextEJBInterfaceWithPrefix(String jndiName, String jndiPrefix, Class localClass) throws NamingException, ClassCastException, ClassNotFoundException
	{
		InitialContext context = new InitialContext();
		return PortableRemoteObject.narrow( context.lookup( jndiPrefix + jndiName ), localClass);
	}

	/**
	 * @param jndiName
	 * @param jndiPrefix
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F64FF01BB
	 */
	public Object lookupSubsystemJndiName(String jndiName, Class localClass) throws NamingException, ClassCastException, ClassNotFoundException
	{
		return PortableRemoteObject.narrow( lookupSubsystemJndiName( jndiName ), localClass );
	}

	/**
	 * @param subSystem
	 * @param jndiName
	 * @param jndiPrefix
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @throws java.lang.ClassCastException
	 * @throws java.lang.ClassNotFoundException
	 * @roseuid 3F1F60AF02B8
	 */
	public static Object lookupJndiName(String subSystem, String jndiName, Class localClass) throws NamingException, ClassCastException, ClassNotFoundException
	{
		return ((EJBEnvironment)Environment.getInstance( subSystem )).lookupSubsystemJndiName( jndiName, localClass );
	}

	/**
	 * @roseuid 3F198111036A
	 */
	protected void initialize()
	{
		super.initialize();
		String jndiPropertiesFile = getCnfgFileMgr().getPropertyValue( _JNDI_RESOURCE_BUNDLE_NAME, null );
		if ( jndiPropertiesFile != null ) {
			_jndiResourceBundleName = jndiPropertiesFile;
			_jndiCnfgFile = new CnfgFileMgr( jndiPropertiesFile );
			if ( _jndiCnfgFile != null && ( _jndiCnfgFile.getProperties() == null || _jndiCnfgFile.getProperties().size() == 0) )
				_jndiCnfgFile = null;
		}
	}






	/**
	 * @param props
	 * @return javax.naming.InitialContext
	 * @throws javax.naming.NamingException
	 * @roseuid 3BF2D19503E2
	 */
	public static InitialContext getContext(Properties props) throws NamingException
	{
		return new InitialContext( props );
	}

	/**
	 * @param props
	 * @param name
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3BF2D6B200CD
	 */
	public static Object lookup(Properties props, String name) throws NamingException
	{
	   	//if ( name.startsWith( AVConstants._ENC_PREFIX ) ) {
			return getContext( props ).lookup( name );
		//}
		//else {
		//	return getContext( props ).lookup( AVConstants._ENC_PREFIX + name );
		//}
	}

	/**
	 * @param propsFile
	 * @param name
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3BF2D6D20033
	 */
	public static Object lookupUsingBundle(String propsFile, String name) throws NamingException
	{
	   	//if ( name.startsWith( AVConstants._ENC_PREFIX ) ) {
			return getContext( propsFile ).lookup( name );
		//}
		//else {
		//	return getContext( propsFile ).lookup( AVConstants._ENC_PREFIX + name );
		//}
	}

	/**
	 * @param props
	 * @param name
	 * @param localClass
	 * @return java.lang.Object
	 * @throws javax.naming.NamingException
	 * @roseuid 3F1F88610288
	 */
	public static Object lookup(Properties props, String name, Class localClass) throws NamingException
	{
		return PortableRemoteObject.narrow( lookup( props, name), localClass );
	}
}
