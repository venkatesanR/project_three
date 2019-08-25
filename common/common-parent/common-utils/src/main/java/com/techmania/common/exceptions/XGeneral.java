
package com.techmania.common.exceptions;


/**
   A simple exension to the standard Exception class.
   This class remembers the "source" that generated the
   exception. i.e class/method name or some id.
   @author Sankar Dhanushkodi
   @revision $Revision$
   @see XRuntime
 */
public class XGeneral extends Exception {

   /**
      The source from where the exception originated.
    */
   private String _source;

   /**
      Constructor. Call the super class and set the source.
      @param source String
      @param desc String
      @return
      @exception
      @roseuid 378B8BD6020B
    */
   public XGeneral(String source, String desc) {
      super(desc);
      _source = source;
   }

   /**
      Accessor method for the _source variable
      @param
      @return source variable - String
      @exception
      @roseuid 378B8BD60356
    */
   public String getSource() {
      return _source;
   }

   /**
      Queries the super class and returns the message
      @param
      @return message String
      @exception
      @roseuid 378B8BD6039C
    */
   public String getMessage() {
      return super.getMessage();
   }

   /**
      Converts the class to a string. For debuggin purpose.
      @param
      @return Class as a string - String
      @exception
      @roseuid 378B8BD70004
    */
   public String toString() {
      return ("XGeneral: Source: " + getSource() + "; Error: " + getMessage());
   }
}
