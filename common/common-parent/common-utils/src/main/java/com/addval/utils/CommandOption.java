//Source file: D:\\Projects\\Common\\source\\com\\addval\\utils\\CommandOption.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.utils;

import java.util.Vector;

/**
 * Used in conjuction with the CommandLine class to parse command line options. 
 * The commandline flag and argument(s) are stored here. Flags with single 
 * arguments are stored in a String variable. Flags with multiple arguments are 
 * stored in a Vector.
 * @author AddVal Technology Inc.
 * @see CommandLine
 */
public class CommandOption {
	private String _flag;
	private String _argument;
	private Vector _allArguments;
	
	/**
	 * @roseuid 3C6437B200A1
	 */
	private CommandOption() {
		
	}
	
	/**
	 * Constructor. Initializes the class setting the argument.
	 * 
	 * @param flag Argument flag.
	 * @roseuid 3BBBBFA702E1
	 */
	public CommandOption(String flag) {
      _flag = flag;
      _argument = "";
      _allArguments = null;		
	}
	
	/**
	 * Access method for the _flag property.
	 * 
	 * @return   the current value of the _flag property
	 */
	public String getFlag() {
      return _flag;		
	}
	
	/**
	 * Sets the value of the _flag property.
	 * 
	 * @param aFlag the new value of the _flag property
	 */
	public void setFlag(String aFlag) {
		_flag = aFlag;
		}
	
	/**
	 * Access method for the _argument property.
	 * 
	 * @return   the current value of the _argument property
	 */
	public String getArgument() {
      return _argument;		
	}
	
	/**
	 * Sets the value of the _argument property.
	 * 
	 * @param aArgument the new value of the _argument property
	 */
	public void setArgument(String aArgument) {
		_argument = aArgument;
		}
	
	/**
	 * Access method for the _allArguments property.
	 * 
	 * @return   the current value of the _allArguments property
	 */
	public Vector getAllArguments() {
      if ( _allArguments == null ) {
         _allArguments = new Vector();
         _allArguments.add( _argument );
      }

      return _allArguments;		
	}
	
	/**
	 * Sets the value of the _allArguments property.
	 * 
	 * @param aAllArguments the new value of the _allArguments property
	 */
	public void setAllArguments(Vector aAllArguments) {
		_allArguments = aAllArguments;
		}
	
	/**
	 * Checks to see if a flag had only one argument.
	 * 
	 * @return true - if the flag has one argument. False otherwise.
	 * @roseuid 3BBCB2650233
	 */
	public boolean hasSingleArgument() {
      if ( _allArguments == null ) {
         return true;
      }
      else {
         return false;
      }		
	}
	
	/**
	 * Returns the class as a string.
	 * 
	 * @return string containing the class members.
	 * @roseuid 3BBCB75603A4
	 */
	public String toString() {
      if ( hasSingleArgument() ) {
         return _flag + ": '" + _argument + "'";
      }
      else {
         StringBuffer sb = new StringBuffer();
         sb.append( _flag ).append(": '");

         int max = _allArguments.size();
         for ( int i=0; i<max; i++ ) {
            if (i>0) { sb.append("', '"); }
            sb.append( (String)_allArguments.get(i) );
         }
         sb.append("'");

         return sb.toString();
      }		
	}
}
