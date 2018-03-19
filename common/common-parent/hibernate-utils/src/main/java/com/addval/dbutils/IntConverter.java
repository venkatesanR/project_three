/* Copyright AddVal Technology Inc. */

package com.addval.dbutils;


/**
   Returns database suitable representation of ints.
   @author Sankar Dhanushkodi
   @revision $Revision$
 */
public class IntConverter extends Converter {

  public IntConverter() {}

  /**
     @roseuid 37935A7B0351
   */
  public IntConverter(String tableName, String columnName, boolean nullable) {
        super( tableName, columnName, nullable );
   }

  /**
     @roseuid 376AB34201B3
   */
  public String convert(String str) {
      if ( isNull( str ) && canContainNull() )
         return "null";
      else
         return str;
   }
}

