package com.addval.exporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.addval.environment.Environment;
import com.addval.springutils.ClientRegistry;
import com.addval.utils.CommandLine;
import com.addval.utils.StrUtl;

/**
 * Export the data as CSV Feed
 *
 *  For CSV feed generation,properties needed for this class had been initialized by passing property file name as an argument to initialize() method.
 *
 *  _propertyFileName 	- property file name for the loader from which the following property had been initialized
 *
 *  _SUBSYSTEM	 		- defaultly we had been used as 'cargores'
 *  csvFeedPath - csv feed file name with path
 *  feedQuery	- saves sqlquery used for generating the CSV feed
 *  delimiter 	- ',' is used as delimiter
 *
 */
public class DBExporter
{
	private String _SUBSYSTEM 	= null;
	private static String _propertyFileName = null;
	private String _outputFile 			= null;
	private String _sql 				= null;
	private String _delimiter 			= null;

	private SimpleDateFormat _defaultFormatter       = null;
	private DecimalFormat    _defaultNumberFormatter = null;

	/**
	 * _isheader - if true,add column names as header in the csv feed
	 */
	private boolean _header 			= false;

	/**
	 * _dateFormatCheck - if true,it parse date field in given format
	 */
	private boolean _dateFormatCheck 	= false;

	/**
	 * _numberFormatCheck - if true,it formats double field in given format
	 */
	private boolean _numberFormatCheck 	= false;

	/**
	 * @exception
	 * @roseuid 3AA677E0005B
	 */
	public DBExporter()
	{
	}

	/**
	 * Method which determines whether to generate the feed as file or in console
	 * @throws Exception
	 */
	public final void export() throws Exception
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet 	rs   = null;
		try {
			conn = Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().getConnection();
			stmt = conn.createStatement();
			stmt.executeQuery( getSql() );
			rs = stmt.getResultSet();

			if (getOutputFile() == null || getOutputFile().length() == 0)
				exportOut( rs );
			else
				exportFile( rs );
		}
		finally {
			if(rs!= null)
				rs.close();
			if(stmt!= null)
				stmt.close();
			Environment.getInstance( _SUBSYSTEM ).getDbPoolMgr().releaseConnection( conn );
		}

	}
	/**
	 * Method is used to generate the feed as file
	 * @param rs
	 * @throws Exception
	 */
	private void exportFile( ResultSet rs ) throws Exception
	{
		File file = new File( getOutputFile() );

		// Create if the file does not exist
		file.createNewFile();

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();

			int i;
			// boolean value _header checks whether to add column names as header in first row of the feed
			if (_header){
				for (i=1; i <= numberOfColumns; i++ ) {
					if (i != 1)
						writer.write( getDelimiter() );

					if (rsmd.getColumnName( i ) != null)
						writer.write( rsmd.getColumnName( i ) );
				}
				writer.newLine();
			}

			while (rs.next()) {
				for (i=1; i <= numberOfColumns; i++ ) {
					if (i != 1)
						writer.write( getDelimiter() );
					// boolean value _dateFormatCheck and _numberFormatcheck - checks the particular column is date or double,and convert it into specified format
					if (_dateFormatCheck && (rsmd.getColumnType( i ) == Types.DATE || rsmd.getColumnType( i ) == Types.TIMESTAMP))
							writer.write( parseDate( rs.getDate( i ) ) );
					else if ( _numberFormatCheck && rsmd.getColumnType( i ) == Types.NUMERIC)
							writer.write( parseNumber( rs.getDouble( i )));
					else if (rs.getString(i) != null)
							writer.write( rs.getString( i ) );
				}
				writer.newLine();
			}
		}
		finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	/**
	 *Method is to prints the data in console
	 * @param rs
	 * @throws Exception
	 */
	private void exportOut( ResultSet rs ) throws Exception
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		int i;

		while (rs.next()) {
			for (i=1; i <= numberOfColumns; i++ ) {
				if ( i != 1 )
					System.out.print( getDelimiter() );

				if (rs.getString(i) != null)
					System.out.print( rs.getString( i ) );
			}
			System.out.println();
		}
	}

	/**
	 * Method is to read the sql query from the file,which we passed as an argument
	 * @param aSqlFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void setSqlFile(String aSqlFile) throws IOException
	{
		BufferedReader input = null;
		try {
			input = new BufferedReader( new FileReader(aSqlFile) );
			String line = input.readLine();
			StringBuffer sql = new StringBuffer();
			while (line != null) {
				sql.append( line );
				line = input.readLine(); //read the next line
			}//while
			setSql( sql.toString() );
		}
		finally {
			if (input != null)
				input.close();
		}
	}


	public String getOutputFile()
	{
		return _outputFile;
	}

	public void setOutputFile(String aOutputFile)
	{
		_outputFile = aOutputFile;
	}

	public String getSql()
	{
		return _sql;
	}

	public void setSql(String aSql)
	{
		_sql = aSql;
	}

	public String getDelimiter()
	{
		return _delimiter;
	}

	public void setDelimiter(String aDelimiter)
	{
		_delimiter = aDelimiter;
	}

	public void setHeader(boolean header){
		_header = header;
	}

	public void setDateCheck(boolean checkdate){
		_dateFormatCheck = checkdate;
	}

	public void setDoubleCheck(boolean checkdouble){
		_numberFormatCheck = checkdouble;
	}

	/** Method is to parse the date in given format */
	public String parseDate(java.sql.Date date)
	{
		if(date != null)
			return _defaultFormatter.format( date );
		else
			return "";
	}

	/** Method is parse the double in given format */
	public String parseNumber(double number)
	{
		return _defaultNumberFormatter.format( number );
	}

	public static void printUsage()
	{
		System.err.println( "Usage : DBexporter [-S <subsystem>] [-d <delimiter>] -o <outFile> -s <sql> -f <sql_file> -b <springBeanName> -l <beanLocatorKey>" );
	}

	/**
	 * Method is called one time,which is to initializes the properties for generating csv feed
	 *
	 **/
	public void initialize(String fileName) throws Exception
	{
		_propertyFileName = fileName;

		_SUBSYSTEM = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getPropertyValue( "env.cacheName", "cargores");
		_delimiter = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getPropertyValue( "delimiter" ,  ",");

		if(_sql == null )
			_sql=Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getPropertyValue( "feedQuery", null);

		if( StrUtl.isEmpty( _sql ))
			throw new Exception( "No SQL specified for querying the STAGE Table " );

		if(_outputFile == null)
			_outputFile = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getPropertyValue( "csvFeedPath", null );

		if( StrUtl.isEmpty( _outputFile ))
			throw new Exception( " Output CSV file path is not specified " );

		_header = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getBoolValue( "header",false );

		_dateFormatCheck = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getBoolValue( "dateFormatCheck",false);
		String dateFormat = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getPropertyValue( "dateFormat", "yyyyMMdd");
		_defaultFormatter = new SimpleDateFormat( dateFormat );

		_numberFormatCheck = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getBoolValue( "numberFormatCheck" , false );
		String numberFormat = Environment.getInstance( _propertyFileName ).getCnfgFileMgr().getPropertyValue( "numberFormat", "##########.###" );
		_defaultNumberFormatter = new DecimalFormat( numberFormat );
	}

	public static void main (String [] args) throws Exception
	{
		DBExporter exporter = null;
		try{
			System.err.println( "Start time:" + new Date() );

			CommandLine cl = new CommandLine( args );

			// if injected in Spring configuration use this flag
			if( cl.hasFlag("b")) {
				ClientRegistry registry = new ClientRegistry();
				if( cl.hasFlag("l")) {
					registry.setBeanFactoryLocatorKey(cl.getFlagArgument( "l" ));
				}
				exporter = (DBExporter) registry.getBean(cl.getFlagArgument( "b" ));
			}
			else{
				// non-Spring usage
				exporter = new DBExporter( );
			}
			if ( cl.hasFlag( "S" )) {
				_propertyFileName = cl.getFlagArgument( "S" );
			}

			if ( cl.hasFlag( "d" ) ) {
				exporter.setDelimiter( cl.getFlagArgument( "d" ) );
			}

			if ( cl.hasFlag( "o" ) ) {
				exporter.setOutputFile( cl.getFlagArgument( "o" ) );
			}

			if ( cl.hasFlag( "f" ) ) {
				exporter.setSqlFile( cl.getFlagArgument( "f" ) );
			}
			else if ( cl.hasFlag( "s" ) ) {
				exporter.setSql( cl.getFlagArgument( "s" ) );
			}
			else {
				printUsage();
				throw new Exception("No SQL or SQL-file specified");
			}
			if( cl.hasFlag("h")){
				exporter.setHeader( Boolean.getBoolean(cl.getFlagArgument( "h" )));
			}
			if(cl.hasFlag( "D" )){
				exporter.setHeader( Boolean.getBoolean(cl.getFlagArgument( "D" )));
			}
			if(cl.hasFlag( "n" )){
				exporter.setHeader( Boolean.getBoolean(cl.getFlagArgument( "n" )));
			}

			exporter.initialize( _propertyFileName );
			exporter.export();

			System.err.println( "End time:" + new Date() );
		}
		catch (Exception e){
			System.out.println( "Exception is :"+e );

		}
	}
}
