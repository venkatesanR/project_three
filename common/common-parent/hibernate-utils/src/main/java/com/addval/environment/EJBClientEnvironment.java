//Source file: C:\\Projects\\Common\\src\\client\\source\\com\\addval\\environment\\EJBClientEnvironment.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.environment;


public class EJBClientEnvironment extends EJBEnvironment {
	
	/**
	 * @roseuid 3BEC78FB0225
	 */
	protected EJBClientEnvironment() {
		
	}
	
	/**
	 * @param config
	 * @roseuid 3B62030601DC
	 */
	protected EJBClientEnvironment(String config) {

		super( config );
		_cache = EJBUIMetaData.getInstance( getCnfgFileMgr() );		
	}
	
	/**
	 * @param context
	 * @param configFile
	 * @roseuid 3B60BFA9035F
	 */
	public static void make(String context, String configFile) {
		setInstance( context , new EJBClientEnvironment( config ) );		
	}
}
/**
 * 
 *  
 * EJBClientEnvironment.getHomeInterface(){
 * 		return null;
 * 	}
 *  
 *  
 * EJBClientEnvironment.getContext(){
 * 		return null;
 * 	}
 *  
 *  
 */
