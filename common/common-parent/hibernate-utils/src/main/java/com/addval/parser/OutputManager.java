//Source file: F:\\proj\\aerlingus\\source\\com\\addval\\parser\\OutputManager.java

//Source file: d:\\projects\\aerlingus\\source\\com\\addval\\parser\\OutputManager.java

//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\OutputManager.java

package com.addval.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Hashtable;
import java.sql.PreparedStatement;
import java.io.PrintWriter;
import com.addval.utils.XRuntime;
import com.addval.environment.Environment;
import com.addval.utils.CnfgFileMgr;
import com.addval.utils.LogFileMgr;

/**
Generalised class for doing output operations. Can handle output either to disk
files
or to database depending on the output type specified.
 */
public class OutputManager implements Constants
{
    private int _updateFrequency = - 1;
    private int _lineCount = 0;
    private String _projectName = null;
    private static final String _module = "OutputManager";
    private String _fileDir = null;
    private boolean _isClosed = true;
    private OrderedHashtable _preparedStatements = null;
    private Hashtable _fileWriters = null;
    private LogFileMgr _logFileMgr = null;


	public OutputManager()
    {
        // default constructor for Spring instantiation
    }
    /**
    @param projectName
    @throws com.addval.parser.InvalidInputException
    @roseuid 3E15528803D5
     */
    public OutputManager(String projectName) throws InvalidInputException
    {
        this( projectName, _FILE_OUTPUT );
    }

    /**
    @param projectName
    @param outputType
    @throws com.addval.parser.InvalidInputException
    @roseuid 3E155288032B
     */
    public OutputManager(String projectName, String outputType) throws InvalidInputException
    {
        this( projectName, null, null, outputType );
    }

    /**
    @param projectName
    @param prefixes
    @param outputType
    @param sqls
    @param prefixes[]
    @param sqls[]
    @throws com.addval.parser.InvalidInputException
    @roseuid 3DA3C61B0002
     */
    public OutputManager(String projectName, String prefixes[], String sqls[], String outputType) throws InvalidInputException
    {
		initialize(projectName, prefixes, sqls, outputType);
    }

	public void initialize(String projectName, String prefixes[], String sqls[], String outputType) throws InvalidInputException
    {
        Environment env = Environment.getInstance( projectName );
        _projectName = projectName;
        _logFileMgr = env.getLogFileMgr( _module );
        _updateFrequency = Integer.parseInt( env.getCnfgFileMgr().getPropertyValue( _UPDATE_FREQ, "1" ) );
        init( prefixes, sqls, outputType );
        _isClosed = false;
    }

    /**
    Access method for the _updateFrequency property.

    @return   the current value of the _updateFrequency property
     */
    public int getUpdateFrequency()
    {
        return _updateFrequency;
    }

    /**
    Sets the value of the _updateFrequency property.

    @param aUpdateFrequency the new value of the _updateFrequency property
     */
    public void setUpdateFrequency(int aUpdateFrequency)
    {
        _updateFrequency = aUpdateFrequency;
    }

    /**
    Access method for the _lineCount property.

    @return   the current value of the _lineCount property
     */
    public int getLineCount()
    {
        return _lineCount;
    }

    /**
    Sets the value of the _lineCount property.

    @param aLineCount the new value of the _lineCount property
     */
    public void setLineCount(int aLineCount)
    {
        _lineCount = aLineCount;
    }

    /**
    Access method for the _projectName property.

    @return   the current value of the _projectName property
     */
    public String getProjectName()
    {
        return _projectName;
    }

    /**
    Sets the value of the _projectName property.

    @param aProjectName the new value of the _projectName property
     */
    public void setProjectName(String aProjectName)
    {
        _projectName = aProjectName;
    }

    /**
    Access method for the _module property.

    @return   the current value of the _module property
     */
    public static String getModule()
    {
        return _module;
    }

    /**
    Access method for the _fileDir property.

    @return   the current value of the _fileDir property
     */
    public String getFileDir()
    {
        return _fileDir;
    }

    /**
    Sets the value of the _fileDir property.

    @param aFileDir the new value of the _fileDir property
     */
    public void setFileDir(String aFileDir)
    {
        _fileDir = aFileDir;
    }

    /**
    Determines if the _isClosed property is true.

    @return   <code>true<code> if the _isClosed property is true
     */
    public boolean getIsClosed()
    {
        return _isClosed;
    }

    /**
    Sets the value of the _isClosed property.

    @param aIsClosed the new value of the _isClosed property
     */
    public void setIsClosed(boolean aIsClosed)
    {
        _isClosed = aIsClosed;
    }

    /**
    Access method for the _preparedStatements property.

    @return   the current value of the _preparedStatements property
     */
    public OrderedHashtable getPreparedStatements()
    {
        return _preparedStatements;
    }

    /**
    Sets the value of the _preparedStatements property.

    @param aPreparedStatements the new value of the _preparedStatements property
     */
    public void setPreparedStatements(OrderedHashtable aPreparedStatements)
    {
        _preparedStatements = aPreparedStatements;
    }

    /**
    Access method for the _fileWriters property.

    @return   the current value of the _fileWriters property
     */
    public Hashtable getFileWriters()
    {
        return _fileWriters;
    }

    /**
    Sets the value of the _fileWriters property.

    @param aFileWriters the new value of the _fileWriters property
     */
    public void setFileWriters(Hashtable aFileWriters)
    {
        _fileWriters = aFileWriters;
    }

    /**
    Access method for the _logFileMgr property.

    @return   the current value of the _logFileMgr property
     */
    public LogFileMgr getLogFileMgr()
    {
        return _logFileMgr;
    }

    /**
    Sets the value of the _logFileMgr property.

    @param aLogFileMgr the new value of the _logFileMgr property
     */
    public void setLogFileMgr(LogFileMgr aLogFileMgr)
    {
        _logFileMgr = aLogFileMgr;
    }


    /**
    @param prefixes
    @param outputType
    @param sqls
    @param prefixes[]
    @param sqls[]
    @throws com.addval.parser.InvalidInputException
    @roseuid 3DA3C9530288
     */
    private void init(String prefixes[], String sqls[], String outputType) throws InvalidInputException
    {
        if (outputType.equalsIgnoreCase( _DB_OUTPUT )) {

            _preparedStatements = new OrderedHashtable();
            prepareStatements( prefixes, sqls);
            return;
        }
        if (outputType.equalsIgnoreCase( _FILE_OUTPUT )) {
            _fileWriters = new Hashtable();
            _fileDir = Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( _OUTPUT_DIR, System.getProperty( "user.dir" ) );
            String fileSeparator = System.getProperty( "file.separator" );
            if (!_fileDir.endsWith( fileSeparator ))
                _fileDir += fileSeparator;
            prepareFileWriters( prefixes );
            return;
        }
        throw new XRuntime( _module, "OutputType should be either " + _DB_OUTPUT + " or " + _FILE_OUTPUT );
    }

    /**
    @param prefix
    @return PreparedStatement
    @roseuid 3DA3DA3202E3
     */
    private PreparedStatement getStatement(String prefix)
    {
        return (PreparedStatement)_preparedStatements.get( prefix );
    }

    /**
    @param prefix
    @return PrintWriter
    @roseuid 3DA3DA51008E
     */
    private PrintWriter getFileWriter(String prefix)
    {
        return (PrintWriter)_fileWriters.get( prefix );
    }

    /**
    @roseuid 3DA3D58B0272
     */
    public void updateTables()
    {
        if (_isClosed)
            throw new XRuntime( _module, "OutputManager is in Closed state. Could not updateTables()!" );
        updateTables( false );
    }

    /**
    @param isEOF
    @roseuid 3DA4011C008E
     */
    public void updateTables(boolean isEOF)
    {
        if (_isClosed)
            throw new XRuntime( _module, "OutputManager is in Closed state. Could not updateTables()!" );
        if (_preparedStatements == null)
            return;
        _lineCount++;
        // if end of file, the update statements are executed unconditionally
        // else per update frequency only the statements are executed
        if (!isEOF && _lineCount % _updateFrequency != 0)
            return;
        PreparedStatement[] pstmts = (PreparedStatement[])_preparedStatements.toValueArray( new PreparedStatement[_preparedStatements.size()] );
		DBConnMgr.executeBatch( pstmts );
   //     _logFileMgr.logInfo( "Update Count " + _lineCount );
//        System.out.println( "Update Count " + _lineCount );
        // if it is completing the output operations, close it.
        if (isEOF)
            close();
    }

    /**
    @roseuid 3DA40E6D02DF
     */
    private void close()
    {
        closeStatements();
        closeFileWriters();
        _isClosed = true;
    }

    /**
    @roseuid 3DA40FBE0059
     */
    private void closeStatements()
    {
        if (_isClosed || _preparedStatements == null)
            return;
        for (Iterator iter = _preparedStatements.values().iterator(); iter.hasNext();) {
            try {
                ((PreparedStatement)iter.next()).close();
            }
            catch(SQLException sqle) {
                _logFileMgr.logWarning( "Error while closing PreparedStatements" + sqle );
            }
        }
    }

    /**
    @roseuid 3DA40FCF0158
     */
    private void closeFileWriters()
    {
        if (_isClosed || _fileWriters == null)
            return;
        for (Iterator iter = _fileWriters.values().iterator(); iter.hasNext();) {
            PrintWriter writer = (PrintWriter)iter.next();
            writer.flush();
            writer.close();
            if (writer.checkError())
                _logFileMgr.logWarning( "Error while closing files " );
        }
    }

    /**
    @param prefixes
    @param sqls

    @param prefixes[]
    @param sqls[]
    @roseuid 3E1552890138
     */
    public void prepareStatements(String prefixes[], String sqls[])
    {
        if (_preparedStatements != null && prefixes != null)
            for (int i=0; i<prefixes.length; i++)
                prepareStatement(prefixes[i], sqls[i]);
    }

    /**
    @param prefix
    @param sql
    @roseuid 3E1552890188
     */
    public void prepareStatement(String prefix, String sql)
    {
        if (_preparedStatements != null && prefix != null && sql != null) {
    		_preparedStatements.put( prefix, DBConnMgr.getPreparedStatement( sql, _projectName ) );
		}
    }

    /**
    @param prefixes

    @param prefixes[]
    @roseuid 3E15528901E2
     */
    public void prepareFileWriters(String prefixes[])
    {
        if (_fileWriters != null && prefixes != null)
            for (int i=0; i<prefixes.length; i++)
                prepareFileWriter( prefixes[i] );
    }

    /**
    @param prefix
    @roseuid 3E155289021E
     */
    public void prepareFileWriter(String prefix)
    {
        if (_fileWriters == null || prefix == null)
            return;
        String fileName = _fileDir + prefix + ".DAT";
        try {
            _fileWriters.put( prefix, new PrintWriter( new BufferedWriter( new FileWriter( fileName ) ) ) );
        }
        catch(IOException ioe) {
            throw new XRuntime( _module, "Error while Preparing fileWriter for file name " + fileName + "\n" + ioe );
        }
    }

    /**
    @param prefix
    @param columnNames
    @param columnTypes
    @param parsedValues

    @param columnNames[]
    @param columnTypes[]
    @roseuid 3E1552890264
     */
    public void setData(String prefix, String columnNames[], String columnTypes[], java.util.Hashtable parsedValues)
    {
        if (_isClosed)
            throw new XRuntime( _module, "OutputManager is in Closed state. Could not setData!" );
        if (prefix == null || columnNames == null || columnTypes == null || parsedValues == null || parsedValues.isEmpty())
            return;
        setStatement ( prefix, columnNames, columnTypes, parsedValues );
        setFileOutput( prefix, columnNames, columnTypes, parsedValues );
    }

    /**
    @param prefix
    @param columnNames
    @param columnTypes
    @param parsedValues

    @param columnNames[]
    @param columnTypes[]
    @roseuid 3E15528902F0
     */
    private void setStatement(String prefix, String columnNames[], String columnTypes[], java.util.Hashtable parsedValues)
    {
        if (_preparedStatements == null)
            return;
        PreparedStatement pStatement = getStatement( prefix );
        if (pStatement == null)
            return;
		DBConnMgr.prepareStatement( pStatement, columnNames, columnTypes, parsedValues );
    }

    /**
    @param prefix
    @param columnNames
    @param columnTypes
    @param parsedValues

    @param columnNames[]
    @param columnTypes[]
    @roseuid 3E155289039B
     */
    private void setFileOutput(String prefix, String columnNames[], String columnTypes[], java.util.Hashtable parsedValues)
    {
        if (_fileWriters == null)
            return;
        int count = columnNames.length;
        if (count == 0)
            return;
        PrintWriter writer = getFileWriter( prefix );
        if (writer == null)
            return;
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<count; i++) {
            String value = (String)parsedValues.get( columnNames[i] );
            if (value == null)
                value = "";
            if (value.equals( "" ) && (columnTypes[i].equals( _NUMBER ) || columnTypes[i].equals( _DECIMAL )))
                value = "0";
            buffer.append( _COMMA ).append( value );
        }
        writer.println( buffer.substring( 1 ) );
        if (writer.checkError())
            throw new XRuntime( _module, "Error while writing to file." );
    }
}
