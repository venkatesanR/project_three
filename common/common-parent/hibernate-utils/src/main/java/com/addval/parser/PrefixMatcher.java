//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\PrefixMatcher.java

package com.addval.parser;


public class PrefixMatcher implements LineTypeMatcher
{
    private String _prefix;

    /**
    @roseuid 3D6B0B260081
     */
    public PrefixMatcher()
    {

    }

    /**
    @param input
    @return boolean
    @roseuid 3D6B0B26008B
     */
    public boolean match(String input)
    {
        return input.indexOf( _prefix ) == 0;
    }

    /**
    @param param
    @roseuid 3D6B0B260096
     */
    public void setParameters(String param)
    {
        _prefix = param;
    }
}
