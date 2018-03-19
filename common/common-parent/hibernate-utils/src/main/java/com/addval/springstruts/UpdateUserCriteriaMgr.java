
package com.addval.springstruts;

import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.environment.EJBEnvironment;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import java.util.Vector;
import java.util.Iterator;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Hashtable;
import com.addval.utils.XRuntime;
import com.addval.ejbutils.dbutils.EJBCriteria;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.ColumnDataType;

import com.addval.metadata.UpdateUserCriteria;
import com.addval.metadata.EditorMetaData;

public class UpdateUserCriteriaMgr
{
	private static transient final String _EDITOR_NAME = "UPDATE_USER_CRITERIA";
	private static transient final String _EDITOR_TYPE = "DEFAULT";
	private EditorMetaData _metadata = null;

	 private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(DirectoryMgr.class);

	private EJBSTableManager _tableManager = null;
	private EJBSEditorMetaData _editorMetadataServer = null;

	public UpdateUserCriteriaMgr()
	{

	}

	/*
	 * get method for TableManager
	 */
	public EJBSTableManager getTableManager() {
		return _tableManager;
	}

	/*
	 * set method for TableManager
	 */
	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}

	public void setEditorMetadataServer(EJBSEditorMetaData editorMetadataServer) {
		_editorMetadataServer = editorMetadataServer;

	}

	public EJBSEditorMetaData getEditorMetadataServer() {
		return _editorMetadataServer;
	}


	public EJBResultSet lookup(Hashtable param) throws Exception
	{
		EJBCriteria ejbCriteria = getEJBCriteriaForLookup( getEditorMetaData(), param );
		return getTableManager().lookup( ejbCriteria );
	}


	public void insert(UpdateUserCriteria criteria)
	{
		try {
			EJBResultSet ejbRS = getEJBResultSet();
			ejbRS.insertRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("DIRECTORY_NAME", criteria.getDirectoryName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			ejbRS.updateString("CRITERIA_DESC", criteria.getCriteriaDesc());
			ejbRS.updateString("FILTER", criteria.getFilter() );
//			ejbRS.updateString("DEFAULT_FILTER", criteria.getDefaultFilter());
			ejbRS.updateString("UPDATE_VALUE", criteria.getUpdateValue());

			ejbRS = getTableManager().updateTransaction ( ejbRS );
			if (ejbRS == null)
				throw new XRuntime( "UpdateUserCriteriaMgr.insert()", "Insert Fails");
		}
		catch(Exception ex) {
			throw new XRuntime( "UpdateUserCriteriaMgr.insert()", ex.getMessage());
		}
	}
	public void update(UpdateUserCriteria criteria)
	{
		try {
			EJBResultSet ejbRS = getEJBResultSet();
			ejbRS.updateRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("DIRECTORY_NAME", criteria.getDirectoryName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			ejbRS.updateString("CRITERIA_DESC", criteria.getCriteriaDesc());
			ejbRS.updateString("FILTER", criteria.getFilter() );
//			ejbRS.updateString("DEFAULT_FILTER", criteria.getDefaultFilter());
			ejbRS.updateString("UPDATE_VALUE", criteria.getUpdateValue());

			getTableManager().updateTransaction ( ejbRS );
		}
		catch(Exception ex) {
			throw new XRuntime( "UpdateUserCriteriaMgr.update()", ex.getMessage());
		}
	}
	public void delete(UpdateUserCriteria criteria)
	{
		try {
			EJBResultSet ejbRS = getEJBResultSet();
			ejbRS.deleteRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("DIRECTORY_NAME", criteria.getDirectoryName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			getTableManager().updateTransaction ( ejbRS );
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


	private EditorMetaData getEditorMetaData() throws Exception
	{
		if (_metadata == null)
			_metadata = getEditorMetadataServer().lookup( _EDITOR_NAME, _EDITOR_TYPE );
		return _metadata;
	}

	private EJBResultSet getEJBResultSet() throws Exception
	{
		return new EJBResultSet( new EJBResultSetMetaData( getEditorMetaData() ) );
	}
}
