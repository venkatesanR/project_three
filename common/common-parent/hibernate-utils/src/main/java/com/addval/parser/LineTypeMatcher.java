//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\LineTypeMatcher.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.parser;


public interface LineTypeMatcher 
{
   
   /**
    * @param input
    * @return boolean
    * @roseuid 3D6B0B1F035C
    */
   public boolean match(String input);
   
   /**
    * @param param
    * @roseuid 3D6B0B1F0371
    */
   public void setParameters(String param);
}
