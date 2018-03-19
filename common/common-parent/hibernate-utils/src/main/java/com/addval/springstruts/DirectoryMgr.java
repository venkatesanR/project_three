package com.addval.springstruts;

import com.addval.metadata.*;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.ejbutils.server.EJBSEditorMetaData;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.rmi.RemoteException;

public class DirectoryMgr
{
	private static final String _EDITOR_NAME = "DIRECTORY_INFO";
	private static final String _EDITOR_TYPE = "DEFAULT";
	private EditorMetaData _metadata = null;

	 private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(DirectoryMgr.class);

	private EJBSTableManager _tableManager = null;
	private EJBSEditorMetaData _editorMetadataServer = null;

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


	public DirectoryMgr()
	{
	}

	public EJBResultSet lookup(Hashtable param) throws Exception
	{
		EJBResultSet ejbRS = null;
		EJBCriteria ejbCriteria = null;
		try {

			if (_metadata == null) {
				try {
					_metadata = getEditorMetadataServer().lookup( _EDITOR_NAME, _EDITOR_TYPE );
				}
				catch(Exception ex) {
					_logger.error(ex);
					throw ex;
				}
			}

			ejbCriteria = getEJBCriteriaForLookup( _metadata , param );
			ejbRS = getTableManager().lookup( ejbCriteria );
			return ejbRS;
		}
		catch(Exception ex) {
			throw ex;
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

}
