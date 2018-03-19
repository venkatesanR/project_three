
package com.addval.dbutils;


/**
   Represents a unique ID field in a table. Used when returning the id of a newly inserted row.
   @author Sankar Dhanushkodi
   @revision $Revison$
 */
public class RecID {
   private int _id = 0;

   public RecID() {}

   /**
      Constructor
      @param id  int
      @return
      @exception
      @roseuid 378E4D9903DA
    */
   public RecID(int v) {
      _id = v;
   }

   /**
      @roseuid 37CADB6A0119
    */
   public RecID(String v) {
        _id = Integer.valueOf( v ).intValue();
   }

   /**
      Returns the Id
      @param
      @return id - int
      @exception
      @roseuid 378E4DC40332
    */
   public int getId() {
      return _id;
   }

   /**
      Sets the id.
      @param id - int
      @return
      @exception
      @roseuid 378E4DC40378
    */
   public void setId(int v) {
      _id = v;
   }

   /**
      Returns the class as a string. For debuggin.
      @param
      @return Class as a string - String
      @exception
      @roseuid 378E4E220369
    */
   public String toString() {
      return "" + _id;
   }
}
