
package com.addval.metadata;

import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import java.util.Vector;
import java.util.Iterator;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Hashtable;
import com.addval.utils.XRuntime;
import com.addval.ejbutils.dbutils.EJBCriteria;
import javax.naming.NamingException;
import java.rmi.RemoteException;

public class UpdateUserCriteriaMgr
{
	private String _subsystem = null;
	private static final String _EDITOR_NAME = "UPDATE_USER_CRITERIA";
	private static final String _EDITOR_TYPE = "DEFAULT";
	private EditorMetaData _metadata = null;

	public UpdateUserCriteriaMgr(String subsystem) throws Exception
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
//		Object object = EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName ,EJBSEditorMetaDataHome.class );
		EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName ,EJBSEditorMetaDataHome.class );
		EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );
		return  metaDataRemote.lookup( _EDITOR_NAME, _EDITOR_TYPE );
	}

	public void insert(UpdateUserCriteria criteria) throws XRuntime
	{
		try {
			EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( _metadata ) );
			ejbRS.insertRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("DIRECTORY_NAME", criteria.getDirectoryName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			ejbRS.updateString("CRITERIA_DESC", criteria.getCriteriaDesc());
			ejbRS.updateString("FILTER", criteria.getFilter() );
//			ejbRS.updateString("DEFAULT_FILTER", criteria.getDefaultFilter());
			ejbRS.updateString("UPDATE_VALUE", criteria.getUpdateValue());

			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( getSubsystem() );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );
			if (ejbRS == null)
				throw new XRuntime( "UpdateUserCriteriaMgr.insert()", "Insert Fails");
		}
		catch(Exception ex) {
			throw new XRuntime( "UpdateUserCriteriaMgr.insert()", ex.getMessage());
		}
	}
	public void update(UpdateUserCriteria criteria) throws XRuntime
	{
		try {
			EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( _metadata ) );
			ejbRS.updateRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("DIRECTORY_NAME", criteria.getDirectoryName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			ejbRS.updateString("CRITERIA_DESC", criteria.getCriteriaDesc());
			ejbRS.updateString("FILTER", criteria.getFilter() );
//			ejbRS.updateString("DEFAULT_FILTER", criteria.getDefaultFilter());
			ejbRS.updateString("UPDATE_VALUE", criteria.getUpdateValue());

			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( getSubsystem() );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );
		}
		catch(Exception ex) {
			throw new XRuntime( "UpdateUserCriteriaMgr.update()", ex.getMessage());
		}
	}
	public void delete(UpdateUserCriteria criteria) throws XRuntime
	{
		try {
			EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( _metadata ) );
			ejbRS.deleteRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("DIRECTORY_NAME", criteria.getDirectoryName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( getSubsystem() );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );
		}
		catch(Exception ex) {
			throw new XRuntime( "UpdateUserCriteriaMgr.delete()", ex.getMessage());
		}
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
