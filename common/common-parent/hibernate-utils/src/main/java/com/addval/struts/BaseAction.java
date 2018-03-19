//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\BaseAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\BaseAction.java

package com.addval.struts;

import org.apache.struts.action.Action;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;
import java.rmi.RemoteException;

/**
 * Base Class - For all actions. Derive from this instead of Action class. @author
 * AddVal Technology Inc.
 */
public class BaseAction extends Action
{
	public static final String _DONE_FORWARD = "done";
	public static final String _FAILED_FORWARD = "failed";
	public static final String _ERROR_FORWARD = "error";
	public static final String _CANCEL_FORWARD = "cancel";
	public static final String _SHOWLIST_FORWARD = "showList";
	public static final String _SHOWEDIT_FORWARD = "showEdit";
	public static final String _INSERT_FORWARD = "insert";
	public static final String _EDIT_FORWARD = "edit";
	public static final String _DELETE_FORWARD = "delete";
	public static final String _CLONE_FORWARD = "clone";
	public static final String _OPEN_FORWARD = "open";
	public static final String _SEARCH_FORWARD = "search";

	/**
	 * override this method in the derived class to build actions
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DCC746700E4
	 */
	public org.apache.struts.action.ActionForward execute(org.apache.struts.action.ActionMapping arg0, org.apache.struts.action.ActionForm arg1, javax.servlet.http.HttpServletRequest arg2, javax.servlet.http.HttpServletResponse arg3) throws Exception
	{
		return null;
	}

	/**
	 * Return the tablemanager home configured in the <subsystem>.properties file.
	 * This should configured in the property file as shown below
	 * editorMetaData.EJBSTableManagerBeanName=EJBSTableManager
	 * This method will then lookup the JNDI name in
	 * java:comp/env/ejb/EJBSTableManager from the context
	 * The purpose of this method is to allow configuring TableManagerBeans for each
	 * subsystem
	 * @param subsystem
	 * @return EJBSTableManagerHome
	 * @throws javax.naming.NamingException
	 * @throws java.rmi.RemoteException
	 * @throws java.lang.Exception
	 * @roseuid 3DCC95560114
	 */
	public EJBSTableManagerHome getTableManagerHome(String subsystem) throws NamingException, RemoteException, Exception
	{

		String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");

        try {

			EJBSTableManagerHome   tableManagerHome   = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSTableManagerHome.class );

			return tableManagerHome;
		}
		catch (javax.naming.NamingException ex)
		{
			throw ex;
		}
		catch (java.lang.Exception ex)
		{
			throw ex;
       	}
	}

	/**
	 * Returns the subsystem from the controller configuration in struts-config.xml.
	 * This is the default subsystem that will be used unless it is overridden in the
	 * FormBeans tags. See BaseControllerConfig for more details on configuring the
	 * subsystem in struts-config.xml
	 * @param req
	 * @return String
	 * @roseuid 3DCF0D720048
	 */
	public String getSubsystem(HttpServletRequest req)
	{
		       org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) req.getAttribute(org.apache.struts.Globals.MODULE_KEY);

				BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();

				return baseConfig.getSubsystem();
	}

	/**
	 * Returns the database servertype for the subsystem. This is retrieved from the
	 * DBPoolMgr for this subsystem. Please see DBPoolMgr.getServerType() for more
	 * information. The return values can be "SQLSERVER", "ORACLE" or "MSAccess" or
	 * any value configured in <subsystem>.properties file
	 * db.<poolname>.serverType
	 * @param req
	 * @return String
	 * @roseuid 3DCF42B500F8
	 */
	public String getServertype(HttpServletRequest req)
	{
		String subsystem 		= getSubsystem(req);

		// Cannot use DBPoolMgr because it will fail. For now the serverType will be returned from db.serverType

		String serverType = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("db.serverType", "");

		return serverType;
	}
}
