//Source file: E:\\Projects\\Common\\src\\Client\\source\\com\\addval\\environment\\EJBServerEnvironment.java

package com.addval.environment;

import com.addval.utils.XRuntime;
import com.addval.dbutils.DBPoolMgr;

public class EJBServerEnvironment extends EJBEnvironment 
{
   
   /**
    * @roseuid 3BEC791D033C
    */
   protected EJBServerEnvironment() 
   {
    
   }
   
   /**
    * @param config
    * @roseuid 3B6203220132
    */
   protected EJBServerEnvironment(String config) 
   {
		super( config );    
   }
   
   /**
    * @param context
    * @param configFile
    * @roseuid 3B60C00A035F
    */
   public static void make(String context, String configFile) 
   {
		try {
			setInstance( context , new EJBServerEnvironment( configFile ) );
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new XRuntime( "JdbcEnvironment.make()", e.getMessage() );
		}    
   }
}
