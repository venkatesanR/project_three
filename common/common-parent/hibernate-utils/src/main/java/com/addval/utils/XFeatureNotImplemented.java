/* AddVal Technology Inc. */

package com.addval.utils;

import java.lang.reflect.Constructor;

/**
   Extends the RuntimeException class. Used to indicate features that are still not fully completed. Very similiar
   to the XRuntime class
   @author Sankar Dhanushkodi
   @version $Revision$
 */
public class XFeatureNotImplemented extends RuntimeException {
   private String _source;

   public XFeatureNotImplemented() {}

   /**
      Constructor. Calls the super class to set the exception. Sets
      the orgin of the exception locally.
      @param source String
      @param desc String
      @return
      @exception
      @roseuid 3874CD5500E9
    */
   public XFeatureNotImplemented(String source, String desc) {
      super(desc);
      _source=source;
   }

   /**
      Accessor to the _source member variable
      @param
      @return source String
      @exception
      @roseuid 3874CD5501E3
    */
   public String getSource() {
      return _source;
   }

   /**
      Returns the message attached to the exeption
      @param
      @return message String
      @exception
      @roseuid 3874CD55021F
    */
   public String getMessage() {
      return super.getMessage();
   }

   /**
      Returns the class as a string. Used for debugging.
      @param
      @return Class as a string - String
      @exception
      @roseuid 3874CD5502AB
    */
   public String toString() {
      return ("XFeatureNotImplemented: Source : " + getSource() + "; Error: " + getMessage());
   }
}
