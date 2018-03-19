//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\TestDAOManager.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import com.addval.environment.Environment;
import java.sql.Connection;
import java.util.Vector;
import java.util.Hashtable;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 * @author AddVal Technology Inc.
 */
public class TestDAOManager extends DAOManager
{
	public static final String _SUBSYSTEM = "TestDAOManager";
	public static final String _LOOKUP_SQL_NAME = "TestDAOManager.lookupSql";
	public static final String _INSERT_SQL_NAME = "TestDAOManager.insertSql";
	public static final String _UPDATE_SQL_NAME = "TestDAOManager.updateSql";
	public static final String _DELETE_SQL_NAME = "TestDAOManager.deleteSql";

	/**
	 * @param args[]
	 * @roseuid 3EAEE6D5008C
	 */
	public static final void main(String args[])
	{

		Environment environment = Environment.getInstance( _SUBSYSTEM );

		TestDAOManager test = new TestDAOManager( );

		try {

			test.delete();

			test.insert();

			test.update();

			test.lookup();

			//test.delete();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.lang.Exception
	 * @roseuid 3EAEE6FD033C
	 */
	public void lookup() throws Exception
	{
		TestAircraftType 	results = null;

		//TestAircraftType criteria = new TestAircraftType();
		//PropertyUtils.copyProperties( criteria, Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getProperties() );

		Vector 		values 	= super.lookup( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getProperties(), "com.addval.dbutils.TestAircraftType" );
		int 		size 	= values == null ? 0 : values.size();

		for (int index = 0; index < size; index++) {

			results = (TestAircraftType)values.elementAt( index );
			System.out.println( results.getType() + " " + results.getName() + " " + results.getPayloadVolume() );
		}

		// Testing lookup of a particular column
		Connection conn = getConnection();

		PreparedStatement pstmt  = super.lookup( conn, Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getProperties() );
		ResultSet rs    = pstmt.getResultSet();
		DAOUtils  utils = new DAOUtils( getDAOSQLStatement( DAOManager._LOOKUP_SQL ), getServerType() );
		//results = new TestAircraftType();
		while (rs.next()) {

			//utils.getProperties( rs, results );
			System.out.println( utils.getProperty( rs, "type" ) );
			System.out.println( utils.getProperty( rs, "payloadVolume" ) );
		}

		rs.close();
		pstmt.close();
		releaseConnection( conn );
	}

	/**
	 * @throws java.lang.Exception
	 * @roseuid 3EB1BD3D033C
	 */
	public void insert() throws Exception
	{
		try {
			TestAircraftType 	aircraftType = new TestAircraftType();
			aircraftType.setType( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( "type", "" ) );

			// Testing to see if insert happens and the Payload_Vol is updated from a sequence
			// A sequence by the name TEMP_PAYLOAD_VOL_SEQ should be created for Oracle
			// For SQL Server a table named SEQUENCE_GENERATOR should be present with a row for TEMP_PAYLOAD_VOL_SEQ
			super.insert( aircraftType );
		}
		catch (java.sql.SQLException se) {

			System.out.println( "The error code is : " + se.getErrorCode() + " and the message is : " + se.getMessage() );
			System.out.println( "Translated Message is : " + Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().getSQLExceptionTranslator().translate( se ) );
			throw se;
		}
	}

	/**
	 * @throws java.lang.Exception
	 * @roseuid 3EB1C2C3006D
	 */
	public void update() throws Exception
	{
		try {
			TestAircraftType 	aircraftType = new TestAircraftType();
			aircraftType.setType( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( "type", "" ) );
			//aircraftType.setGroup( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( "group", "P" ) );
			aircraftType.setName( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( "name", "" ) );

			super.update( aircraftType );
		}
		catch (java.sql.SQLException se) {

			System.out.println( "The error code is : " + se.getErrorCode() + " and the message is : " + se.getMessage() );
			System.out.println( "Translated Message is : " + Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().getSQLExceptionTranslator().translate( se ) );
			throw se;
		}
	}

	/**
	 * @throws java.lang.Exception
	 * @roseuid 3EB1C2C7007D
	 */
	public void delete() throws Exception
	{
		try {
			TestAircraftType 	aircraftType = new TestAircraftType();
			aircraftType.setType( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( "type", "" ) );

			super.delete( aircraftType );
		}
		catch (java.sql.SQLException se) {

			System.out.println( "The error code is : " + se.getErrorCode() + " and the message is : " + se.getMessage() );
			System.out.println( "Translated Message is : " + Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().getSQLExceptionTranslator().translate( se ) );
			throw se;
		}
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EAEE63D000F
	 */
	protected String getSubsystem()
	{
		return _SUBSYSTEM;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EAEE63D001F
	 */
	protected String getServerType()
	{
		return Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().getServerType();
	}

	/**
	 * @param type
	 * @return com.addval.dbutils.DAOSQLStatement
	 * @roseuid 3EAEE63D0020
	 */
	protected DAOSQLStatement getDAOSQLStatement(int type)
	{
		DAOSQLLoader 	loader    = new DAOSQLLoader();
		DAOSQLStatement statement = null;
		String 		 	rulesFile = Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( DAOSQLLoader._RULES_URL, "DAORules.xml" );
		String 		 	sqlFile   = Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( DAOSQLLoader._SQL_URL, ""   );
		Hashtable 		sqls 	  = loader.loadSQL( rulesFile, sqlFile );

		switch (type) {

			case DAOManager._LOOKUP_SQL:
				statement = (DAOSQLStatement)sqls.get( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( _LOOKUP_SQL_NAME, "" ) );
				break;
			case DAOManager._INSERT_SQL:
				statement = (DAOSQLStatement)sqls.get( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( _INSERT_SQL_NAME, "" ) );
				break;
			case DAOManager._UPDATE_SQL:
				statement = (DAOSQLStatement)sqls.get( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( _UPDATE_SQL_NAME, "" ) );
				break;
			case DAOManager._DELETE_SQL:
				statement = (DAOSQLStatement)sqls.get( Environment.getInstance( _SUBSYSTEM ).getCnfgFileMgr().getPropertyValue( _DELETE_SQL_NAME, "" ) );
				break;
		}

		return statement;
	}

	/**
	 * @return java.sql.Connection
	 * @roseuid 3EB1682103B9
	 */
	protected Connection getConnection()
	{
		return Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().getConnection();
	}

	/**
	 * @param conn
	 * @roseuid 3EB1682103C8
	 */
	protected void releaseConnection(Connection conn)
	{
		Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().releaseConnection( conn );
	}
}
