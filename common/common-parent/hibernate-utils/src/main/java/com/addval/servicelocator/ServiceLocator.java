/*
 * ServiceLocator.java
 *
 * Created on August 29, 2003, 3:03 PM
 */

package com.addval.servicelocator;

import java.util.*;
import javax.naming.*;
import java.rmi.RemoteException;
import javax.ejb.*;
import javax.rmi.PortableRemoteObject;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.ref.SoftReference;

import com.addval.utils.Pair;
import com.addval.utils.LogFileMgr;
import com.addval.utils.AVConstants;

/**
 *
 * @author  ravi
 */
public class ServiceLocator 
{
	private static final String _CLS_NAME = "ServiceLocator";
	
	private String _subsystem;
	
	private InitialContext _ctx;
	
	private LogFileMgr _logFileMgr;
	
	/**
	 * If false the homes are not cached for example if deployed in WebSphere 5.1 or j2ee 1.3
	 * we don't want cache homes
	 */
	private static boolean _useAVHomeCache = true;
	
	public static String _ENV_CFG_VAR_USE_AV_HOME_CACHE = "UseAVEJBHomeCache";
	
	/**
	 * key - subsystem name
	 * value - ServiceLocator object
	 */
	private static Map _instances = new HashMap();
	
	/**
	 * key - JNDI name
	 * value - EJBHome (A soft reference)
	 */
	private Map _cachedEJBHomes;
	
	static{
		//get flag about whether to use home cache or not from java env
		String val = System.getProperty(_ENV_CFG_VAR_USE_AV_HOME_CACHE);
		if(val != null)_useAVHomeCache = Boolean.valueOf(val).booleanValue();
		System.out.println("AddVal EJB Home cache is configured to = " + _useAVHomeCache);
	}
	
	/** Creates a new instance of ServiceLocator */
	private ServiceLocator(String subsystem)
	{
		try{			
			_subsystem = subsystem;
			_logFileMgr = com.addval.environment.Environment.getInstance(_subsystem).getLogFileMgr(getClass().getName());
			_ctx = com.addval.environment.EJBEnvironment.getContext(_subsystem);		
			_cachedEJBHomes = new HashMap();
		} 
		catch(NamingException ne){
			throw new ServiceLocatorException(ne);
		}
	}
	
	public static ServiceLocator getInstance(String subsystem)
	{
		ServiceLocator srvLoc = (ServiceLocator) _instances.get(subsystem);
		
		if(srvLoc == null){
			synchronized(_instances){
				srvLoc = (ServiceLocator) _instances.get(subsystem);
				if(srvLoc != null)return srvLoc;
				srvLoc = new ServiceLocator(subsystem);
				_instances.put(subsystem, srvLoc);
			} //end sync
		}
		
		return srvLoc;
	}
	
	/**
	 * Returns the EJBHome object for requested service 
	 * name. Throws ServiceLocatorException If Any Error 
	 * occurs in lookup
	 */
	public EJBHome getHome(String jndiname, Class clazz) 
	{	
		if(!_useAVHomeCache)return lookupHome(jndiname, clazz);
			
		//pair ctr will check for null pointers
		Pair key = new Pair(jndiname, clazz.getName());
		
		//check cache for availability
		SoftReference ref = (SoftReference)this._cachedEJBHomes.get(key);
		EJBHome home = null;
		if(ref != null)home = (EJBHome)ref.get();
		
		if(home == null){	
			synchronized(_cachedEJBHomes){				
				ref = (SoftReference)this._cachedEJBHomes.get(key);
				if(ref != null)home = (EJBHome)ref.get();
				if(home != null)return home;
				_cachedEJBHomes.remove(key);
				home = lookupHome(jndiname, clazz);
				if(home == null){
					throw new ServiceLocatorException("Unknown error while trying to create ejb home for " 
						+ jndiname + ", " + clazz.getName());
				}
				this._cachedEJBHomes.put(key, new SoftReference(home));
			} //end sync
		}
		
		return home;
		
	}
		
	private EJBHome lookupHome(String jndiname, Class clazz)
	{
		if(_logFileMgr.debugOn())_logFileMgr.logTrace("Asking JNDI name - " + jndiname);
	
        try{
            Object objref = _ctx.lookup(jndiname);
            EJBHome home = (EJBHome) PortableRemoteObject.narrow(objref, clazz);
            if(_logFileMgr.debugOn())_logFileMgr.logTrace("Found for JNDI name - " + jndiname);
            return home;		
        }
        catch(NamingException ex){
            throw new ServiceLocatorException(ex);
        }
	}
	
	private synchronized void clearEJBHomeCache()
	{		
		_logFileMgr.logInfo("Clearing ejb home cache for subsystem " + _subsystem);
		this._cachedEJBHomes.clear();
		try{
			_ctx = com.addval.environment.EJBEnvironment.getContext(_subsystem);
		}
		catch(NamingException ne){
			throw new ServiceLocatorException(ne);
		}
	}
	
	public void printCache()
	{
		System.out.println(this._cachedEJBHomes);
	}
}	

