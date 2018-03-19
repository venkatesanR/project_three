//Source file: D:\\Projects\\Common\\source\\com\\addval\\utils\\CommandLine.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.utils;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Class used to parse and store information about commandline flags and arguments
 *
 * @author AddVal Technology Inc.
 */
public class CommandLine {
   private static final char _flagPrefix = '-';
   private Hashtable _options = new Hashtable();

   /**
    * @roseuid 3C6437AD01A8
    */
   private CommandLine() {

   }

   /**
    * Constructor. Parses the input (arguments) seperating flags and the
    * corresponding arguments.
    *
    * @param argv Array of flags and corresponding arguments.
    * @roseuid 3BBBBD7002FA
    */
   public CommandLine(String[] argv) {

      CommandOption option = null;

      int lookingFor = 0;

      int max = argv.length;
      for ( int i=0; i<max; i++ ) {

         switch ( lookingFor ) {

            case 0 : //looking for flag
               if ( isFlag( argv[i] ) ) {
                  String flag = argv[i];
                  option = new CommandOption( flag );
                  _options.put( flag, option );
                  lookingFor = 1;
               }
               else {//found naked arg
                  String defaultFlag = String.valueOf(_flagPrefix);
                  option = new CommandOption( defaultFlag );
                  option.setArgument( argv[i] );
                  _options.put( defaultFlag, option );
                  lookingFor = 2;
               }
               break;

            case 1 : //looking for arg
               if ( isFlag( argv[i] ) ) {
                  i--;
                  lookingFor = 0;
               }
               else {
                  option.setArgument( argv[i] );
                  lookingFor = 2;
               }
               break;

            case 2 : //looking for more args
               if ( isFlag( argv[i] ) ) {
                  i--;
                  lookingFor = 0;
               }
               else {
                  option.getAllArguments().add( argv[i] );
                  lookingFor = 2;
               }
               break;

         }//switch

      }//for

   }

   /**
    * Access method for the _flagPrefix property.
    *
    * @return   the current value of the _flagPrefix property
    */
   public static char getFlagPrefix() {
        return _flagPrefix;
   }

   /**
    * Access method for the _options property.
    *
    * @return   the current value of the _options property
    */
   public Hashtable getOptions() {
      return _options;
   }

   /**
    * Checks to see if a given flag was passed in through the commandline. The flag
    * to be checked can be passed in with or without the "-" prefix.
    *
    * @param flag Flag name to check against the list of flags passed in.
    * @return True if the flag is in the list of flags passed in by the user.
    * @roseuid 3BBBC2C40020
    */
   public boolean hasFlag(String flag) {
      if ( isFlag( flag ) ) {
         return _options.containsKey( flag );
      }
      else {
         return _options.containsKey( _flagPrefix + flag );
      }
   }

   /**
    * Returns the number of argumetns associated with a flag.
    *
    * @param flag Flag to be checked.
    * @return Number of arguments associated with the flag. 0 if none are found.
    * @roseuid 3BBBC46F029B
    */
   public int getNumberOfFlagArgs(String flag) {
      if (! isFlag(flag) ) { flag = _flagPrefix + flag; }

      CommandOption option = (CommandOption)_options.get( flag );

      if ( option == null ) { return -1; }

      if ( option.getArgument().equals("") ) {
         return 0;
      }
      else {
         if ( option.hasSingleArgument() ) {
            return 1;
         }
         else {
            return option.getAllArguments().size() + 1;
         }
      }
   }

   /**
    * Returns argument associated with a flag. Used with flags that are known to have
    * one argument.
    *
    * @param flag Flag to get the argument of.
    * @return Argument associated with the flag. Returns empty string if no arguments
    * are found.
    * @roseuid 3BBBC4B10355
    */
   public String getFlagArgument(String flag) {
      if (! isFlag(flag) ) { flag = _flagPrefix + flag; }

      CommandOption option = (CommandOption)_options.get( flag );

      if ( option == null ) { return ""; }

      return option.getArgument();
   }

   /**
    * Returns all the arguments associated with a flag. Used for flags that have more
    * than one argument.
    *
    * @param flag Flag to get the arguments of
    * @return Vector containing all the arguments associated with the given flag.
    * Null if the flag has no arguments.
    * @roseuid 3BBBC4920378
    */
   public Vector getFlagAllArguments(String flag) {
      if (! isFlag(flag) ) { flag = _flagPrefix + flag; }

      CommandOption option = (CommandOption)_options.get( flag );

      if ( option == null ) { return null; }

      return option.getAllArguments();
   }

   /**
    * Returns the total number of flags found in the commandline.
    *
    * @return Number of flags.
    * @roseuid 3BBCAE48010C
    */
   public int getNumberOfFlags() {
      return _options.size();
   }

   /**
    * Returns a enumeration of all the flags passed in.
    *
    * @return Enumeration of all the flags.
    * @roseuid 3BBCB5980104
    */
   public Enumeration getFlags() {
      return _options.keys();
   }

   /**
    * Prints contents of the class as a string.
    *
    * @return Contents of the class as a string.
    * @roseuid 3BBCB7340189
    */
   public String toString() {
      Enumeration opts = _options.elements();
      StringBuffer sb = new StringBuffer();
      String lineSeparator = System.getProperty("line.separator", "\n");

      while ( opts.hasMoreElements() ) {

         sb.append( ((CommandOption)opts.nextElement()).toString() ).append( lineSeparator );

      }

      return sb.toString();
   }

   /**
    * @param arg
    * @return boolean
    * @roseuid 3BBBCE6B032D
    */
   private static boolean isFlag(String arg) {
      if ( arg==null || arg.length()==0 ) { return false; }

      if ( arg.charAt(0) == _flagPrefix ) { return true; }
      else { return false; }
   }
}
