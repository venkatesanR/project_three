package com.addval.dbutils;

import java.util.StringTokenizer;

/**
   Used to format string by removing quotes. i.e strings formatted here can be used
   in DB queries.
   @author Sankar Dhanushkodi
   @revision $Revision$
 */
public class StringConverter extends Converter {

   public StringConverter() {}

   /**
      Constructor.
      @param tableName String
      @param colname String
      @param nullable boolean
      @return
      @exception
      @see Converter
      @roseuid 37935AD60211
    */
   public StringConverter(String tableName, String columnName, boolean nullable) {
      super( tableName, columnName, nullable );
   }

   /**
      Escapes all quotes in the input string.
      @param strContent String
      @return formatted string  String
      @exception
      @roseuid 376AB09601BA
    */
   public String convert(String strContent) {
      if ( isNull( strContent ) && canContainNull() )
         return "null";
      else
         return ( "'" + escapeQuotes(strContent) + "'");
   }

   /**
      Escapes all the single quotes (') in a string. Static method. Can be used to clean up strings
      @param inString String (to escape)
      @return formatted string - String
      @exception
      @roseuid 390F51470319
    */
   public static String escapeQuotes(String inString) {
      StringBuffer outString= new StringBuffer();
      String token = null;
      boolean first = true;

      if (inString != null) {
         StringTokenizer st = new StringTokenizer(inString,"'", true);
         while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals("'"))
               outString.append("''");
            else
               outString.append(token);
         }
      }
      return outString.toString();

   }
}
