package com.addval.dataloader;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.sql.Types;
import com.addval.environment.Environment;
import com.addval.utils.LogFileMgr;
import com.addval.dbutils.DAOManager;
import com.addval.dbutils.DAOUtils;
import com.addval.dbutils.DAOSQLStatement;
import org.apache.commons.beanutils.DynaBean;


// This class have methods for basic DAO opertations
public class XmlLoaderDao extends DAOManager
{
	protected static final String _module 		=	"com.addval.dataloader.XmlLoaderDao";
	private String _projectName					=	null;
	protected Connection _conn					=	null;
	private Environment _env                    =   null;
    private String    _daoCacheName = null;

   	private static transient final org.apache.log4j.Logger _logger = 	com.addval.utils.LogMgr.getLogger(XmlLoaderDao.class);

	private PreparedStatement _selectStatement 	=	null;
	private PreparedStatement _insertStatementBatch	=	null;
	private PreparedStatement _updateStatementBatch	=	null;
	private PreparedStatement _deleteStatementBatch	=	null;

	private String _lookupSqlName 					= null;
	private String _insertSqlName 					= null;
	private String _updateSqlName 					= null;
	private String _deleteSqlName					= null;
	private String _deleteOverlappingSqlName		= null;

	private int _insertCount = 0;
	private int _updateCount = 0;
	private int _deleteCount = 0;

	public void setProjectName(String aName) {
		_projectName = aName;
	}

	public String getProjectName() {
		return _projectName;
	}

	public void setEnvironment(Environment env) {
		_env = env;
	}

	public Environment getEnvironment() {
		return _env;
	}

    public void setDaoCacheName(String daoCacheName) {
		_daoCacheName = daoCacheName;
	}

	public String getDaoCacheName() {
		return _daoCacheName;
	}

	protected Hashtable getSqls() {
		return (Hashtable) getEnvironment().getCacheMgr().get(getDaoCacheName());
	}

	public void setLookupSqlName(String aName) {
		_lookupSqlName = aName;
	}

	public void setInsertSqlName(String aName) {
		_insertSqlName = aName;
	}

	public void setUpdateSqlName(String aName) {
		_updateSqlName = aName;
	}

	public void setDeleteSqlName(String aName) {
		_deleteSqlName = aName;
	}

	public void setDeleteOverlappingSqlName(String aName) {
		_deleteOverlappingSqlName = aName;
	}

	public String getLookupSqlName() {
		return _lookupSqlName;
	}

	public String getInsertSqlName() {
		return _insertSqlName;
	}

	public String getUpdateSqlName() {
		return _updateSqlName;
	}

	public String getDeleteSqlName() {
		return _deleteSqlName;
	}

	public String getDeleteOverlappingSqlName() {
		return _deleteOverlappingSqlName;
	}


	public final void afterPropertiesSet() {
		_conn = getConnection();
		try {
			configureLookupSql(getLookupSqlName());
			configureInsertSql(getInsertSqlName());
			configureUpdateSql(getUpdateSqlName());
			configureDeleteSql(getDeleteSqlName());
		} catch(Exception e) {
			_logger.error(e);
		}
	}


	public int[] insertRow(String sqlName, Object object, boolean isMap) throws SQLException
	{
		//_log.traceEnter( "insertRow" );
		//_log.traceExit( "insertRow" );
		return insertRow( object,isMap, false );
	}

    public int[] insertRow(Object object,boolean isMap, boolean execute) throws SQLException
    {
		//_log.traceEnter( "insertRow" );
		DAOSQLStatement statement 	= getDAOSQLStatement( _insertSqlName );
		DAOUtils utils 				= new DAOUtils( statement, getServerType() );
		return doInsertOrUpdate( utils, object, isMap, execute, "insert" );
    }

	public int[] deletetRow(String sqlName, Object object, boolean isMap) throws SQLException
	{
		//_log.traceEnter( "deleteRow" );
		return deleteRow( object,isMap, false );
	}

    public int[] deleteRow(Object object,boolean isMap, boolean execute) throws SQLException
    {
		//_log.traceEnter( "deleteRow" );
		DAOSQLStatement statement 	= getDAOSQLStatement( _deleteSqlName );
		DAOUtils utils 				= new DAOUtils( statement, getServerType() );
		return doInsertOrUpdate( utils, object, isMap, execute, "delete" );
    }

    public Map selectRow(Map object) throws SQLException
    {
		if(_lookupSqlName == null)
			return null;
		DAOSQLStatement statement 	= getDAOSQLStatement( _lookupSqlName );
		DAOUtils utils = new DAOUtils( statement, getServerType() );
        if (object != null) {
           if (object instanceof HashMap)
               utils.setProperties( _selectStatement, (HashMap)object );
           else if (object instanceof Hashtable)
               utils.setProperties( _selectStatement, (Hashtable)object );
        }
         //    System.out.println("select Statement = " + _selectStatement.toString());
        ResultSet rs = _selectStatement.executeQuery();
        if (!rs.next())
            return null;
        Map result = utils.getMap( rs );
        if (rs.next())
            throw new IllegalStateException( "Multiple rows found for select Query " + _lookupSqlName );
        return merge( result, object );
    }

    private Map merge(Map source, Map target)
    {
        for(Iterator targ=target.keySet().iterator();targ.hasNext();) {
            Object key = targ.next();
            Object object = target.get( key );
			Object dbValue = source.get( key );
			if (dbValue == null)
				continue;
			if (object == null) {
				target.put( key, dbValue ) ;
   //         	System.out.println("############ DB value " + dbValue + " Xml value = " + object + " to be udpated to DB = " + target.get( key ));
			}

        }
        return target;
    }

    public HashMap [] selectRow(Object object,boolean isMap) throws SQLException
    {
		DAOSQLStatement statement 	= getDAOSQLStatement( _lookupSqlName );
		return selectRow( object, isMap, statement, _selectStatement );
	}

	public HashMap [] selectRow(Object object,boolean isMap, String sqlName) throws SQLException
	{
        DAOSQLStatement statement 	= getDAOSQLStatement( sqlName );

		PreparedStatement pstmt = _conn.prepareStatement( statement.getSQL( getServerType() ));
		try {
			return selectRow( object, isMap, statement, pstmt );
		}
		finally {
			if (pstmt != null)
				pstmt.close();
		}
	}

	private HashMap [] selectRow(Object object,boolean isMap, DAOSQLStatement statement, PreparedStatement pstmt) throws SQLException
	{
		DAOUtils utils 				= new DAOUtils( statement, getServerType() );
		 if (object != null) {
			if (isMap){
					if (object instanceof HashMap)
						utils.setProperties( pstmt, (HashMap)object );
					else if (object instanceof Hashtable)
						utils.setProperties( pstmt, (Hashtable)object );
			}
			else
				utils.setProperties( pstmt, object );
		}
		ResultSet rs	= pstmt.executeQuery();
		int size		= 0;
		while(rs.next()){
			size++;
		}
		HashMap map[]	= new HashMap[size];
		for(int j=0;j<size;j++){
				map[j]=new HashMap();
		}
		rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int i = 0 ;
		while(rs.next()){
			int cols = rsmd.getColumnCount();
			for(int j=1;j<=cols;j++){
				int colType=rsmd.getColumnType(j);
				if(colType==Types.VARCHAR){
					map[i].put(rsmd.getColumnName(j),rs.getString(j));
				}else if(colType==Types.NUMERIC){
					map[i].put(rsmd.getColumnName(j),new Double(rs.getDouble(j)));
				}else if(colType==Types.DATE){
					 map[i].put(rsmd.getColumnName(j),rs.getDate(j));
				}else if(colType==Types.INTEGER){
					 map[i].put(rsmd.getColumnName(j),new Integer(rs.getInt(j)));
				}
			}
			i++;
		 }
		_conn.commit();

		return map;
	}

	public int[] updateRow(Object object, boolean isMap) throws SQLException
	{
	    //_log.traceExit( "updateRow" );
		return updateRow( object, isMap, false  );
	}

    public int[] updateRow(Object object, boolean isMap, boolean execute) throws SQLException
    {
		//_log.traceEnter( "updateRow" );
		DAOSQLStatement statement 	= getDAOSQLStatement( _updateSqlName );
		DAOUtils utils 				= new DAOUtils( statement, getServerType() );
		//_log.traceExit( "updateRow" );
		return doInsertOrUpdate( utils, object, isMap, execute, "update" );
    }

    private int[] doInsertOrUpdate( DAOUtils utils, Object object, boolean isMap, boolean execute, String type) throws SQLException
    {
		//_log.traceEnter( "doInsertOrUpdate" );
		try {
			if (object != null) {
				if(type.equals("insert")) {
					if (isMap)  {
						if (object instanceof HashMap)
							utils.setProperties( _insertStatementBatch, (HashMap)object );
						else if (object instanceof Hashtable)
							utils.setProperties( _insertStatementBatch, (Hashtable)object );
					} else {
						utils.setProperties( _insertStatementBatch, object );
					}
					_insertStatementBatch.addBatch();

				} else if(type.equals("update")) {

					if (isMap)  {
						if (object instanceof HashMap)
							utils.setProperties( _updateStatementBatch, (HashMap)object );
						else if (object instanceof Hashtable)
							utils.setProperties( _updateStatementBatch, (Hashtable)object );
					} else {
						utils.setProperties( _updateStatementBatch, object );
					}
					_updateStatementBatch.addBatch();
				}
				else if(type.equals("delete")) {
					if (isMap)  {
						if (object instanceof HashMap)
							utils.setProperties( _deleteStatementBatch, (HashMap)object );
						else if (object instanceof Hashtable)
							utils.setProperties( _deleteStatementBatch, (Hashtable)object );
					} else {
						utils.setProperties( _deleteStatementBatch, object );
					}
					_deleteStatementBatch.addBatch();
				}
			}
			if (execute)
				return executeStatements();
		}
		catch(SQLException e) {
			if (type.equals("insert"))
				_insertStatementBatch.clearBatch();
			else if (type.equals( "update") )
				_updateStatementBatch.clearBatch();
			else
				_deleteStatementBatch.clearBatch();
			throw e;
		}
		return null;
	}

	public Object[] getBeans(String sqlName, Object dataObject) throws Exception
	{
		//_log.traceEnter( "getBeans" );
		if (dataObject == null)
			return null;
		PreparedStatement pStmt 	= null;
		ResultSet rs 				= null;
		try {
			pStmt 	= getResultSet( sqlName );
			rs 		= pStmt.getResultSet();
			return ParserUtils.getBeans( rs, dataObject );
		}
		finally {
			if (rs != null)
				rs.close();
			if (pStmt != null)
				pStmt.close();
			//_logger.debug( "exit getBeans" );
		}
	}

	public DynaBean[] getDynaBeans(String sqlName) throws Exception
	{
		//_log.traceEnter( "getDynaBeans" );
		PreparedStatement pStmt 	= null;
		ResultSet rs 				= null;
		try {
			pStmt 	= getResultSet( sqlName );
			rs 		= pStmt.getResultSet();
			return ParserUtils.getDynaBeans( rs );
		}
		finally {
			if (rs != null)
				rs.close();
			if (pStmt != null)
				pStmt.close();
			//_log.traceExit( "getDynaBeans" );
		}
	}

	public PreparedStatement getResultSet(String sqlName) throws SQLException
	{
		//_log.traceEnter( "getResultSet" );
		DAOSQLStatement statement	= getDAOSQLStatement( sqlName );
		PreparedStatement pStmt 	= _conn.prepareStatement( statement.getSQL( getServerType() ) );
		pStmt.executeQuery();
		///_log.traceExit( "getResultSet" );
		return pStmt;
	}

	public HashMap getKeyPairs(String sqlName) throws SQLException
	{
		//_log.traceEnter( "getKeyPairs" );
		HashMap keyPairs = new HashMap();
		if (sqlName == null)
			return keyPairs;
		DAOSQLStatement statement 	= getDAOSQLStatement( sqlName );
		PreparedStatement pStmt 	= null;
		ResultSet rs = null;
		try {
			String sql 		= statement.getSQL( getServerType() );
			pStmt			= _conn.prepareStatement( sql );
			int startIndex 	= "SELECT".length();
			int endIndex 	= sql.indexOf( "FROM" );

			String columns 			= sql.substring( startIndex, endIndex ).trim();
			StringTokenizer tokens 	= new StringTokenizer( columns, "," );
			String keyName 			= null;
			String valueName 		= null;

			if (tokens.hasMoreTokens())
				keyName = tokens.nextToken().trim();
			if (tokens.hasMoreTokens())
				valueName = tokens.nextToken().trim();
			rs = pStmt.executeQuery();
			while (rs.next()) {
				String key		= rs.getString( keyName );
				String value 	= rs.getString( valueName );
				keyPairs.put( key, value );
			}
		}
		finally {
			if (rs != null)
				rs.close();
			if (pStmt != null)
				pStmt.close();
			//_log.traceExit( "getKeyPairs" );
		}
		return keyPairs;
	}

	public int[] executeStatements() throws SQLException
	{
		//_log.traceEnter( "executeAndCloseStatements" );
		int [] affectedRows = null;
		if (_deleteStatementBatch != null) {
			affectedRows =_deleteStatementBatch.executeBatch();
			for(int i=0;i<affectedRows.length;i++) {
				if(affectedRows[i]==-2)
					_deleteCount++;
			}
		}

		if (_updateStatementBatch != null) {
			affectedRows =_updateStatementBatch.executeBatch();
			for(int i=0;i<affectedRows.length;i++) {
				if(affectedRows[i]==-2)
					_updateCount++;
			}
		}
		if (_insertStatementBatch != null) {
			affectedRows =_insertStatementBatch.executeBatch();
			for(int i=0;i<affectedRows.length;i++) {
				if(affectedRows[i]==-2)
					_insertCount++;
			}
		}
		_conn.commit();
		//_log.traceExit( "executeAndCloseStatements" );
		return affectedRows;
	}


		public void close() throws SQLException
		{
			//_log.traceEnter( "Close");
			executeStatements();
			closeStatement( _selectStatement );
			closeStatement( _insertStatementBatch );
			closeStatement( _updateStatementBatch );
			closeStatement( _deleteStatementBatch );
			if (_conn != null)
				releaseConnection( _conn );
			//String details = getprintDetails().toString();
			//_log.logInfo( details );
			//System.out.println( details );

			/*_log.logInfo( "No of rows deleted  " + _deleteCount );
			_log.logInfo( "No of rows Updated  " + _updateCount );
			_log.logInfo( "No of rows Inserted " + _insertCount );
			System.out.println( "No of rows deleted   " + _deleteCount );
			System.out.println( "No of rows Updated   " + _updateCount );
			System.out.println( "No of rows Inserted  " + _insertCount );    */
			//_log.traceExit( "Close" );
		}

		public String getprintDetails()
		{
			StringBuffer details = new StringBuffer();
			details.append("No of rows deleted  " + _deleteCount ).append("\n");
			details.append("No of rows Updated  " + _updateCount).append("\n");
			details.append("No of rows Inserted " + _insertCount).append("\n");
			return details.toString() ;
		}


	private DAOSQLStatement getDAOSQLStatement(String sqlName)
	{
		return (DAOSQLStatement)getSqls().get( sqlName );
	}

    // not implemeted - DAOManager method
	protected DAOSQLStatement getDAOSQLStatement( int type )
	{
		DAOSQLStatement statement	= 	null;
		return statement;
	}

	public boolean hasThisRow(Object object, boolean isMap) throws SQLException
	{
		 //_log.traceEnter( "hasThisRow" );
		if (_lookupSqlName == null)
		 	return false;
        DAOSQLStatement statement 	= getDAOSQLStatement( _lookupSqlName );
        boolean hasThisRow 			= false;
        ResultSet rs 				= null;
        try {
			DAOUtils utils 	= new DAOUtils( statement, getServerType() );
			if (object != null) {

				if (isMap)	{
					if (object instanceof HashMap)
						utils.setProperties( _selectStatement, (HashMap)object );
					else if (object instanceof Hashtable)
						utils.setProperties( _selectStatement, (Hashtable)object );
				}
				else{
					 utils.setProperties( _selectStatement, object );
				}
			}
			rs 			= _selectStatement.executeQuery();
            hasThisRow 	= rs.next();
        }
        finally {
            if (rs != null)
                rs.close();
        }
        //_log.traceExit( "hasThisRow" );
		return hasThisRow;
	}

	public int getIntValue( String sqlName ) throws SQLException
	{
		return getIntValue( sqlName, null );
	}

    public int getIntValue(String sqlName, Object object) throws SQLException
    {
        //_log.traceEnter( "getIntValue" );
        int retVal 		= -1;
        String value	= null;
        try {
            value = getStringValue( sqlName, object );
            if (value == null)
                _logger.warn( "String value is null" );
            else
                retVal = Integer.parseInt(  value );
        }
        catch(NumberFormatException nfe) {
            _logger.warn( "Unparsable String value " + value );
        }
        //_log.traceExit( "getIntValue" );
        return retVal;
    }

	public String getStringValue(String sqlName) throws SQLException
	{
		return getStringValue( sqlName, null );
	}

    public String getStringValue(String sqlName, Object object) throws SQLException
    {
		//_log.traceEnter( "getStringValue" );
		if (sqlName == null)
			return null;
        PreparedStatement pStmt = null;
        String retVal 			= null;
        ResultSet rs 			= null;
        try {
			DAOSQLStatement statement 	= getDAOSQLStatement( sqlName );
			pStmt 						= _conn.prepareStatement( statement.getSQL( getServerType() ) );

			if (object != null) {
				DAOUtils utils = new DAOUtils( statement, getServerType() );
				utils.setProperties( pStmt, object );
			}
		    rs = pStmt.executeQuery();
            if (rs.next())
                retVal = rs.getString( 1 );
        }
        finally {
            if (rs != null)
                rs.close();
            if (pStmt != null)
                pStmt.close();
        }
       // _log.traceExit( "getStringValue" );
        return retVal;
    }

	public void deleteOverlapping() throws SQLException {
		Environment.getInstance( _projectName ).getLogFileMgr( getClass().getName() ).traceEnter( getClass().getName()+".deleteOverlapping() " );
		_logger.debug("Enter: deleteOverlapping");
		if (com.addval.utils.StrUtl.isEmpty(getDeleteOverlappingSqlName())) return;

		CallableStatement cstmt = null;

		try	{
			// the whole load should be one transaction
			 _conn.setAutoCommit ( false );
			DAOSQLStatement statement 	= getDAOSQLStatement( getDeleteOverlappingSqlName());
			cstmt = _conn.prepareCall(statement.getSQL( getServerType() ));
			cstmt.executeQuery();
		}
		catch( SQLException ex ) {
			_conn.rollback();
			_logger.error( "deleteOverlappingPayload(): Error while deleting overlapping records" );
			_logger.error( ex );
			throw ex;
		}
		finally {
			try	{
				if (cstmt != null)		cstmt.close();
					releaseConnection ( _conn  );
			}
			catch (SQLException se)	{
				_logger.error( "deleteOverlapping(): Error while deleting overlapping records finally");
				_logger.error( se );
				se.printStackTrace();
				throw se;
			}
		}
	}

	protected String getServerType()
	{
		return getEnvironment().getDbPoolMgr().getServerType();
	}

	protected String getSubsystem()
	{
		return _projectName;
	}

	protected Connection getConnection()
	{
		return getEnvironment().getDbPoolMgr().getConnection();
	}

    protected void releaseConnection( Connection conn )
    {
        getEnvironment().getDbPoolMgr().releaseConnection( conn );
    }


	public void configureLookupSql(String lookupSqlName) throws SQLException
	{
		closeStatement( _selectStatement );
		if (lookupSqlName == null)
			return;
		DAOSQLStatement statement 	= getDAOSQLStatement( lookupSqlName );
		if (statement != null)
			_selectStatement = _conn.prepareStatement( statement.getSQL( getServerType() ) );
	}

	public void configureInsertSql(String insertSqlName) throws SQLException
	{
		closeStatement( _insertStatementBatch );
		if (insertSqlName == null)
			return;
		DAOSQLStatement statement 	= getDAOSQLStatement( insertSqlName );
		if (statement != null)
			_insertStatementBatch = _conn.prepareStatement( statement.getSQL( getServerType() ) );

	}

	public void configureUpdateSql(String updateSqlName) throws SQLException
	{
		closeStatement( _updateStatementBatch );
		if (updateSqlName == null)
			return;
		DAOSQLStatement statement 	= getDAOSQLStatement( updateSqlName );
		if (statement != null)
			_updateStatementBatch = _conn.prepareStatement( statement.getSQL( getServerType() ) );
	}

	public void configureDeleteSql(String deleteSqlName) throws SQLException
	{
		closeStatement( _deleteStatementBatch );
		if (deleteSqlName == null)
			return;
		DAOSQLStatement statement 	= getDAOSQLStatement( deleteSqlName );
		if (statement != null)
			_deleteStatementBatch = _conn.prepareStatement( statement.getSQL( getServerType() ) );
	}

	private void closeStatement(PreparedStatement stmt)
	{
		if (stmt == null)
			return;
		try {
			stmt.close();
		}
		catch(Exception e) {

		}
		stmt = null;
	}
}