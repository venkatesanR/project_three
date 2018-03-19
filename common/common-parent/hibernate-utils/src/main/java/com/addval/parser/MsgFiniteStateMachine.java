//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\MsgFiniteStateMachine.java

package com.addval.parser;


public interface MsgFiniteStateMachine 
{
    
    /**
    Method to return the LineType for the given String prefix
    @param prefix
    @return LineType
    @roseuid 3D7849C70185
     */
    public LineType getLineType(String prefix);
    
    /**
    @param currentLine
    @param prevLineType
    @return LineType
    @throws InvalidInputException
    @throws com.addval.parser.InvalidInputException
    @roseuid 3D6B0B2203C5
     */
    public LineType getNextLineType(String currentLine, String prevLineType) throws InvalidInputException;
    
    /**
    @param prevLineType
    @return boolean
    @roseuid 3D6B0B230196
     */
    public boolean isDone(String prevLineType);
    
    /**
    @return String
    @roseuid 3D6B0B2301A9
     */
    public String getInitialLineType();
}
