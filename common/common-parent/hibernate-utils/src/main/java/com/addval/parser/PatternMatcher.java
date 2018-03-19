//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\PatternMatcher.java

package com.addval.parser;

import org.apache.regexp.RE;

public class PatternMatcher implements LineTypeMatcher
{
    private String _pattern;
    private RE _regExp = null;

    /**
    @param input
    @return boolean
    @roseuid 3DA664DD0376
     */
    public boolean match(String input)
    {
        if (_regExp != null)
            return _regExp.match( input );
        return false;
    }

    /**
    @param param
    @roseuid 3DA664DD03A8
     */
    public void setParameters(String param)
    {
        _pattern = param;
        try{
            _regExp = new org.apache.regexp.RE( _pattern );
        }
        catch(org.apache.regexp.RESyntaxException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
