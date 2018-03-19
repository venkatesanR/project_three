package com.addval.parser.xmlparser;

import java.sql.*;
import java.util.*;
import java.sql.Types;
import com.addval.environment.Environment;
import com.addval.parser.InvalidInputException;
import com.addval.dbutils.DAOManager;
import com.addval.dbutils.DAOUtils;
import com.addval.dbutils.DAOSQLStatement;
import org.apache.commons.beanutils.DynaBean;


// This class have methods for basic DAO opertations
public class XmlLoaderDao extends DAOManager
{
	protected static final String _module 		= "com.addval.parser.xmlparser;.XmlLoaderDao";
	private String _projectName					= null;
	protected Connection _conn					= null;
	private Environment _env                    = null;
    private String _daoCacheName 				= null;

   	private static transient final org.apache.log4j.Logger _logger = 	com.addval.utils.LogMgr.getLogger(XmlLoaderDao.class);

	private PreparedStatement _selectStatement 		=	null;
	private PreparedStatement _insertStatementBatch	=	null;
	private PreparedStatement _updateStatementBatch	=	null;
	private PreparedStatement _deleteStatementBatch	=	null;

	private String _selectSqlName 				= null;
	private String _insertSqlName 				= null;
	private String _updateSqlName 				= null;
	private String _deleteSqlName				= null;
	private String _deleteOverlappingSqlName	= null;

	private int _insertCount = 0;
	private int _updateCount = 0;
	private int _deleteCount = 0;
	private int _failCount = 0;
	
	/*  for loaders _autoCommit is true and
		for servers _autoCommit should be injected as false
	*/
	private boolean _autoCommit = true;

	public boolean isAutoCommit() {
		return _autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this._autoCommit = autoCommit;
	}

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
		return (Hashtable) getEnvironment().getCacheMgr().get( getDaoCacheName() );
	}

	public void setLookupSqlName(String aName) {
		_selectSqlName = aName;
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
		return _selectSqlName;
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

	public final void initialize() throws SQLException
	{
		try {
			configureLookupSql(getLookupSqlName());
			configureInsertSql(getInsertSqlName());
			configureUpdateSql(getUpdateSqlName());
			configureDeleteSql(getDeleteSqlName());
		}
		catch(Exception e) {
			_logger.error(e);
		}
	}

    public int[] insertRow(Object object, boolean execute) throws SQLException
    {
		return insertRow( null, object, execute );
    }

	public int[] insertRow(String sqlName, Object object) throws SQLException
	{
		return insertRow( sqlName, object, true );
	}
	
	public int[] insertRow(String sqlName, Object object, boolean execute) throws SQLException
	{
		PreparedStatement pStatement = null;
		try {
			if (sqlName == null)
				return insert( new DAOUtils( getDAOSQLStatement( _insertSqlName ), getServerType() ), object, execute );
			pStatement = getConnection().prepareStatement( sqlName );
			return insert( new DAOUtils( getDAOSQLStatement( sqlName ), getServerType() ), object, execute, pStatement );
		}
		finally {
			if (pStatement != null)
				pStatement.close();
		}
	}

	public int[] deleteRow(Object object, boolean execute) throws SQLException
	{
		return deletetRow( null, object, execute );
	}

	public int[] deletetRow(String sqlName, Object object) throws SQLException
	{
		return deletetRow( sqlName, object, true );
	}

	public int[] deletetRow(String sqlName, Object object, boolean execute) throws SQLException
	{
		PreparedStatement pStatement = null;
		try {
			if (sqlName == null)
				return delete( new DAOUtils( getDAOSQLStatement( _deleteSqlName ), getServerType() ), object, execute );
			pStatement = getConnection().prepareStatement( sqlName );
			return delete( new DAOUtils( getDAOSQLStatement( sqlName ), getServerType() ), object, true, pStatement );
		}
		finally {
			if (pStatement != null)
				pStatement.close();
		}
	}

    public Map<Object, Object> selectRow(Map<Object, Object> object) throws SQLException
    {
		if (_selectSqlName == null)
			return null;
		DAOSQLStatement statement 	= getDAOSQLStatement( _selectSqlName );
		DAOUtils utils = new DAOUtils( statement, getServerType() );
        if (object != null) {
			  utils.setProperties( _selectStatement, object );
		}
         //    System.out.println("select Statement = " + _selectStatement.toString());
        ResultSet rs = _selectStatement.executeQuery();
        if (!rs.next())
            return null;
        Map result = utils.getMap( rs );
        if (rs.next())
            throw new IllegalStateException( "Multiple rows found for select Query " + _selectSqlName );
        return merge( result, object );
    }

    private Map<Object, Object> merge(Map source, Map<Object, Object> target)
    {
		for (Object key : target.keySet()) {
			Object dbValue = source.get(key);
			if (dbValue == null)
				continue;
			Object object = target.get(key);
			if (object == null) {
				target.put(key, dbValue);
				//         	System.out.println("############ DB value " + dbValue + " Xml value = " + object + " to be udpated to DB = " + target.get( key ));
			}

		}
        return target;
    }

    public Map[] selectRow(Object object) throws SQLException
    {
		DAOSQLStatement statement 	= getDAOSQLStatement(_selectSqlName);
		return selectRow( object, statement, _selectStatement );
	}

	public Map[] selectRow(Object object, String sqlName) throws SQLException
	{
		if (sqlName == null)
			return selectRow( object );
		PreparedStatement pstmt = null;
		try {
			DAOSQLStatement statement 	= getDAOSQLStatement( sqlName );
			pstmt = getConnection().prepareStatement( statement.getSQL( getServerType() ));
			return selectRow( object, statement, pstmt );
		}
		finally {
			closeStatement( pstmt );
		}
	}

	private Map[] selectRow(Object object, DAOSQLStatement statement, PreparedStatement pstmt) throws SQLException
	{
		DAOUtils utils = new DAOUtils( statement, getServerType() );
		if (object != null) 
			utils.setProperties( pstmt, object );
		ResultSet rs	= null;
		try {
			rs	= pstmt.executeQuery();
			int size = 0;
			while(rs.next())
				size++;
			Map[] map	= new HashMap[size];
			if (size == 0)
				return map;
			for (int j=0; j<size; j++)
				map[j] = new HashMap();
			rs.beforeFirst();
			ResultSetMetaData rsmd = rs.getMetaData();
			int i = 0 ;
			while(rs.next()) {
				int cols = rsmd.getColumnCount();
				for (int j=1;j<=cols;j++) {
					int colType=rsmd.getColumnType(j);
					if(colType==Types.VARCHAR)
						map[i].put(rsmd.getColumnName(j),rs.getString(j));
					else if(colType==Types.NUMERIC)
						map[i].put(rsmd.getColumnName(j), rs.getDouble(j) );
					else if(colType==Types.DATE)
						map[i].put(rsmd.getColumnName(j),rs.getDate(j));
					else if(colType==Types.INTEGER)
						map[i].put(rsmd.getColumnName(j), rs.getInt(j) );
				}
				i++;
			}
			return map;
		}
		finally {
			closeRs( rs );
		}
	}

	public int[] updateRow(Object object) throws SQLException
	{
	    //_log.traceExit( "updateRow" );
		return updateRow( object, true );
	}

    public int[] updateRow(Object object, boolean execute) throws SQLException
    {
		//_log.traceEnter( "updateRow" );
		DAOSQLStatement statement 	= getDAOSQLStatement( _updateSqlName );
		DAOUtils utils 				= new DAOUtils( statement, getServerType() );
		//_log.traceExit( "updateRow" );
		return update( utils, object, execute );
    }

    private int[] insert( DAOUtils utils, Object object, boolean execute) throws SQLException
    {
		int[] affectedRows = insert( utils, object, execute, _insertStatementBatch );
		if (affectedRows == null)
			return null;
		_insertCount += getCount( affectedRows );
		return affectedRows;
	}

    private int[] insert( DAOUtils utils, Object object, boolean execute, PreparedStatement pStatement) throws SQLException
    {
		try {
			utils.setProperties( pStatement, object );
			pStatement.addBatch();
			if (execute)
				return pStatement.executeBatch();
		}
		catch (SQLException e) {
			pStatement.clearBatch();
			throw e;
		}
		return null;
	}

	private int[] update( DAOUtils utils, Object object, boolean execute) throws SQLException
	{
		int[] affectedRows = update( utils, object, execute, _updateStatementBatch );
		if (affectedRows == null)
			return null;
		_updateCount += getCount( affectedRows );
		return affectedRows;
	}

	private int[] update( DAOUtils utils, Object object, boolean execute, PreparedStatement pStatement) throws SQLException
	{
		try {
			utils.setProperties( pStatement, object );
			pStatement.addBatch();
			if (execute)
				return pStatement.executeBatch();
		}
		catch (SQLException e) {
			pStatement.clearBatch();
			throw e;
		}
		return null;
	}

    private int[] delete(DAOUtils utils, Object object, boolean execute) throws SQLException
    {
		int[] affectedRows = delete( utils, object, execute, _deleteStatementBatch ) ;
		if (affectedRows == null)
			return null;
		_deleteCount += getCount( affectedRows );
		return affectedRows;
	}
    

    private int[] delete(DAOUtils utils, Object object, boolean execute, PreparedStatement pStatement) throws SQLException
    {
		try {
			utils.setProperties( pStatement, object );
			pStatement.addBatch();
			if (execute)
				return pStatement.executeBatch();
		}
		catch (SQLException e) {
			pStatement.clearBatch();
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
			closeRs( rs );
			closeStatement( pStmt );
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
			closeRs( rs );
			closeStatement( pStmt );
		}
	}

	public PreparedStatement getResultSet(String sqlName) throws SQLException
	{
		//_log.traceEnter( "getResultSet" );
		DAOSQLStatement statement	= getDAOSQLStatement( sqlName );
		PreparedStatement pStmt 	= getConnection().prepareStatement( statement.getSQL( getServerType() ) );
		pStmt.executeQuery();
		///_log.traceExit( "getResultSet" );
		return pStmt;
	}

	public Map<String, String> getKeyPairs(String sqlName) throws SQLException
	{
		//_log.traceEnter( "getKeyPairs" );
		Map<String, String> keyPairs = new HashMap<String, String>();
		if (sqlName == null)
			return keyPairs;
		DAOSQLStatement statement 	= getDAOSQLStatement( sqlName );
		PreparedStatement pStmt 	= null;
		ResultSet rs = null;
		try {
			String sql 		= statement.getSQL( getServerType() );
			pStmt			= getConnection().prepareStatement( sql );
			int startIndex 	= sql.indexOf( "SELECT" ) + "SELECT".length();
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
				keyPairs.put( rs.getString( keyName ), rs.getString( valueName ) );
			}
		}
		finally {
			closeRs( rs );
			closeStatement( pStmt );
		}
		return keyPairs;
	}

	private int[] executeStatements() throws SQLException
	{
		//_log.traceEnter( "executeAndCloseStatements" );
		int [] affectedRows = null;
		if (_deleteStatementBatch != null) {
			affectedRows = _deleteStatementBatch.executeBatch();
			_deleteCount += getCount( affectedRows );
		}

		if (_updateStatementBatch != null) {
			affectedRows = _updateStatementBatch.executeBatch();
			_updateCount += getCount( affectedRows );
		}
		if (_insertStatementBatch != null) {
			affectedRows = _insertStatementBatch.executeBatch();
			_insertCount += getCount( affectedRows );
		}
		if (_conn != null && isAutoCommit()) {
			_conn.commit();
		}
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
			releaseConnection( _conn );
		}

		public String getprintDetails()
		{
			StringBuffer details = new StringBuffer();
			details.append("No of rows deleted  " ).append( _deleteCount ).append("\n");
			details.append("No of rows Updated  " ).append( _updateCount - _failCount).append("\n");
			details.append("No of rows Inserted " ).append( _insertCount).append("\n");
			return details.toString() ;
		}


	private DAOSQLStatement getDAOSQLStatement(String sqlName)
	{
		return (DAOSQLStatement)getSqls().get( sqlName );
	}

    // not implemeted - DAOManager method
	protected DAOSQLStatement getDAOSQLStatement( int type )
	{
		throw new IllegalStateException( "Method \"getDAOSQLStatement( int type )\" not yet implemented" );
	}

	public boolean hasThisRow(Object object) throws SQLException
	{
		 //_log.traceEnter( "hasThisRow" );
		if (_selectSqlName == null)
		 	return false;
        DAOSQLStatement statement 	= getDAOSQLStatement(_selectSqlName);
        ResultSet rs 				= null;
        try {
			DAOUtils utils 	= new DAOUtils( statement, getServerType() );
			if (object != null)
				utils.setProperties( _selectStatement, object );
			rs = _selectStatement.executeQuery();
            return rs.next();
        }
        finally {
			closeRs( rs );
        }
	}
	public void hasMultipleRow(Object object) throws SQLException, InvalidInputException
	{
		if (_selectSqlName == null)
		 	return;
        DAOSQLStatement statement 	= getDAOSQLStatement(_selectSqlName);
        ResultSet rs 				= null;
        try {
			DAOUtils utils 	= new DAOUtils( statement, getServerType() );
			if (object != null)
				utils.setProperties( _selectStatement, object );
			rs = _selectStatement.executeQuery();	
			int noOfRows = 0;
			while ( rs.next() )
			noOfRows++;
            if (noOfRows > 1)
                throw new InvalidInputException( "Multiple rows found while overlap check " );
        }
        finally {
			closeRs( rs );
        }
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
        ResultSet rs 			= null;
        try {
			DAOSQLStatement statement 	= getDAOSQLStatement( sqlName );
			pStmt 						= getConnection().prepareStatement( statement.getSQL( getServerType() ) );

			if (object != null) {
				DAOUtils utils = new DAOUtils( statement, getServerType() );
				utils.setProperties( pStmt, object );
			}
		    rs = pStmt.executeQuery();
            if (rs.next())
                return rs.getString( 1 );
        }
        finally {
			closeRs( rs );
			closeStatement( pStmt );
        }
		return null;
    }

	public void deleteOverlapping() throws SQLException {
		Environment.getInstance( _projectName ).getLogFileMgr( getClass().getName() ).traceEnter( getClass().getName()+".deleteOverlapping() " );
		_logger.debug("Enter: deleteOverlapping");
		if (com.addval.utils.StrUtl.isEmpty(getDeleteOverlappingSqlName()))
			return;

		CallableStatement cstmt = null;
		try	{
			// the whole load should be one transaction
			getConnection().setAutoCommit ( false );
			DAOSQLStatement statement 	= getDAOSQLStatement( getDeleteOverlappingSqlName());
			cstmt = getConnection().prepareCall(statement.getSQL( getServerType() ));
			cstmt.executeQuery();
		}
		catch( SQLException ex ) {
			getConnection().rollback();
			_logger.error( "deleteOverlappingPayload(): Error while deleting overlapping records" );
			_logger.error( ex );
			throw ex;
		}
		finally {
			closeStatement( cstmt );
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
		if (_conn == null)
			_conn = getEnvironment().getDbPoolMgr().getConnection();
		return _conn;
	}

    protected void releaseConnection(Connection conn)
    {
		if (conn != null)
			getEnvironment().getDbPoolMgr().releaseConnection( conn );
    }


	public void configureLookupSql(String lookupSqlName) throws SQLException
	{
		closeStatement( _selectStatement );
		if (lookupSqlName == null)
			return;
		DAOSQLStatement statement 	= getDAOSQLStatement( lookupSqlName );
		if (statement != null) 
			_selectStatement = getConnection().prepareStatement( statement.getSQL( getServerType() ), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
	}

	public void configureInsertSql(String insertSqlName) throws SQLException
	{
		closeStatement( _insertStatementBatch );
		if (insertSqlName == null)
			return;
		DAOSQLStatement statement = getDAOSQLStatement( insertSqlName );
		if (statement != null)
			_insertStatementBatch = getConnection().prepareStatement( statement.getSQL( getServerType() ) );

	}

	public void configureUpdateSql(String updateSqlName) throws SQLException
	{
		closeStatement( _updateStatementBatch );
		if (updateSqlName == null)
			return;
		DAOSQLStatement statement = getDAOSQLStatement( updateSqlName );
		if (statement != null)
			_updateStatementBatch = getConnection().prepareStatement( statement.getSQL( getServerType() ) );
	}

	public void configureDeleteSql(String deleteSqlName) throws SQLException
	{
		closeStatement( _deleteStatementBatch );
		if (deleteSqlName == null)
			return;
		DAOSQLStatement statement = getDAOSQLStatement( deleteSqlName );
		if (statement != null)
			_deleteStatementBatch = getConnection().prepareStatement( statement.getSQL( getServerType() ) );
	}
	/*
	 * Method is update the rejected records information in Job Schedule Screen
	 */
	public void updateTaskStatus(int count) throws SQLException
    {
        Statement stmt = null;
        Connection con = null;

        try {
            con = getEnvironment().getDbPoolMgr().getConnection();
            String sql = "UPDATE job_status SET run_error_desc = '" +
                count + " record(s) rejected, verify detail' " +
                "WHERE job_status_key = (SELECT MAX (job_status_key) FROM job_status WHERE run_status = 'Started')";

            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } finally {
            if (stmt != null)
            	stmt.close();
            if (con != null) {
            	getEnvironment().getDbPoolMgr().releaseConnection( con );
            }
        }
   }

	private int getCount(int[] affectedRows)
	{
		int count = 0;
		for (int affectedRow : affectedRows) {
			if (affectedRow == -2)
				count++;
			else
				count = count + affectedRow;
		}
		return count;
	}

	private void closeStatement(Statement stmt)
	{
		if (stmt == null)
			return;
		try {
			stmt.close();
		}
		catch(Exception e) {
			//do nothing
		}
	}

	private void closeRs(ResultSet rs)
	{
		if (rs == null)
			return;
		try {
			rs.close();
		}
		catch(Exception e) {
			//do nothing
		}
	}

	public int getInsertCount()
	{
		return _insertCount;
	}

	public int getUpdateCount()
	{
		return _updateCount;
	}

	public int getDeleteCount()
	{
		return _deleteCount;
	}
	
	public void setUpdateCount(int count)
	{
		_updateCount = count;
	}
	
	public void setInsertCount(int count)
	{
		_insertCount = count;
	}
	
	public void setDeleteCount(int count)
	{
		_deleteCount = count;
	}
	public void setFailCount(int failCount)
	{
		_failCount = failCount ;
	}

	public int getFailCount()
	{
		return _failCount ;
	}
	
}