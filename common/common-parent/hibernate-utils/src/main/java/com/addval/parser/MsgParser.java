//Source file: d:\\projects\\common\\source\\com\\addval\\parser\\MsgParser.java

package com.addval.parser;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.addval.utils.CnfgFileMgr;


/**
 * Base Abstract class to do the parsing of text lines.
 * Modified to cater to the needs of parsing various types of input files.
 * Most of the times, only the parseLine method alone is to be implemented by a
 * subclass.
 */
public class MsgParser
{
    private long _lineCount = 0;
    private boolean _isStarted = false;
    private Hashtable _parsedValues = null;
    private MessageFiniteStateMachine _fsm = null;
    private LineType _prevLineType = null;
    private boolean _ignoreUnknownLines = false;
    private String _module = "MsgParser";
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(MsgParser.class);
	private java.util.Properties _fsmProperties = null;
	private CnfgFileMgr _cnfgFileMgr = null;

    /**
     * temperorary variable to measure performance
     */
    protected long _lineTypeTime = 0;
    protected long _parseTime = 0;

    /**
     * constructor to be called from child implementation, when the LineType object to
     * be created is to be specified.
     * @param projectName
     * @param lineTypeName
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15521402B6
     */
    protected MsgParser(String projectName, String lineTypeName) throws InvalidInputException
    {
        initialize(projectName,lineTypeName);
    }

    protected void initialize(String projectName, String lineTypeName) throws InvalidInputException
    {
        _parsedValues = new Hashtable();
		_fsmProperties = getCnfgFileMgr().getProperties();
        setFiniteStateMachine(new MessageFiniteStateMachine( getCnfgFileMgr().getProperties(), lineTypeName));
        _ignoreUnknownLines = getCnfgFileMgr().getBoolValue("ignoreUnknownLines", false );
    }

	protected void setFsmProperties(java.util.Properties props) {
		_fsmProperties = props;

        _parsedValues = new Hashtable();

        String value = _fsmProperties.getProperty( "ignoreUnknownLines");
        if (value == null) value = "false";
        _ignoreUnknownLines = value.equalsIgnoreCase( "true" );
	}

	protected java.util.Properties getFsmProperties() {
		return _fsmProperties;
	}


    protected MsgParser()
    {

    }

    /**
     * constructor to be called from child implementation, when the LineType object to
     * be created is either specified in the properties file or the default LineType
     * object
     * @param projectName
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1552140220
     */
    public MsgParser(String projectName) throws InvalidInputException
    {
        this( projectName, null );
    }

    /**
     * Access method for the _lineCount property.
     *
     * @return   the current value of the _lineCount property
     */
    public long getLineCount()
    {
       return _lineCount;
    }

    /**
     * Sets the value of the _lineCount property.
     *
     * @param aLineCount the new value of the _lineCount property
     */
    public void setLineCount(long aLineCount)
    {
        _lineCount = aLineCount;
    }

    /**
     * Determines if the _isStarted property is true.
     *
     * @return   <code>true<code> if the _isStarted property is true
     */
    public boolean getIsStarted()
    {
        return _isStarted;
    }

    /**
     * Sets the value of the _isStarted property.
     *
     * @param aIsStarted the new value of the _isStarted property
     */
    public void setIsStarted(boolean aIsStarted)
    {
        _isStarted = aIsStarted;
    }

    /**
     * Access method for the _parsedValues property.
     *
     * @return   the current value of the _parsedValues property
     */
    public Hashtable getParsedValues()
    {
        return _parsedValues;
    }

    /**
     * Sets the value of the _parsedValues property.
     *
     * @param aParsedValues the new value of the _parsedValues property
     */
    public void setParsedValues(Hashtable aParsedValues)
    {
        _parsedValues.putAll( aParsedValues );
    }

    /**
     * Access method for the _prevLineType property.
     *
     * @return   the current value of the _prevLineType property
     */
    public LineType getPrevLineType()
    {
        return _prevLineType;
    }

    /**
     * Sets the value of the _prevLineType property.
     *
     * @param aPrevLineType the new value of the _prevLineType property
     */
    public void setPrevLineType(LineType aPrevLineType)
    {
        _prevLineType = aPrevLineType;
    }

    /**
     * External function called to parse a message.  Derived classes should not need
     * to override this implementation.
     * internally invokes the overloaded method to parse the individual lines in the
     * collection
     * @param msgStr
     * @throws InvalidInputException
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3D6B0B240056
     */
    public void parse(String msgStr) throws InvalidInputException
    {
        preParse(); //allow child implementations to do actions prior to parsing
        clear(); //allow child implementations to clear state and be ready to parse new message
        long startTime = System.currentTimeMillis();
        parse( msgStr, System.getProperty( "line.separator" ) );
        _parseTime += System.currentTimeMillis() - startTime;
        postParse();//allow child implementations to do actions after parsing
    }
    
    /**
     * External function called to parse a message.  Derived classes should not need
     * to override this implementation.
     * internally invokes the overloaded method to parse the individual lines in the
     * collection
     * @param msgStr
     * @throws InvalidInputException
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3D6B0B240056
     */
    public void parse(String msgStr, Object parsedDoc) throws InvalidInputException
    {
        preParse(); //allow child implementations to do actions prior to parsing
        clear(); //allow child implementations to clear state and be ready to parse new message
        long startTime = System.currentTimeMillis();
        parse( msgStr, System.getProperty( "line.separator" ), parsedDoc);
        _parseTime += System.currentTimeMillis() - startTime;
        postParse();//allow child implementations to do actions after parsing
    }

    /**
     * provisional method for doing any pre parsing activity for different child
     * implementations. Default implementation does not do anything.
     * @roseuid 3DA3B48D0213
     */
    protected void preParse()
    {

    }

    /**
     * provisional method for doing any post parsing activity for different child
     * implementations. Default implementation does not do anything.
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3DA3B4A002F7
     */
    protected void postParse() throws InvalidInputException
    {

    }

    /**
     * Derived classes should override this method to clear any state and be ready to
     * start parsing a new message
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3D6B0B240151
     */
    protected void clear() throws InvalidInputException
    {
        setPrevLineType(null);
    }

    /**
     * Child classes should override this to parse a new line , in a different manner
     * @param line
     * @param lineType
     * @throws InvalidInputException
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3D6B0B240165
     */
    protected void parseLine(String line, LineType lineType) throws InvalidInputException
	{
		setParsedValues( lineType.getValues( line, lineType.getLineColumns() ));
	}
    
    /**
     * Child classes should override this to parse a new line , in a different manner
     * @param line
     * @param lineType
     * @throws InvalidInputException
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3D6B0B240165
     */
    protected void parseLine(String line, LineType lineType, Object parsedDoc) throws InvalidInputException
	{
    	parseLine(line, lineType);
	}

    /**
     * @return com.addval.utils.LogFileMgr
     * @roseuid 3E40AC230155
     */
    protected Logger getLogger()
    {
        return _logger;
    }

    /**
     * method to process a single line.
     * This method is used when a bunch of lines delimited by a delimiter are to be
     * parsed.
     * @param line
     * @param delimiter
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1552150041
     */
    public void parse(String line, String delimiter) throws InvalidInputException
    {
//        final String source = "MsgParser.parse";
        if (Utils.isNullOrEmpty( line ))
            return;
        if (delimiter == null)
            delimiter = System.getProperty( "line.separator" );
        StringTokenizer lines = new StringTokenizer( line, delimiter );
        while (lines.hasMoreTokens()) {
            _lineCount++;
            if ((line = lines.nextToken()).equals( "" ))
                continue;
            long startTime = System.currentTimeMillis();
            LineType lineType = getNextLineType( line, getPrevLineType() );
            _lineTypeTime += System.currentTimeMillis() - startTime;
            // get line type from FSM
            if (lineType == null) {
                if (!_ignoreUnknownLines){
                    throw new InvalidInputException( "Unknown/unexpected line: " + line );
                }
                getLogger().info("MsgParser.parse" + "Unknown/unexpected line: " + line );           
                continue;
            }
            // if it is not a comment line, parse it further
            if (lineType.isCommentLine())
                continue;
            parseLine( line, lineType );
            if (!_isStarted)
                _isStarted = true;
            // save previous line type, if it is not a comment.
            // i.e. a comment line will never be the previous line type
            // FSM might need it for the next line
            setPrevLineType( lineType );
            if (!isDone())
                throw new InvalidInputException( "Unexpected end of message after " + getPrevLineType() );
        }
    }
    
    /**
     * method to process a single line.
     * This method is used when a bunch of lines delimited by a delimiter are to be
     * parsed.
     * @param line
     * @param delimiter
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1552150041
     */
    public void parse(String line, String delimiter, Object parsedDoc) throws InvalidInputException
    {
//        final String source = "MsgParser.parse";
        if (Utils.isNullOrEmpty( line ))
            return;
        if (delimiter == null)
            delimiter = System.getProperty( "line.separator" );
        StringTokenizer lines = new StringTokenizer( line, delimiter );
        while (lines.hasMoreTokens()) {
            _lineCount++;
            if ((line = lines.nextToken()).equals( "" ))
                continue;
            long startTime = System.currentTimeMillis();
            LineType lineType = getNextLineType( line, getPrevLineType() );
            _lineTypeTime += System.currentTimeMillis() - startTime;
            // get line type from FSM
            if (lineType == null) {
                if (!_ignoreUnknownLines){
                    throw new InvalidInputException( "Unknown/unexpected line: " + line );
                }
                getLogger().info("MsgParser.parse" + "Unknown/unexpected line: " + line );           
                continue;
            }
            // if it is not a comment line, parse it further
            if (lineType.isCommentLine())
                continue;
            parseLine( line, lineType , parsedDoc);
            if (!_isStarted)
                _isStarted = true;
            // save previous line type, if it is not a comment.
            // i.e. a comment line will never be the previous line type
            // FSM might need it for the next line
            setPrevLineType( lineType );
            if (!isDone())
                throw new InvalidInputException( "Unexpected end of message after " + getPrevLineType() );
        }
    }

    /**
     * sets the FiniteStateMachine Object. Mostly used internally.
     * @param fsm
     * @roseuid 3E15521500EB
     */
    protected void setFiniteStateMachine(MessageFiniteStateMachine fsm)
    {
        if (_fsm == null)
            _fsm = fsm;
    }

    /**
     * returns the FiniteStateMachine Object. Mostly used internally.
     * @return com.addval.parser.MsgFiniteStateMachine
     * @roseuid 3E155215013B
     */
    protected MessageFiniteStateMachine getFiniteStateMachine()
    {
        return _fsm;
    }

    /**
     * @param line
     * @return LineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1552150177
     */
    public LineType getLineType(String line) throws InvalidInputException
    {
        return getFiniteStateMachine().getLineType( line );
    }

    /**
     * friendly method to get the initital lineType object
     * shields the FiniteStateMachine from child implementations
     * @return com.addval.parser.LineType
     * @roseuid 3E15521502C2
     */
    public LineType getInitialLineType()
    {
        return getFiniteStateMachine().getInitialLineType();
    }

    /**
     * friendly method to get the lineType object specifying the input text line
     * shields the FiniteStateMachine from child implementations
     * @param line
     * @return com.addval.parser.LineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15521502F4
     */
    public LineType getNextLineType(String line) throws InvalidInputException
    {
        return getNextLineType( line, null );
    }

    /**
     * method useful when the previous lineType is specified as there is order in the
     * text input lines
     * for the very first line of input, the prevLineType would be null.
     * for subsequent lines, the prevLineType would be the previously processed
     * lineType
     * shields the FiniteStateMachine from child implementations
     * @param line
     * @param prevLineType
     * @return com.addval.parser.LineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E155215039E
     */
    public LineType getNextLineType(String line, LineType prevLineType) throws InvalidInputException
    {
        return getFiniteStateMachine().getNextLineType( line, prevLineType );
    }

    /**
     * method useful to retrieve the LineType with the type(state or prefix) variable
     * shields the FiniteStateMachine from child implementations
     * @param type
     * @return com.addval.parser.LineType
     * @roseuid 3E155216009C
     */
    public LineType getLineTypeWithType(String type)
    {
         return getFiniteStateMachine().getLineTypeWithType( type );
    }

    /**
     * returns the value corresponding to the input key field, if any
     * @param columnName
     * @return String
     * @roseuid 3E1552160132
     */
    public String getParsedValue(String columnName)
    {
        if (columnName == null)
            return null;
        return (String)_parsedValues.get( columnName );
    }

    /**
     * @param key
     * @return java.lang.String
     * @roseuid 3E3F63EC034A
     */
    public String removeParsedValue(String key)
    {
     return null;
    }

    /**
     * clear the contents of the parsedValues. Useful between subsequent prase() calls
     * @roseuid 3E155216016E
     */
    public void clearParsedValues()
    {
        _parsedValues.clear();
    }

    /**
     * useful method when the clumnsNames and values are available as arrays to set
     * the Hashtable
     * @param columnNames
     * @param columnValues
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E1552160223
     */
    public void setParsedValues(java.lang.String[][] columnNames, java.lang.String[][] columnValues) throws InvalidInputException
    {
        if (Utils.isNullOrEmpty( columnNames ))
            return;
        if (columnNames.length < columnValues.length)
            throw new InvalidInputException( "There is no enough values in the array for the columns. ColumnNames size is " + columnNames.length + " and columnValues size is "  + columnValues.length );
        for (int i=0; i<columnNames.length; i++)
            setParsedValue( columnNames[i], columnValues[i] );
    }

    /**
     * useful to set the key, value pair to the parsed values
     * @param columnName
     * @param columnValue
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15521602D7
     */
    public void setParsedValue(Object columnName, Object columnValue) throws InvalidInputException
    {
        if (columnName != null && columnValue != null)
            _parsedValues.put( columnName, columnValue );
    }

    /**
     * @return boolean
     * @roseuid 3E1552160395
     */
    protected boolean isStarted()
    {
        return _isStarted;
    }

    /**
     * friendly method to check the completion of processing of the previous lineType
     * object.
     * shields the FiniteStateMachine from child implementations
     * @return boolean
     * @roseuid 3E15521603BD
     */
    private boolean isDone()
    {
        return getFiniteStateMachine().isDone( getPrevLineType() );
    }
    
    public void setCnfgFileMgr(CnfgFileMgr cnfgFileMgr){
 	   _cnfgFileMgr = cnfgFileMgr;
    }
    public CnfgFileMgr getCnfgFileMgr(){
 	   return _cnfgFileMgr;
    }
}
