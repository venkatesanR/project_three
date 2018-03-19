//Source file: d:\\projects\\common\\source\\com\\addval\\parser\\LineType.java

package com.addval.parser;

import java.util.Vector;
import java.util.Properties;
import java.util.Hashtable;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * This is base common class for a LineType which holds MetaData of a line Type.
 */
public class LineType
{
    private String _type = null;
    private String _keys[] = null;
    private int _minLineCount = 0;
    private int _maxLineCount = 0;
    private int _minFieldCount = 0;
    private int _maxFieldCount = 0;
    private boolean _isComment = false;
    private String _splitterRegexp = null;
    private String _matcherRegexp = null;
    private String _lineColumns[] = null;
    private String _nextStates[] = null;
	private String _splits[] = null;
	private String _levels[] = null;
    private String _nextLevels[] = null;
//    private static final String _DELIMITER = "|";
    private static final String _LINE_COLUMNS = "columns";
    private static final String _IS_COMMENT = "isComment";
    private static final String _NEXT_STATES = "nextStates";
    private static final String _COUNTS = "counts";
    private static final String _MATCHER_REGEXP = "matcherPattern";
    private static final String _SPLITTER_REGEXP = "splitterPattern";
	private static final String _SPLITS = "splits";
    private static final String _NEXT_LEVELS = "nextLevels";
    private static final String _LEVELS = "levels";
    private static final String _FALSE = "false";
    private static final String _TRUE = "true";
    private static final String _YES = "yes";
//    private static final String _KEY = "key";
//    private static final String _NO = "no";
    private static final String _INPUT_LINE = "INPUT LINE";
    private static final String _KEYS = "keys";
    private Vector _nextValidLineTypes = null;
    private Properties _prop = null;
    private RE _matcher = null;
    private RE _splitter = null;
    private Vector _lineTypes = null;

	public LineType()
	{
		
	}

    /**
     * Access method for the _type property.
     *
     * @return   the current value of the _type property
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Sets the value of the _type property.
     *
     * @param aType the new value of the _type property
     */
    public void setType(String aType) throws InvalidInputException
    {
        if (Utils.isNullOrEmpty( aType ))
            throw new InvalidInputException( "LineType should not be null or Empty!" );
        _type = aType;
        initValues();
    }

    /**
     * Access method for the _keys property.
     *
     * @return   the current value of the _keys property
     */
    public String[] getKeys()
    {
        return _keys;
    }

    /**
     * Sets the value of the _keys property.
     *
     * @param aKeys the new value of the _keys property
     */
    public void setKeys(String[] aKeys)
    {
        _keys = aKeys;
    }

    /**
     * Access method for the _minLineCount property.
     *
     * @return   the current value of the _minLineCount property
     */
    public int getMinLineCount()
    {
        return _minLineCount;
    }

    /**
     * Sets the value of the _minLineCount property.
     *
     * @param aMinLineCount the new value of the _minLineCount property
     */
    public void setMinLineCount(int aMinLineCount)
    {
        _minLineCount = aMinLineCount;
    }

    /**
     * Access method for the _maxLineCount property.
     *
     * @return   the current value of the _maxLineCount property
     */
    public int getMaxLineCount()
    {
        return _maxLineCount;
    }

    /**
     * Sets the value of the _maxLineCount property.
     *
     * @param aMaxLineCount the new value of the _maxLineCount property
     */
    public void setMaxLineCount(int aMaxLineCount)
    {
        _maxLineCount = aMaxLineCount;
    }

    /**
     * Access method for the _minFieldCount property.
     *
     * @return   the current value of the _minFieldCount property
     */
    public int getMinFieldCount()
    {
        return _minFieldCount;
    }

    /**
     * Sets the value of the _minFieldCount property.
     *
     * @param aMinFieldCount the new value of the _minFieldCount property
     */
    public void setMinFieldCount(int aMinFieldCount)
    {
        _minFieldCount = aMinFieldCount;
    }

    /**
     * Access method for the _maxFieldCount property.
     *
     * @return   the current value of the _maxFieldCount property
     */
    public int getMaxFieldCount()
    {
        return _maxFieldCount;
    }

    /**
     * Sets the value of the _maxFieldCount property.
     *
     * @param aMaxFieldCount the new value of the _maxFieldCount property
     */
    public void setMaxFieldCount(int aMaxFieldCount)
    {
        _maxFieldCount = aMaxFieldCount;
    }

    /**
     * Determines if the _isComment property is true.
     *
     * @return   <code>true<code> if the _isComment property is true
     */
    public boolean getIsComment()
    {
        return _isComment;
    }

    /**
     * Sets the value of the _isComment property.
     *
     * @param aIsComment the new value of the _isComment property
     */
    public void setIsComment(boolean aIsComment)
    {
        _isComment = aIsComment;
    }

    /**
     * Access method for the _splitterRegexp property.
     *
     * @return   the current value of the _splitterRegexp property
     */
    public String getSplitterRegexp()
    {
        return _splitterRegexp;
    }

    /**
     * Sets the value of the _splitterRegexp property.
     *
     * @param aSplitterRegexp the new value of the _splitterRegexp property
     */
    public void setSplitterRegexp(String aSplitterRegexp)
    {
        _splitterRegexp = aSplitterRegexp;
    }

    /**
     * Access method for the _matcherRegexp property.
     *
     * @return   the current value of the _matcherRegexp property
     */
    public String getMatcherRegexp()
    {
        return _matcherRegexp;
    }

    /**
     * Sets the value of the _matcherRegexp property.
     *
     * @param aMatcherRegexp the new value of the _matcherRegexp property
     */
    public void setMatcherRegexp(String aMatcherRegexp)
    {
        _matcherRegexp = aMatcherRegexp;
    }

    /**
     * Access method for the _lineColumns property.
     *
     * @return   the current value of the _lineColumns property
     */
    public String[] getLineColumns()
    {
        return _lineColumns;
    }

    /**
     * Sets the value of the _lineColumns property.
     *
     * @param aLineColumns the new value of the _lineColumns property
     */
    public void setLineColumns(String[] aLineColumns)
    {
        _lineColumns = aLineColumns;
    }

    /**
     * Access method for the _nextStates property.
     *
     * @return   the current value of the _nextStates property
     */
    public String[] getNextStates()
    {
        return _nextStates;
    }

    /**
     * Sets the value of the _nextStates property.
     *
     * @param aNextStates the new value of the _nextStates property
     */
    public void setNextStates(String[] aNextStates)
    {
        _nextStates = aNextStates;
    }

    public String[] getNextLevels()
    {
        return _nextLevels;
    }

    public void setNextLevels(String[] aNextLevels)
    {
        _nextLevels = aNextLevels;
    }

	public String[] getSplits()
	{
		return _splits;
	}

	public void setSplits(String[] aSplits)
	{
		_splits = aSplits;
	}

	public String[] getLevels()
	{
		return _levels;
	}

	public void setLevels(String[] levels)
	{
		_levels = levels;
	}

    /**
     * Access method for the _nextValidLineTypes property.
     *
     * @return   the current value of the _nextValidLineTypes property
     */
    public Vector getNextValidLineTypes()
    {
        return _nextValidLineTypes;
    }

    /**
     * Sets the value of the _nextValidLineTypes property.
     *
     * @param aNextValidLineTypes the new value of the _nextValidLineTypes property
     */
    public void setNextValidLineTypes(Vector aNextValidLineTypes)
    {
        _nextValidLineTypes = aNextValidLineTypes;
    }

    /**
     * Access method for the _prop property.
     *
     * @return   the current value of the _prop property
     */
    public Properties getProp()
    {
        return _prop;
    }

    /**
     * Sets the value of the _prop property.
     *
     * @param aProp the new value of the _prop property
     */
    public void setProp(Properties aProp)
    {
        _prop = aProp;
    }

    /**
     * Access method for the _matcher property.
     *
     * @return   the current value of the _matcher property
     */
    public RE getMatcher()
    {
        return _matcher;
    }

    /**
     * Sets the value of the _matcher property.
     *
     * @param aMatcher the new value of the _matcher property
     */
    public void setMatcher(RE aMatcher)
    {
        _matcher = aMatcher;
    }

    /**
     * Access method for the _splitter property.
     *
     * @return   the current value of the _splitter property
     */
    public RE getSplitter()
    {
        return _splitter;
    }

    /**
     * Sets the value of the _splitter property.
     *
     * @param aSplitter the new value of the _splitter property
     */
    public void setSplitter(RE aSplitter)
    {
        _splitter = aSplitter;
    }

    /**
     * @param lineType
     * @roseuid 3E3F90760009
     */
    public void addLineType(LineType lineType)
    {
        _lineTypes.add( lineType );
    }

    /**
     * @return java.util.Iterator
     * @roseuid 3E3F909B0233
     */
    public Iterator getLineTypes()
    {
        return _lineTypes.iterator();
    }

    /**
     * @param prop
     * @roseuid 3E15506E0331
     */
    public void setProperties(java.util.Properties prop)
    {
        _prop = prop;
    }

    /**
     * friendly internal method.
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15506E03BE
     */
    protected void initValues() throws InvalidInputException
    {
        _keys = Utils.splitLine( get( _KEYS ) );
        _splitterRegexp = get( _SPLITTER_REGEXP );
        _matcherRegexp = get( _MATCHER_REGEXP );
        _lineTypes = new Vector();
        try {
            if (getSplitterRexexp() != null)
                _splitter = new RE( getSplitterRexexp(), RE.MATCH_CASEINDEPENDENT );
        }
        catch( RESyntaxException e ) {
            throw new InvalidInputException( "Invalid splitterPattern for line type: " + getType() + " - \t" + getSplitterRexexp() + '\n' + e );
        }
        try {
            _matcher = getMatcherRegexp() == null ? getSplitter() : new RE( getMatcherRegexp(), RE.MATCH_CASEINDEPENDENT );
        }
        catch( RESyntaxException e ) {
            throw new InvalidInputException( "Invalid matcherPattern for line type: " + getType() + " - \t" + getMatcherRegexp() + '\n' + e );
        }
        String isComment = get( _IS_COMMENT, _FALSE );
        _isComment = isComment.equalsIgnoreCase( _TRUE ) || isComment.equalsIgnoreCase( _YES );
        // no need to process further if it is a comment line
        if (isCommentLine())
            return;
        _nextValidLineTypes = new Vector();
        _lineColumns = Utils.splitLine( get( _LINE_COLUMNS ) );
        _nextStates  = Utils.splitLine( get( _NEXT_STATES  ) );
        _nextLevels  = Utils.splitLine( get( _NEXT_LEVELS  ) );
		_splits = Utils.splitLine( get( _SPLITS ) );
        _levels = Utils.splitLine( get(_LEVELS ) );
        String countsStr = get( _COUNTS );
        if (countsStr == null)
            return;
        String[] counts = Utils.splitLine(  countsStr );
        if (counts.length != 4)
            throw new InvalidInputException( "If specified, there should be 4 values (Min and Max values for line and field) for line type: " + getType() +'\t'+countsStr );
        int i = 0;
        _minLineCount   = Integer.parseInt( counts[i++] );
        _maxLineCount   = Integer.parseInt( counts[i++] );
        _minFieldCount  = Integer.parseInt( counts[i++] );
        _maxFieldCount  = Integer.parseInt( counts[i]   );
        // ensure to have atleast the matcher specified,
        // when some lines and fields are expected for this LineType
        if (getMatcher() == null && _minLineCount + _maxLineCount + _minFieldCount + _maxFieldCount > 0)
            throw new InvalidInputException( "Either matcherPattern or splitterPattern or better both are to be specified! Please check. " + getType() );
    }


	public Map getMapValues(String line, String columnNames[]) throws InvalidInputException
	{
		Map parsedValues = new HashMap();
		RE splitter = getSplitter( line );
		if (splitter == null)
			return parsedValues;
		if (columnNames == null)
			columnNames = getLineColumns();
		int size = columnNames.length;
		// The first element would be the input line in full
		parsedValues.put( _INPUT_LINE, _splitter.getParen( 0 ) );
		for (int i=0; i<size; i++)
			parsedValues.put( columnNames[i], _splitter.getParen( i+1 ) );
		return parsedValues;
	}
    /**
     * @param line
     * @param columnNames
     * @param columnNames[]
     * @return java.util.Hashtable
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15506F009E
     */
    public Hashtable getValues(String line, String columnNames[]) throws InvalidInputException
    {
        Hashtable parsedValues = new Hashtable();
        RE splitter = getSplitter( line );
        if (splitter == null)
            return parsedValues;
        if (columnNames == null)
            columnNames = getLineColumns();
        int size = columnNames.length;
        // The first element would be the input line in full
        parsedValues.put( _INPUT_LINE, _splitter.getParen( 0 ) );
        for (int i=0; i<size; i++) {
            String value = _splitter.getParen( i+1 );
            if (columnNames[i] != null && value != null && !value.trim().equals( "" ))
                parsedValues.put( columnNames[i], value );
        }
        return parsedValues;
    }

    /**
     * The regular expression not only serves to validate the input line.
     * it validates the count, format and value validation and splits the input
     * returns as an array.
     * NOTE: the first element of the array would always contains the input line
     * This would eb helpful when the full text line is required, even after splitting
     * the line.
     * @param line
     * @return String[]
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15506F01FC
     */
    public String[] getValues(String line) throws InvalidInputException
    {
        RE splitter = getSplitter( line );
        if (splitter == null)
            return new String[]{ line };
        int size = splitter.getParenCount();
        String[] values = new String[size];
        // The first element would be the input line in full
        for (int i=0; i<size; i++)
            values[i] = splitter.getParen( i );
        return values;
    }

    /**
     * @return java.util.Properties
     * @roseuid 3E15506F0301
     */
    public Properties getProperties()
    {
        return _prop;
    }

    /**
     * @return boolean
     * @roseuid 3E15506F0365
     */
    public boolean hasNextStates()
    {
        return _nextStates.length > 0;
    }

    public boolean hasNextLevels()
    {
        return _nextLevels.length > 0;
    }

    public boolean hasLevels()
    {
        return _levels.length > 0;
    }

    /**
     * based on the regExpression the input line is validated
     * @param line
     * @return boolean
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15506F03B5
     */
    public boolean isMatch(String line) throws InvalidInputException
    {
        // if there is a pattern matcher match it with the input line
        // non-specifying the matcher pattern is interpreted as no match
        // to be discussed, whether otherwise
        return _matcher == null ? false : _matcher.match( line );
    }

    /**
     * populate the NextValid LineTypes for this line
     * @param lineType
     * @roseuid 3E15507000C7
     */
    public void addNextValidLineType(LineType lineType)
    {
        _nextValidLineTypes.add( lineType );
    }

    /**
     * @param line
     * @return RE
     * @throws InvalidInputException
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E155070015E
     */
    public RE getSplitter(String line) throws InvalidInputException
    {
        if (getSplitter() == null)
            return null;
        // if splitter and matcher are the same, the current line is already matched with the
        // regexp and so could be returned as such.
        if (getSplitter() == getMatcher() || getSplitter().match( line ))
            return getSplitter();
        throw new InvalidInputException( "Format error in line - Found while splitting the line : " + line + " with regExp " + getSplitterRexexp() );
    }

    /**
     * returnes the regular expression associated with this lineType
     * @return String
     * @roseuid 3E1550700280
     */
    public String getSplitterRexexp()
    {
        return _splitterRegexp;
    }

    /**
     * Specify whether the current line is  a Comment line.
     * Useful in deciding whether to process the line or not
     * @return boolean
     * @roseuid 3E15507002D0
     */
    public boolean isCommentLine()
    {
        return _isComment;
    }

    /**
     * friendly method to retrieve the attribute value from the property file.
     * @param key
     * @return String
     * @roseuid 3E1550700320
     */
    protected String get(String key)
    {
        return get( key, null );
    }

    /**
     * friendly method to retrieve the attribute value from the property file.
     * Givesout the default value in case the
     * return value from property file is null
     * @param key
     * @param value
     * @return String
     * @roseuid 3E1550710001
     */
    protected String get(String key, String value)
    {
        if (key == null)
            return null;
        return getProp().getProperty( getType().concat( "." ).concat( key ), value );
    }

    /**
     * @return java.lang.String
     * @roseuid 3E1550710123
     */
    public String toString()
    {
        return Utils.toString( this );
    }
}
