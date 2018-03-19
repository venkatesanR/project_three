//Source file: d:\\projects\\common\\source\\com\\addval\\parser\\MessageFiniteStateMachine.java

package com.addval.parser;

import org.apache.regexp.RESyntaxException;

import java.util.*;

import org.apache.regexp.RE;

/**
 * Usually this class would be used as such with out the need to extends it
 * further. Takes care of the base Parsing needs, like preparing, storing  and
 * supplying the LineType Objects, validating whether the LineTypes are in the
 * expected order etc.
 */
public class MessageFiniteStateMachine {
    private String _lineTypeName = null;
    private static final String _LINE_TYPE_NAME = "LineType.class";
    private static final String _MESSAGE_TYPE_ID = "messageTypeID";
    private static final String _HOST_MESSAGE_TYPE_ID = "hostMessageTypeID";
    private static final String _STATES = "states";
    private Hashtable _lineTypes = null;
    private LineType _initialLineType = null;
    private LineType _hostInitialLineType = null;
    private RE _keyMatcher = null;
    private String _keyDelimiter = "|";

    /**
     * Constructor prepares and stores the lineTypes based on the property file entries
     * The Type of LineType to be created is read from the property file and defaults
     * to <code>com.addval.parser.LinType</code>.
     * @param prop
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E155169031E
     */
    protected MessageFiniteStateMachine(java.util.Properties prop) throws InvalidInputException {
        this( prop, null );
    }

    /**
     * Constructor prepares and stores the lineTypes based on the property file entries
     * The Type of LineType to be created is read from the property file and defaults
     * to <code>com.addval.parser.LinType</code>.
     * @param prop
     * @param lineTypeName
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E155169026A
     */
    protected MessageFiniteStateMachine(java.util.Properties prop, String lineTypeName) throws InvalidInputException {
        String[] states  = Utils.getPropertyValues( _STATES, prop );
        int size = states.length;
        if (size == 0)
            throw new RuntimeException( "configuration of Factory is invalid: no states defined" );
        if (Utils.isNullOrEmpty( lineTypeName ))
            _lineTypeName = prop.getProperty( _LINE_TYPE_NAME, "com.addval.parser.LineType" );
        else
            _lineTypeName = lineTypeName;
        _lineTypes = new Hashtable( size );
        for (int i=0; i<size; i++) {
            LineType lineType = getLineType( prop );
            lineType.setType( states[i] );
            // put into the collection of lineTypes
            _lineTypes.put( lineType.getType(), lineType );
            String[] keys = lineType.getKeys();
            // if there are keys specified in the property file, put the lineType
            // against each of the keys
            for (int j=0; j<keys.length; j++) {
                Object object = _lineTypes.get( keys[j] );
                if (object == null) {
                    _lineTypes.put( keys[j], lineType );
                    continue;
                }
                // in case the present key is already associated with a lineType
                // put the current lineType, in to the already present entry
                // they may differ in their matcher reg expressions, though the key is same
                LineType oldLineType = (LineType)object;
                if (!oldLineType.getMatcherRegexp().equals( lineType.getMatcherRegexp() )){
                    oldLineType.addLineType( lineType );
                }
            }
        }
        setInitialLineType( prop.getProperty( _MESSAGE_TYPE_ID ) );
        setHostInitialLineType( prop.getProperty( _HOST_MESSAGE_TYPE_ID ) );
        checkDependancy();
        setKeys( prop );
    }

    /**
     * @param prop
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E3F6197028D
     */
    private void setKeys(Properties prop) throws InvalidInputException {
        // prepare the keyMatcher object if a pattern is specified
        String keyRegexp = prop.getProperty( "lineKeys.pattern" );
        try {
            if (!Utils.isNullOrEmpty( keyRegexp ))
                _keyMatcher = new RE( keyRegexp );
        }
        catch(RESyntaxException re) {
            throw new InvalidInputException( "Error in keyPattern " + keyRegexp );
        }
    }

    /**
     * check and verifies the order of the various LineTypes created.
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15516903A1
     */
    private void checkDependancy() throws InvalidInputException {
        // resolve dependencies between states
        for (Iterator iter = _lineTypes.values().iterator(); iter.hasNext();) {
            LineType lineType = (LineType)iter.next();
            String[] nextTypes = lineType.getNextStates();
            // if no next state is specified, skip to check the next lineType
            if (nextTypes == null || nextTypes.length == 0)
                continue;
            for (int i=0; i<nextTypes.length; i++) {
                Object obj = getLineTypeWithType( nextTypes[i] );
                if (obj == null)
                    throw new RuntimeException("configuration of Factory is invalid: no configuration info for " + nextTypes[i]);
                lineType.addNextValidLineType( (LineType)obj );
            }
        }
    }

    /**
     * using refelction the new lineType object is created and assigned with the
     * properties.
     * the state would be set further.
     * @param prop
     * @return com.addval.parser.LineType
     * @roseuid 3E15516A003B
     */
    private LineType getLineType(java.util.Properties prop) {
        LineType lineType = (LineType)Utils.getNewObject( _lineTypeName );
        lineType.setProperties( prop );
        return lineType;
    }

    /**
     * vital method to give the appropriate LineType object based on the inputLine and
     * the previous LineType
     * validates to whether the specified order is kept correctly
     * @param currentLine
     * @param prevLineType
     * @return com.addval.parser.LineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15516A008B
     */
    protected LineType getNextLineType(String currentLine, LineType prevLineType) throws InvalidInputException {
        if (prevLineType == null) {
            // it is the very first line
            if (getInitialLineType() == null)
                // initital lineCheck is not needed
                return getLineType( currentLine );
            if (getInitialLineType().isMatch( currentLine )){
                // whether it matches the specified first lineType?
                return getInitialLineType();
            }
            else if(getHostInitialLineType().isMatch(currentLine)){
            	//whether it matches a host FFR message?
            	return getHostInitialLineType();
            }
            LineType lineType = getLineType( currentLine );
            // is it atleast matching the comment line?
            if (lineType != null && lineType.isCommentLine())
                return lineType;
            throw new InvalidInputException( "The first line should either be a comment line or of line type specified in the \"messageTypeID\" property " + currentLine );
        }
        // other than first line will be processed here
        // if the specified prevLineType hasNextStates, pick a lineType matching the current line from the
        // validNextStates
        if (prevLineType.hasNextStates())
            return getMatchingLineType( prevLineType.getNextValidLineTypes().iterator(), currentLine );
        // if there is no order enforced simply return the lineType which matches the input line, if any.
        return getLineType( currentLine );
    }

    /**
     * When a full text line is given, the line is matched againgst the matcher
     * regexpression.
     * @param line
     * @return com.addval.parser.LineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15516A0167
     */
    protected LineType getLineType(String line) throws InvalidInputException {
        // if keys are not specified or not found, check every lineType object to find a match
        if (_keyMatcher == null)
            return getMatchingLineType( _lineTypes.values().iterator(), line );
        if (!_keyMatcher.match( line ))
            return null;
        // get the key from the keyPattern
        // get the lineObject corresponding to the key and try getting a match
        String key = "";
        int size = _keyMatcher.getParenCount();
        for (int i=1; i<size;i++)
            key += _keyDelimiter + _keyMatcher.getParen( i );
        if (!key.equals( "" ))
            key = key.substring( _keyDelimiter.length() );
        // try getting a lineType corresponding to the key
        LineType lineType = getLineTypeWithType( key );
        // if this lineType is not matching the inputLine, check with the contained
        // lineType objects
        if (lineType == null || lineType.isMatch( line ))
            return lineType;
        return getMatchingLineType( lineType.getLineTypes(), line );
    }

    /**
     * from the collection of LineTypes, return the one which matches the input line,
     * which is not the first line
     * @param lineTypes
     * @param line
     * @return com.addval.parser.LineType
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E15516A0211
     */
    private LineType getMatchingLineType(Iterator lineTypes, String line) throws InvalidInputException {
        while (lineTypes.hasNext()) {
            LineType lineType = (LineType)lineTypes.next();
            if (lineType.isMatch( line ))
                return lineType;
        }
        return null;
    }

    /**
     * friendly method to retrieve the LineType object specifying the type(state or
     * prefix)
     * @param type
     * @return com.addval.parser.LineType
     * @roseuid 3E15516A02EE
     */
    protected LineType getLineTypeWithType(String type)
	{
		Object object = _lineTypes.get( type );
		if (object != null)
			return (LineType)object;
		// if it is not a stright match try getting the lineType by using the matcher.
		for (Iterator iter = _lineTypes.values().iterator(); iter.hasNext(); ) {
			LineType lineType = (LineType)iter.next();
			if (lineType.getMatcher().match( type ))
				return lineType;
		}
		// even if not get now also, return a null
        return null;
    }

    /**
     * set the inittial lineType object
     * @param type
     * @roseuid 3E15516A0334
     */
    private void setInitialLineType(String type) {
        if (!Utils.isNullOrEmpty( type ))
            _initialLineType = getLineTypeWithType( type );
    }

    /**
     * returns the initiallineType
     * @return com.addval.parser.LineType
     * @roseuid 3E15516A0366
     */
    public LineType getInitialLineType() {
        return _initialLineType;
    }
    
    /**
     * set the host initial lineType object
     * @param type
     * @roseuid 3E15516A0334
     */
    private void setHostInitialLineType(String type) {
        if (!Utils.isNullOrEmpty( type ))
        	_hostInitialLineType = getLineTypeWithType( type );
    }

    /**
     * returns the host initiallineType
     * @return com.addval.parser.LineType
     * @roseuid 3E15516A0366
     */
    public LineType getHostInitialLineType() {
        return _hostInitialLineType;
    }
    
    
    
    
    

    /**
     * method yet fully to be implemented. now always return true.
     * @param lineType
     * @return boolean
     * @roseuid 3E15516A0398
     */
    protected boolean isDone(LineType lineType) {
        return true;
    }

	public Map getLineTypes() {
		return _lineTypes;
	}
}
