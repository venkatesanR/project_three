package com.addval.metadata;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;

import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.rmi.RemoteException;

public class DirectoryMgr
{
	private String _subsystem = null;
	private static final String _EDITOR_NAME = "DIRECTORY_INFO";
	private static final String _EDITOR_TYPE = "DEFAULT";
	private EditorMetaData _metadata = null;

	public DirectoryMgr(String subsystem) throws Exception
	{
		try {
			setSubsystem(subsystem);
			_metadata = getEditorMetaData();
		}
		catch(Exception ex) {
			throw ex;
		}
	}

	public EJBResultSet lookup(Hashtable param) throws Exception
	{
		EJBResultSet ejbRS = null;
		EJBCriteria ejbCriteria = null;
		try {
			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(getSubsystem());
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbCriteria = getEJBCriteriaForLookup( _metadata , param );
			ejbRS = tableManagerRemote.lookup( ejbCriteria );
			return ejbRS;
		}
		catch(Exception ex) {
			throw ex;
		}
	}

	public String getSubsystem()
	{
		return _subsystem;
	}

	public void setSubsystem(String aSubsystem)
	{
		_subsystem = aSubsystem;
	}

	private EditorMetaData getEditorMetaData() throws Exception
	{
		String homeName = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName","");
		Object object = EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName ,EJBSEditorMetaDataHome.class );
		EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName ,EJBSEditorMetaDataHome.class );
		EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );
		return  metaDataRemote.lookup( _EDITOR_NAME, _EDITOR_TYPE );
	}

	private EJBCriteria getEJBCriteriaForLookup(EditorMetaData metadata, Hashtable param)
	{
    	EJBCriteria ejbCriteria = ( new EJBResultSetMetaData( metadata ) ).getNewCriteria();
		ejbCriteria.where ( param, null );
	    Vector orderby = new Vector( );
	    for ( Iterator iter = metadata.getDisplayableColumns().iterator( ); iter.hasNext( ); ) {
	        ColumnMetaData colInfo = ( ColumnMetaData ) iter.next( );
	        if ( colInfo.getType() != ColumnDataType._CDT_LINK )
	    		orderby.add ( colInfo.getName() );
	    }
        ejbCriteria.orderBy ( orderby );
        return ejbCriteria;
	}

	private EJBSTableManagerHome getTableManagerHome(String subsystem) throws NamingException, RemoteException, Exception
	{
		String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
        try {
			EJBSTableManagerHome   tableManagerHome   = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSTableManagerHome.class );
			return tableManagerHome;
		}
		catch (javax.naming.NamingException ex) {
			throw ex;
		}
		catch (java.lang.Exception ex) {
			throw ex;
       	}
	}
}
