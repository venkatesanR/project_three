//Source file: d:\\projects\\aerlingus\\source\\com\\addval\\parser\\AdapterMsgParser.java

package com.addval.parser;

import com.addval.environment.Environment;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Friendly Adapter class for data parsing and output. Splits, validates and
 * prepares the text input.
 * As the text lines are parsed, the Hashtable is
 * filled with the Database column names and corresponding values.
 * The subClasses has to take care to call setParsedValues() and setData() methods
 * and may implement the following methods.
 * a) preParse(), b)clear(), c)postParse(), d)setCnstantValues() and may create
 * dataObjcts as needed, if any.
 */
public abstract class AdapterMsgParser extends MsgParser implements Constants
{
    private static final String _module = "AdapterMsgParser";
    private String _outputType = null;
    private Hashtable _constantDBValues = null;
    private static final SimpleDateFormat _sdfOutDate = new SimpleDateFormat (_SQL_DATE_FORMAT);
    private static final SimpleDateFormat _sdfOutTime = new SimpleDateFormat (_SQL_TIME_FORMAT);
    private static final SimpleDateFormat _sdfOutTimestamp = new SimpleDateFormat (_SQL_DATE_TIME_FORMAT);
    private OutputManager _outputManager = null;
	private Environment _environment;

    /**
     * adapter implementation of MsgParser class. Takes care of Output creation
     * operations.
     * The actual parsing class has to extend this to implemnt the parseLine() and
     * setConstantValues() methods and prepare the data object, if any.
     * @param projectName
     * @param prop
     * @param prefixes
     * @param prefixes[]
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1525120023
     */

	public AdapterMsgParser()
    {
        super();
    }
    public AdapterMsgParser(String projectName, String fsmName, String prefixes[]) throws InvalidInputException
    {
        super( fsmName );
        // do the one time initialisation
        init( projectName, fsmName, prefixes );
    }

    /**
     * Access method for the _module property.
     *
     * @return   the current value of the _module property
     */
    public static String getModule()
    {
        return _module;
    }

    /**
     * Access method for the _outputType property.
     *
     * @return   the current value of the _outputType property
     */
    public String getOutputType()
    {
        return _outputType;
    }

    /**
     * Sets the value of the _outputType property.
     *
     * @param aOutputType the new value of the _outputType property
     */
    public void setOutputType(String aOutputType)
    {
        _outputType = aOutputType;
    }

    /**
     * Access method for the _constantDBValues property.
     *
     * @return   the current value of the _constantDBValues property
     */
    public Hashtable getConstantDBValues()
    {
        return _constantDBValues;
    }

    /**
     * Sets the value of the _constantDBValues property.
     *
     * @param aConstantDBValues the new value of the _constantDBValues property
     */
    public void setConstantDBValues(Hashtable aConstantDBValues)
    {
        _constantDBValues = aConstantDBValues;
    }

    /**
     * Access method for the _sdfOutDate property.
     *
     * @return   the current value of the _sdfOutDate property
     */
    public static SimpleDateFormat getSdfOutDate()
    {
        return _sdfOutDate;
    }

    /**
     * Access method for the _sdfOutTime property.
     *
     * @return   the current value of the _sdfOutTime property
     */
    public static SimpleDateFormat getSdfOutTime()
    {
        return _sdfOutTime;
    }

    /**
     * Access method for the _sdfOutTimestamp property.
     *
     * @return   the current value of the _sdfOutTimestamp property
     */
    public static SimpleDateFormat getSdfOutTimestamp()
    {
        return _sdfOutTimestamp;
    }

    /**
     * Access method for the _outputManager property.
     *
     * @return   the current value of the _outputManager property
     */
    public OutputManager getOutputManager()
    {
        return _outputManager;
    }

    /**
     * Sets the value of the _outputManager property.
     *
     * @param aOutputManager the new value of the _outputManager property
     */
    public void setOutputManager(OutputManager aOutputManager)
    {
        _outputManager = aOutputManager;
    }

    /**
     * givesout the constantvalues, in any, which persist between parsing different
     * lines
     * @return java.util.Hashtable
     * @roseuid 3E15251202E0
     */
    protected Hashtable getConstantValues()
    {
        return _constantDBValues;
    }

    /**
     * friendly method to call OutPutManager's UpdateTables() method
     * shields the OutputManager from child implementations
     * @roseuid 3E1525120312
     */
    protected void updateTables()
    {
        updateTables( false );
    }

    /**
     * friendly method to call OutPutManager's UpdateTables() method
     * shields the OutputManager from child implementations
     * @param isEOF
     * @roseuid 3E1525120326
     */
    protected void updateTables(boolean isEOF)
    {
        _outputManager.updateTables( isEOF );
    }

    /**
     * method for one time initialisation of constant values that are to be inserted
     * into database, which are independant of text line data.
     * @param prop
     * @param projectName
     * @param prefixes
     * @param prefixes[]
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1525120344
     */
    public void init(String projectName, String fsmName, String prefixes[]) throws InvalidInputException
    {
        // prepare the OutputManager
        String[] sqls = null;
        if( getCnfgFileMgr() != null )
        	_outputType = getCnfgFileMgr().getPropertyValue( _OUTPUT_TYPE, Constants._FILE_OUTPUT );
        else
			_outputType = Environment.getInstance( projectName ).getCnfgFileMgr().getPropertyValue( _OUTPUT_TYPE, Constants._FILE_OUTPUT );
        if (_outputType.equals( Constants._DB_OUTPUT )) {
            sqls = new String[ prefixes.length ];
            for (int i =0; i<prefixes.length; i++)
                sqls[i] = ((LineMetaData)getLineTypeWithType( prefixes[i] )).getSQL();
        }

        if( getOutputManager() == null )
			_outputManager = new OutputManager();

		_outputManager.initialize( projectName, prefixes, sqls, _outputType);

        setConstantValues( new Hashtable() );
        com.addval.utils.CnfgFileMgr cnfg = new com.addval.utils.CnfgFileMgr( fsmName );
        Properties prop = cnfg.getProperties();
        setConstantValues( prop );
    }

    /**
     * method to ensure that the date format is as "mm/dd/yyyy" for properly setting
     * the date to PreparedStatement
     * @param lineType
     * @param columnNames
     * @param columnTypes
     * @param columnNames[]
     * @param columnTypes[]
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15251203E5
     */
    protected void setDateFormat(LineType lineType, String columnNames[], String columnTypes[]) throws InvalidInputException
    {
        LineMetaData lineMetaData = (LineMetaData)lineType;
        if (lineMetaData.hasDateFormatError())
            throw new InvalidInputException( "Date format is not specified in property file, though the line content has atleast one  DATE/ DATETIME/ TIMESTAMP type variable - line Type - " + lineMetaData.getType() );
        String dateFormat = lineMetaData.getDateFormat();
        if (dateFormat == null)
            return;
        try {
            // the format expected by java.sql.Date
            SimpleDateFormat sdfIn  = new SimpleDateFormat( dateFormat );
            for (int j=0; j<columnNames.length; j++) {
                String value = getParsedValue( columnNames[j] );
                if (Utils.isNullOrEmpty( value ))
                    continue;
                if (columnTypes[j].equals( Constants._DATE ))
                    setParsedValue( columnNames[j], _sdfOutDate.format( sdfIn.parse( value ) ) );
                else if (columnTypes[j].equals( _DATE_TIME ))
                    setParsedValue( columnNames[j], _sdfOutTime.format( sdfIn.parse( value ) ) );
                else if (columnTypes[j].equals( _TIMESTAMP ))
                    setParsedValue( columnNames[j], _sdfOutTimestamp.format( sdfIn.parse( value ) ) );
            }
        }
        catch(ParseException pe) {
            throw new InvalidInputException( "Error while converting date! " + pe );
        }
    }

    /**
     * friendly method to set the Data for a particular lineType.
     * calls the undelying OutputManagers's setData method.
     * this method shields the Outputmanager from the child implementations
     * @param lineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15251300BB
     */
    protected void setData(LineType lineType) throws InvalidInputException
    {
        LineMetaData lineMetaData = (LineMetaData)lineType;
        String[] columnNames = lineMetaData.getDBColumns();
        String[] columnTypes = lineMetaData.getColumnTypes();
        // correct the date values suitable for PreparedStatements, in case the output is set to DATABASE
        if (_outputType.equals( Constants._DB_OUTPUT ))
            setDateFormat( lineType, columnNames, columnTypes );
        _outputManager.setData( lineType.getType(), columnNames, columnTypes, getParsedValues() );
    }

    /**
     * individual constant values, in any, which persist between parsing different
     * lines
     * are set using this method.
     * @param constantValues
     * @roseuid 3E152513015B
     */
    protected void setConstantValues(java.util.Hashtable constantValues)
    {
        _constantDBValues = constantValues;
    }

    /**
     * a set of constant values, which persist between parsing different lines
     * are added to the existing values using this method.
     * @param constantValues
     * @roseuid 3E1525130179
     */
    protected void addConstantValues(java.util.Hashtable constantValues)
    {
        _constantDBValues.putAll( constantValues );
    }

    /**
     * individual constant values, in any, which persist between parsing different
     * lines
     * are set using this method.
     * @param key
     * @param value
     * @roseuid 3E15251301A1
     */
    protected void setConstantValue(String key, String value)
    {
        if (key != null && value != null)
            _constantDBValues.put( key, value );
    }

	public void setEnvironment(Environment environment) {
        _environment = environment;
    }

    public Environment getEnvironment() {
        return _environment;
    }

   /**
     * The Constant values needed are to be set based on the needs of the child
     * implementations
     * @param prop
     * @roseuid 3E15251301D3
     */
    protected abstract void setConstantValues(java.util.Properties prop);
}
