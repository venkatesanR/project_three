/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */
package com.addval.ejbutils.server;

import javax.ejb.SessionBean;

import com.addval.ejbutils.utils.EJBXFeatureNotImplemented;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.ejbutils.utils.TableManagerInterceptor;
import com.addval.ejbutils.utils.TableManagerInterceptorFactory;
import com.addval.metadata.ColumnDataType;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.addval.metadata.EditorMetaData;

import java.sql.SQLException;

import com.addval.metadata.RecordStatus;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.addval.ejbutils.dbutils.EJBDateTime;

import java.util.Iterator;

import com.addval.utils.AVConstants;
import com.addval.utils.ListUtl;

import javax.naming.NamingException;

import java.sql.CallableStatement;

import com.addval.ejbutils.dbutils.EJBRecord;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.ejb.CreateException;

import org.apache.log4j.Logger;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBColumn;
import com.addval.metadata.ColumnMetaData;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.dbutils.DBUtl;

import java.sql.ResultSetMetaData;

/**
 * A session bean implementation that enables CRUD functions on a table/view that
 * is represented by EditorMetaData. The bean returns EJBResultSet objects for
 * lookup and update functions
 */
public class EJBSTableManagerBean implements SessionBean
{
	private static final long serialVersionUID = -7134431690139896612L;
	private SessionContext _context;
	private static final String _module = "com.addval.ejbutils.server.EJBSTableManagerBean";
	private String _subSystem = null;
	private String _serverType = "";
	private int _MAX_RECORDS = 500;
	private Logger _logger  = com.addval.utils.LogMgr.getLogger(_module);

	/**
	 * Default constructor
	 * 
	 * @roseuid 3B69708B0015
	 */
	public EJBSTableManagerBean()
	{

	}

	/**
	 * @throws java.rmi.RemoteException
	 * @throws javax.ejb.CreateException
	 * @roseuid 3B0944550377
	 */
	public void ejbCreate() throws RemoteException, CreateException
	{

	}

	/**
	 * @param context
	 * @roseuid 3B0944AE01D0
	 */
	public void setSessionContext(SessionContext context)
	{

        _context = context;
        try{
            _subSystem  = (String)EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );
            _serverType = Environment.getInstance( _subSystem ).getDbPoolMgr().getServerType();
            _MAX_RECORDS = (int)Environment.getInstance( _subSystem ).getCnfgFileMgr().getLongValue("db." + Environment.getInstance( _subSystem ).getDbPoolMgr().getName() + ".MaxReadCount",500);
        }
        catch(NamingException nex){
            nex.printStackTrace();
            throw new EJBException( nex );
        }
	}

	/**
	 * @throws java.rmi.RemoteException
	 * @roseuid 3B094456018E
	 */
	public void unsetSessionContext() throws RemoteException
	{
        _context = null;
	}

	/**
	 * @throws java.rmi.RemoteException
	 * @roseuid 3B094456024C
	 */
	public void ejbActivate() throws RemoteException
	{

	}

	/**
	 * @throws java.rmi.RemoteException
	 * @roseuid 3B0944560274
	 */
	public void ejbPassivate() throws RemoteException
	{

	}

	/**
	 * @throws java.rmi.RemoteException
	 * @roseuid 3B0944AE025C
	 */
	public void ejbRemove() throws RemoteException
	{

	}


	/**
	 * Given a criteria, this method will retrieve the data into an EJBResultset (performs a 'select')
	 * Can also be used to lock a table using the 'for update of' clause
	 * 
	 * @throws java.rmi.RemoteException
	 * @roseuid 3B0944AE025C
	 */
	private EJBResultSet lookup(EJBCriteria criteria ,boolean lock) {
        Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try {

            // If a row does not exist for the editor type then the editor is not editable
            // But the metadata cache should have an element for the editor name, editor type.\
            // The edit, delete, add, custom icons will be placed in the UI based on whether
            // the link exists as a column in ColumnInfo and Editor_Columns table
            final EJBSQLBuilderUtils	sqlBuilder  = new EJBSQLBuilderUtils( _serverType );
            final EJBResultSetMetaData  metaData    = getMetaData( criteria.getEditorName(), criteria.getEditorType() );

            String sql = sqlBuilder.buildLookupSql( metaData, criteria );

            // add additional sql if the it needs to be locked
            if(lock) {

				Vector keyColumns = metaData.getEditorMetaData().getKeyColumns();
				if(keyColumns != null) {
					Iterator iterator =  keyColumns.iterator();
					if(iterator.hasNext()) {
						ColumnMetaData 	columnMetaData = (ColumnMetaData)iterator.next();
						sql += " for update of " + columnMetaData.getName();
					}
				}
			}

            EJBResultSet result  = new EJBResultSet( metaData, criteria );

            // Execute the SQL Query
            conn = getConnection();
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs   = stmt.executeQuery( sql );

            // Populate serializable EJBResultSet from SQL ResultSet
            populateData( rs, result );

            return result;
        }
        catch (Exception e) {

            Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
            throw new EJBException( e );
        }
        finally {

            try {
                if (rs != null  ) rs.close();
                if (stmt != null) stmt.close();
                releaseConnection( conn );
            }
            catch (SQLException se) {

                Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
                throw new EJBException( se );
            }
        }
	}


	/**
	 * Lookup the database and return a resultset based on the EJBCriteria that is
	 * provided. Forces the record to be locked
	 *
	 * @param criteria
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3B09445503D1
	 */
	public EJBResultSet lookupForUpdate(EJBCriteria criteria)
	{
		return lookup(criteria,true);
	}


	/**
	 * Lookup the database and return a resultset based on the EJBCriteria that is
	 * provided. This will get a read-only copy - no lock is placed on the table/row
	 *
	 * @param criteria
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3B09445503D1
	 */
	public EJBResultSet lookup(EJBCriteria criteria)
	{
		return lookup(criteria,false);
	}

	/** 
	 * Utility method to get a resultset from a pre-built SQL.
	 *
	 * Currently used by the export function to set max records to a high number
	 * 
	 */
	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData, EJBCriteria criteria)
	{
		Connection conn = null;
		Statement  stmt = null;
		ResultSet  rs   = null;

		try {
			conn = this.getConnection();
			stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
			rs = stmt.executeQuery(sql);

			//make local copy of the the editor meta data to modify
			EditorMetaData localMetaData = (EditorMetaData) editorMetaData.clone();

			//from the sql metadata prune localmetadata
			ResultSetMetaData sqlMetaData = rs.getMetaData();

			int sqlColCount = sqlMetaData.getColumnCount();
			String columnName = null;
			Vector columnList  = new Vector(sqlColCount);
			ColumnMetaData colMetaData;

			for(int i = 1; i <= sqlColCount; i++){
				columnName = sqlMetaData.getColumnName(i);
				if((colMetaData = localMetaData.getColumnMetaData(columnName)) != null){
					columnList.add(colMetaData);
				}
			}
			localMetaData.setColumnsMetaData(columnList);
			EJBCriteria ejbCrit = null;
			if (criteria != null) {

				ejbCrit = criteria;
			}
			else {

				ejbCrit = new EJBCriteria(localMetaData.getName(), localMetaData.getType(),
					localMetaData.getColumnsMetaData());
			}

			EJBResultSet result  = new EJBResultSet(new EJBResultSetMetaData(localMetaData), ejbCrit);
			populateData( rs, result );

			return result;
		}
		catch(Exception e){
			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
			throw new EJBException( e );
		}
		finally{
			DBUtl.closeFinally(rs, stmt, _logger);
			this.releaseConnection(conn);
		}
	}

	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData)
	{
		return lookup( sql, editorMetaData, null );
	}

	/**
	 * Updates the record set based on the status of each record in it
	 * (insert/update/delete). Records that fail updates have their status set to
	 * _RSS_ERROR
	 * @param rs
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B0944560057
	 */
	public EJBResultSet update(EJBResultSet rs) throws EJBXRuntime
	{
        Connection conn = null;

        try {

            EditorMetaData editorMetaData = rs.getEJBResultSetMetaData().getEditorMetaData();

            if (!editorMetaData.isEditable())
                throw new EJBXRuntime( "Editor is not editable :" + editorMetaData.getName() );

            if (rs.getMetaData() == null)
                throw new EJBXRuntime( _module, "MetaData is not set for the resultset to be updated" );

            rs.beforeFirst();

            conn    = getConnection();
            int rv  = 0;

            // check if there is any interceptor defined for this editor
            TableManagerInterceptor tblMgrInterceptor = null;
            if(editorMetaData.getInterceptor() != null) {
                tblMgrInterceptor = TableManagerInterceptorFactory.getInstance(editorMetaData.getInterceptor());
            }

            // perform any before-update operations
            if(tblMgrInterceptor != null) {
                tblMgrInterceptor.beforeUpdate(rs);
            }

            while( rs.next() ) {
                try {

                    switch (rs.getRecord().getStatus()) {

                        case RecordStatus._RMS_INSERTED :
                            rv = insert( rs, conn );
                            break;

                        case RecordStatus._RMS_UPDATED :
                            rv = update( rs, conn );
                            break;

                        case RecordStatus._RMS_DELETED :
                            rv = delete( rs, conn );
                            break;
                        default :
                            throw new EJBXRuntime( _module, "Record Status is not recognized:" + rs.getRecord().getStatus() );
                    }

                    if (rv == 1)
                        rs.getRecord().setSyncStatus( RecordStatus._RSS_SYNC  );
                    else
                        rs.getRecord().setSyncStatus( RecordStatus._RSS_ERROR );
                }
                catch (SQLException se) {

                    Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );

					String errorMessage = Environment.getInstance( _subSystem ).getDbPoolMgr().getSQLExceptionTranslator().translate( se );

                    // Catch only SQL Exceptions and set the sync status to error
                    rs.getRecord().setSyncStatus( RecordStatus._RSS_ERROR );

					if (errorMessage != null)
						rs.getRecord().setErrorMsg( errorMessage );
					else
						rs.getRecord().setErrorMsg( se.getErrorCode() + ":" + se.getMessage() );
                }
            }

            // perform any before-update operations
            if(tblMgrInterceptor != null) {
                tblMgrInterceptor.afterUpdate(rs);
            }
            return rs;
        }
        catch (Exception e) {

            Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
            throw new EJBException( e );
        }
        finally {

			releaseConnection( conn );
        }
	}
	public boolean updateTransaction(EJBResultSet masterSet, List<EJBResultSet> deleteSet, List<EJBResultSet> insertSet) throws EJBXRuntime, RemoteException
	{

		Connection conn = null;
		boolean updateStatus = true;
		String module = getClass().getName();
		EditorMetaData editorMetaData = null;
		try {
			editorMetaData = masterSet.getEJBResultSetMetaData().getEditorMetaData();
			conn = getConnection();
			// First deal with the parent table info - delete or insert or  update
			
			EJBResultSet masterRS = updateTransaction(masterSet);

			//Parent-Child hasAutoSequenceName() check.
            EJBResultSetMetaData metaData = masterRS.getEJBResultSetMetaData();
            EJBColumn parentColumn = null;
            ColumnMetaData columnMetaData = null;
            int size = metaData.getColumnCount();
            for (int index = 1; index <= size; index++) {
            	columnMetaData = metaData.getColumnMetaData(index);
            	if(columnMetaData.hasAutoSequenceName()){
            		parentColumn = masterRS.getRecord().getColumn(columnMetaData.getName());
            		/*
            		if(!ListUtl.isEmpty(deleteSet)){
            			for (EJBResultSet deleteRS : deleteSet) {
            				deleteRS.beforeFirst();
            				while (deleteRS.next()) {
            					deleteRS.getRecord().getColumn(parentColumn.getName()).setIntValue(parentColumn.getIntValue());
            				}
            			}
            		}
            		*/
            		if(!ListUtl.isEmpty(insertSet)){
            			for (EJBResultSet insertRS : insertSet) {
            				insertRS.beforeFirst();
            				while (insertRS.next()) {
            					insertRS.getRecord().getColumn(parentColumn.getName()).setIntValue(parentColumn.getIntValue());
            				}
            			}
            		}            
            	}
            }
            
			// Delete the details
			if(!ListUtl.isEmpty(deleteSet)){
				for (EJBResultSet deleteRS : deleteSet) {
					updateTransaction(deleteRS);
				}
			}            
			// Now insert based on the details available
			if(!ListUtl.isEmpty(insertSet)){
				for (EJBResultSet insertRS : insertSet) {
					updateTransaction(insertRS);
				}
			}            
		}
		catch (Exception se) {
			// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// _context.setRollbackOnly();
			_logger.error(se);
			String errorMessage = se.getMessage();
			if (errorMessage == null)
				throw new EJBException(se);
			throw new EJBXRuntime(errorMessage);
		}
		finally {
			releaseConnection(conn);
		}
		return updateStatus;
	
	}
	/**
	 * Similar to the update method, but if one of the records fail to update, the
	 * method will throw an exception thus rolling back the full transaction.
	 * @param rs
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B82E2CB03DD
	 */
	public EJBResultSet updateTransaction(EJBResultSet rs) throws EJBXRuntime
	{
        Connection conn = null;
        try {

            EditorMetaData editorMetaData = rs.getEJBResultSetMetaData().getEditorMetaData();
            if (!editorMetaData.isEditable())
                throw new EJBXRuntime( "Editor is not editable :" + editorMetaData.getName() );

            if (rs.getMetaData() == null)
                throw new EJBXRuntime( _module, "MetaData is not set for the resultset to be updated" );

//          rs.beforeFirst();

            conn    = getConnection();
            int rv  = 0;

            // check if there is any interceptor defined for this editor
            TableManagerInterceptor tblMgrInterceptor = null;
            if(editorMetaData.getInterceptor() != null) {
                tblMgrInterceptor = TableManagerInterceptorFactory.getInstance(editorMetaData.getInterceptor());
	            if (tblMgrInterceptor != null ) {
		            tblMgrInterceptor.setSubSystem( _subSystem );
	            }
            }

            // perform any before-update operations
            if(tblMgrInterceptor != null) {
                tblMgrInterceptor.beforeUpdate(rs);
            }

            rs.beforeFirst();

            while( rs.next() ) {
                switch (rs.getRecord().getStatus()) {
                    case RecordStatus._RMS_INSERTED :
                        rv = insert( rs, conn );
                        break;

                    case RecordStatus._RMS_UPDATED :
                        rv = update( rs, conn );
                        break;

                    case RecordStatus._RMS_DELETED :
                        rv = delete( rs, conn );
                        break;

                    default :
                        throw new EJBXRuntime( "Record Status is not recognized:" + rs.getRecord().getStatus() );
                }

                if (rv == 1)
                    rs.getRecord().setSyncStatus( RecordStatus._RSS_SYNC  );
                else
                    rs.getRecord().setSyncStatus( RecordStatus._RSS_ERROR );
            }

            // perform any after-update operations
            if(tblMgrInterceptor != null) {
                tblMgrInterceptor.afterUpdate(rs);
            }

            return rs;
        }
		catch(SQLException se) {
			
			//log the raw error
			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );

			// translate the SQL error into an user-displayable error 
			String errorMessage = Environment.getInstance( _subSystem ).getDbPoolMgr().getSQLExceptionTranslator().translate( se );

			// rollback happens
			if (errorMessage != null) {

				_context.setRollbackOnly();
				throw new EJBXRuntime( errorMessage );
			}
			else {

				throw new EJBException( se );
			}
		}
        catch (Exception e) {

            Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
            throw new EJBException( e );
        }
        finally {

			releaseConnection( conn );
        }
	}

	/**
	 * Method for updating the Master/Detail Screens. Will do a) MasterTable Insert,
	 * b) detail table delete and then c) detail table inserts. All the above
	 * operations are done as a single transaction.
	 * 
	 * @param masterSet
	 * @param deleteSet
	 * @param insertSet
	 * @return boolean
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @throws java.rmi.RemoteException
	 * @roseuid 3F2A14F800CB
	 */
	public boolean updateTransaction(EJBResultSet masterSet, EJBResultSet deleteSet, EJBResultSet insertSet) throws EJBXRuntime, RemoteException
	{
		Connection conn = null;
		boolean updateStatus = true;
		String module = getClass().getName();
		try {
			conn = getConnection();
			// First deal with the parent table info - delete or insert or update
            updateTransaction( masterSet, conn );
			// Delete the details
			updateTransaction( deleteSet, conn );
			//Now insert based on the details available
			updateTransaction( insertSet, conn );
		}
		catch(SQLException se) {
			_context.setRollbackOnly();
			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
			String errorMessage = Environment.getInstance( _subSystem ).getDbPoolMgr().getSQLExceptionTranslator().translate( se );
			if (errorMessage == null)
				throw new EJBException( se );
			throw new EJBXRuntime( errorMessage );
		}
		catch(IOException ioe) {
			_context.setRollbackOnly();
			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( ioe );
			SQLException se = new SQLException( ioe.getMessage() );
			String errorMessage = Environment.getInstance( _subSystem ).getDbPoolMgr().getSQLExceptionTranslator().translate( se );
			if (errorMessage == null)
				throw new EJBException( ioe );
			throw new EJBXRuntime( errorMessage );
		}
		finally {
			releaseConnection( conn );
		}
		return updateStatus;
	}

	/*
	 * Mass Update
	 */
	public int updateTransaction(EJBCriteria criteria) throws RemoteException, EJBXRuntime{
		return 0;
	}
	
	/**
	 * Internal method to perform operations on a given connection.  Does not perform any transaction control
	 * 
	 * @param rs
	 * @param conn
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @throws java.sql.SQLException
	 * @roseuid 3F2A18EE01F4
	 */
	private EJBResultSet updateTransaction(EJBResultSet rs, Connection conn) throws EJBXRuntime, SQLException, IOException
	{
		if (rs == null)
			return null;
		EditorMetaData editorMetaData = rs.getEJBResultSetMetaData().getEditorMetaData();
		if (!editorMetaData.isEditable())
			throw new EJBXRuntime( "Editor is not editable :" + editorMetaData.getName() );

		if (rs.getMetaData() == null)
			throw new EJBXRuntime( _module, "MetaData is not set for the resultset to be updated" );

		rs.beforeFirst();
		int rv  = 0;
		while( rs.next() ) {
			switch (rs.getRecord().getStatus()) {
				case RecordStatus._RMS_INSERTED :
					rv = insert( rs, conn );
					break;

				case RecordStatus._RMS_UPDATED :
					rv = update( rs, conn );
					break;

				case RecordStatus._RMS_DELETED :
					rv = delete( rs, conn );
					break;

				default :
					throw new EJBXRuntime( "Record Status is not recognized:" + rs.getRecord().getStatus() );
			}

			//System.out.println( "RV = " + rv );

			if (rv == 1)
				rs.getRecord().setSyncStatus( RecordStatus._RSS_SYNC  );
			else
				rs.getRecord().setSyncStatus( RecordStatus._RSS_ERROR );
		}
		return rs;
	}

	/**
	 * Underlying method to update a record  
	 * 
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B09270801D6
	 */
	private int insert(EJBResultSet rs, Connection conn) throws SQLException, IOException
	{
		int rv = 0;

		final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils( _serverType );

		// if metadata has a procedure defined, use that.  Otherwise build the INSERT statement
        if (rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure() != null &&
            rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure().startsWith( "CALL" ) )
            rv = execFunction( rs, conn );
        else
            rv = execUpdate( sqlBuilder, sqlBuilder.buildInsertSql( rs, _context.getCallerPrincipal().getName() ,conn ), rs, conn );

        return rv;


	}

	/**
	 * Underlying method to update a record
	 * 
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B09270801F4
	 */
	private int update(EJBResultSet rs, Connection conn) throws SQLException, IOException
	{
        int rv = 0;

        final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils( _serverType );

		// if metadata has a procedure defined, use that.  Otherwise build the UPDATE SQL
        if (rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure() != null &&
            rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure().startsWith( "CALL" ) )
            rv = execFunction( rs, conn );
        else
            rv = execUpdate( sqlBuilder, sqlBuilder.buildUpdateSql( rs, _context.getCallerPrincipal().getName() ), rs, conn );

        return rv;
	}

	/**
	 * Underlying method to delete a record
	 * 
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B0927080213
	 */
	private int delete(EJBResultSet rs, Connection conn) throws SQLException, IOException
	{
		final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils( _serverType );
        return execUpdate( sqlBuilder, sqlBuilder.buildDeleteSql( rs, _context.getCallerPrincipal().getName() ), rs, conn );
	}

	/**
	 * Responsible for executing the sql update using the connection
	 * 
	 * @param sql
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B192CB200A3
	 */
	private int execUpdate(EJBSQLBuilderUtils sqlBuilder, String sql, EJBResultSet rs, Connection conn) throws SQLException, IOException
	{
        PreparedStatement pStmt = null;
        int       rv   = 0;

        try {
            pStmt = conn.prepareStatement( sql );
            sqlBuilder.setClob(pStmt, rs);
            rv = pStmt.executeUpdate();
        }
        finally {
            if (pStmt != null) pStmt.close();
        }

        // If update was successful rv = 1 else rv = 0
        return rv;
	}

	protected String getClobToString(EJBColumn column, ResultSet rs, int index) throws SQLException, EJBXRuntime
	{
		if (rs == null)
			return null;
        Clob clob = rs.getClob(index);
        if (clob == null) 
        	return null;
        
    	byte[] fileByteArr = new byte[(int) clob.length()];
        InputStream inStream = clob.getAsciiStream();
        try {
            inStream.read(fileByteArr);
            if (inStream != null) 
                inStream.close();
        }
        catch (IOException ioe) {
        	Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( "Error while reading CLOB Column " + column.getName() ); 
        	Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( ioe );
        	throw new EJBXRuntime(  _module, "Error while reading CLOB Column " + column.getName() );
		}
        return new String( fileByteArr );
	}
	
	protected String getBlobToString(EJBColumn column, ResultSet rs, int index) throws SQLException, EJBXRuntime
	{
		if (rs == null)
			return null;
        Blob blob = rs.getBlob(index);
        if (blob == null) 
        	return null;
        
    	byte[] fileByteArr = new byte[(int) blob.length()];
        InputStream inStream = blob.getBinaryStream();
        try {
            inStream.read(fileByteArr);
            if (inStream != null) 
                inStream.close();
        }
        catch (IOException ioe) {
        	Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( "Error while reading BLOB Column " + column.getName() ); 
        	Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( ioe );
        	throw new EJBXRuntime(  _module, "Error while reading BLOB Column " + column.getName() );
		}
        return new String( fileByteArr );
	}	

	/**
	 * Responsible for invoking a SQL procedure/function using the connection
	 * Parameters are built using the metadata
	 *  
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B1A6CBC02B5
	 */
	private int execFunction(EJBResultSet rs, Connection conn) throws SQLException
	{

        //Stored procedure arguments are always input parameters
        //The stored procedure will always return 1 output parameter
        //The stored procedure will NOT have any INOUT parameters

        CallableStatement cstmt = null;

        try {
			final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils( _serverType );

            cstmt = conn.prepareCall( sqlBuilder.buildFunction( rs ) );

            sqlBuilder.buildFuncParams( rs, cstmt );

            // Execute the Function
            cstmt.executeQuery();

            int rv = cstmt.getInt( 1 );
            cstmt.close();

            return rv > 0 ? 1 : 0;
        }
        finally {

            if (cstmt != null) cstmt.close();
        }
	}

	/**
	 * Converts from a JDBC resultset to an EJBResultSet
	 * 
	 * @param rs
	 * @param result
	 * @throws java.sql.SQLException
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B0D9A9902B1
	 */
	private void populateData(ResultSet rs, EJBResultSet result) throws SQLException, EJBXRuntime
	{

        //Environment.getInstance( _subSystem ).getLogFileMgr( _module ).traceEnter( "populateData()" );
        int     count           = rs.getMetaData().getColumnCount();
        Vector  customColumns   = result.getEJBResultSetMetaData().getEditorMetaData().getCustomColumns();
        int     customCount     = customColumns == null ? 0 : customColumns.size();

        if (result.getMetaData().getColumnCount() != count + customCount )
            throw new EJBXRuntime( _module, "Column count in MetaData: " + result.getMetaData().getColumnCount() + " does not match SQL column count: " + count );

        Vector          records         = new Vector();
        Vector          columns         = null;
        int             index           = 0;
        ColumnMetaData  columnMetaData  = null;
        int             rowIndex        = 0;
        int             action          = result.getEJBCriteria().getPageAction();
        int             pageSize        = result.getEJBCriteria().getPageSize();
        int             currPosition    = result.getEJBCriteria().getCurrPosition() == 0 ? 1 : result.getEJBCriteria().getCurrPosition();

        // Move the resultset to the starting position (before first record)
        rs.beforeFirst();

        if (pageSize != -1) {

            int rsCount = 0;
            // To see if there are any rows
            if (rs.next()) rsCount++;

            // Set rs to ordinal position
            rs.beforeFirst();

            // first, depending on the paging operation and current page, 
            // setup the resultset position
            if (rsCount != 0) {

                switch (action) {

                    case AVConstants._FETCH_FORWARD     :
                        rs.absolute( currPosition + pageSize - 1 );
                        result.getEJBCriteria().setCurrPosition( currPosition + pageSize );
                        break;

                    case AVConstants._FETCH_BACKWARD    :
                        rs.absolute( currPosition - pageSize - 1 );
                        result.getEJBCriteria().setCurrPosition( currPosition - pageSize );
                        break;

                    case AVConstants._FETCH_FIRST       :
                        result.getEJBCriteria().setCurrPosition( 1 );
                        break;

                    case AVConstants._FETCH_LAST        :
                    	rsCount = 0;
						while (rs.next()) rsCount++;
						result.getEJBCriteria().setRowCount( rsCount );
						rs.beforeFirst();
                        // Get only the integral part
                        rsCount = rsCount / pageSize;
                        rs.absolute( pageSize * rsCount );
                        result.getEJBCriteria().setCurrPosition( (pageSize * rsCount) + 1 );
                        break;

                    case AVConstants._UNDEF             :
                    	rsCount = 0;
						while (rs.next()) rsCount++;
						result.getEJBCriteria().setRowCount( rsCount );
						rs.beforeFirst();
                        if (currPosition > 1 && currPosition < rsCount ) {
                            rs.absolute( currPosition - 1 );
                        }
                        else if (currPosition > rsCount ) {
                            rsCount = rsCount / pageSize;
                            rs.absolute( pageSize * rsCount );
                            result.getEJBCriteria().setCurrPosition( (pageSize * rsCount) + 1 );
                        }
                        break;

                    default :
                        throw new EJBXRuntime( _module, "Paging Action in EJBCriteria is not recognized : " + String.valueOf( action ) );
                }
            }
        }
        else {

            pageSize = _MAX_RECORDS;
        }

        // at this point, the resultset has been positioned and rowIndex, pageSize are set.
        // just look through and read
        while (rs.next() && ++rowIndex <= pageSize ) {
            columns = new Vector( count );
            // Populate the columns in the record
            for (index = 1; index <= count; index++) {
            	// get the columnMetadata for the resultset column
            	String columnName = rs.getMetaData().getColumnName( index );
            	columnMetaData = result.getEJBResultSetMetaData().getColumnMetaData( columnName );
            	if (columnMetaData == null)
            		throw new EJBXRuntime( _module, String.format( "ResultSet column %s does not have any MetaData configured ", columnName));
                columns.add( createColumn( rs, columnMetaData, index ) );
            }
            records.add( new EJBRecord( columns ) );
        }

        result.setRecords( records );

        //Environment.getInstance( _subSystem ).getLogFileMgr( _module ).traceExit( "populateData()" );
	}

	/**
	 * Build an EJBColumn from the JDBC resultset (using the column-type etc. in the column metadata)
	 * 
	 * @param rs
	 * @param columnMetaData
	 * @param index
	 * @return com.addval.ejbutils.dbutils.EJBColumn
	 * @throws java.sql.SQLException
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B0E88A10367
	 */
	private EJBColumn createColumn(ResultSet rs, ColumnMetaData columnMetaData, int index) throws SQLException, EJBXRuntime
	{

        EJBColumn column = new EJBColumn( columnMetaData );

        String longString = rs.getString( index );

        if (longString == null) {
            column.setNull();
        }
        else {

            switch (columnMetaData.getType()) {

                case ColumnDataType._CDT_STRING     :
                case ColumnDataType._CDT_FILE       :
                case ColumnDataType._CDT_USER       :
                                                        column.setStrValue      ( longString );
                                                        break;

                case ColumnDataType._CDT_INT        :   column.setIntValue      ( rs.getInt     ( index ) );
                                                        break;

                case ColumnDataType._CDT_LONG       :
                case ColumnDataType._CDT_VERSION    :	column.setLongValue     ( rs.getLong    ( index ) );
                                                        break;

                case ColumnDataType._CDT_DOUBLE     :   column.setDblValue      ( rs.getDouble  ( index ) );
                                                        break;

                case ColumnDataType._CDT_FLOAT      :   column.setFloatValue    ( rs.getFloat   ( index ) );
                                                        break;

                case ColumnDataType._CDT_TIME       :   column.setTimeValue     ( rs.getInt     ( index ) );
                                                        break;

                case ColumnDataType._CDT_CHAR       :   column.setByteValue     ( rs.getByte    ( index ) );
                                                        break;

                case ColumnDataType._CDT_BOOLEAN    :   column.setBoolValue     ( rs.getBoolean ( index ) );
                                                        break;

                case ColumnDataType._CDT_DOW        :   column.setDowValue      ( rs.getInt     ( index ) );
                                                        break;

                case ColumnDataType._CDT_SHORT      :   column.setShortValue    ( rs.getShort   ( index ) );
                                                        break;

                case ColumnDataType._CDT_KEY        :   column.setKeyValue      ( longString );
                                                        break;

                case ColumnDataType._CDT_DATE       :   column.setDateTimeValue ( new EJBDateTime( rs.getDate( index, AVConstants._GMT_CALENDAR ) ) );
                                                        break;

				case ColumnDataType._CDT_DATETIME   :
				case ColumnDataType._CDT_TIMESTAMP  :	//System.out.println( "Date with no calendar : " + rs.getTimestamp( index ) );
														//System.out.println( "Date with calendar : " + rs.getTimestamp( index, AVConstants._GMT_CALENDAR ) );
														//java.text.SimpleDateFormat s = new java.text.SimpleDateFormat( "MM/dd/yyyy hh:mm" );
														//s.setTimeZone( AVConstants._GMT_TIMEZONE );
														//System.out.println( "Formatted Date with calendar : " + s.format( rs.getTimestamp( index, AVConstants._GMT_CALENDAR ) ) );
														column.setDateTimeValue ( new EJBDateTime( new java.sql.Date( rs.getTimestamp( index, AVConstants._GMT_CALENDAR ).getTime() )  ) );
                                                        break;

                case ColumnDataType._CDT_CLOB       :  	column.setStrValue( getClobToString(column, rs, index));
														break;
                case ColumnDataType._CDT_BLOB       :  	column.setStrValue( getBlobToString(column, rs, index));
														break;

                case ColumnDataType._CDT_LINK     	:   break;

                default                             :   throw new EJBXRuntime( _module, "Type of the column is not recognized: " + columnMetaData.getType() );
            }
        }

        return column;
	}

	/**
	 * Lookup the metadata for an editor
	 * 
	 * @param editorName
	 * @param editorType
	 * @return com.addval.ejbutils.dbutils.EJBResultSetMetaData
	 * @roseuid 3B1A6EC10363
	 */
	private EJBResultSetMetaData getMetaData(String editorName, String editorType)
	{
        try {

            String  metaDataServerName   	= Environment.getInstance( _subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBSEditorMetaDataBeanName", "" );
            EJBSEditorMetaDataHome   home   = (EJBSEditorMetaDataHome) EJBEnvironment.lookupEJBInterface( _subSystem, metaDataServerName, EJBSEditorMetaDataHome.class );
            EJBSEditorMetaDataRemote remote = home.create();

            return new EJBResultSetMetaData( remote.lookup( editorName, editorType ) );
        }
        catch (Exception e) {

            Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
            throw new EJBException( e );
        }
	}

	/**
	 * Get a JDBC connection from the pool
	 * 
	 * @return java.sql.Connection
	 * @roseuid 3B09C0CF03B7
	 */
	private Connection getConnection()
	{
        return Environment.getInstance( _subSystem ).getDbPoolMgr().getConnection();
	}

	/**
	 * Release a JDBC connection back to the pool
	 * 
	 * @param conn
	 * @roseuid 3B09C0E002A3
	 */
	private void releaseConnection(Connection conn)
	{
		if (conn != null)
        	Environment.getInstance( _subSystem ).getDbPoolMgr().releaseConnection( conn );
	}

	public void setCallerPrincipalName(String name) throws RemoteException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}
	
	public void setEnvironmentInstances(Map<String,Environment> envInstances) throws RemoteException{
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

}
