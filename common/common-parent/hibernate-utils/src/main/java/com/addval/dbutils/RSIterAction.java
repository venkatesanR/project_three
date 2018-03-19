//Source file: D:\Projects\AVOptimizer\source\com\addval\dbutils\RSIterAction.java

/* AddVal Technology Inc. */

package com.addval.dbutils;


/**
 * Set of statics that define various actions that can be
 * performed on a record set.  The client will send this information.
 * @author Sankar Dhanushkodi
 * @version $Revision$
 * @see RSIterator
 * @see RSPageDesc
 */
public class RSIterAction
{
    public static final int _UNDEF = 0;
    public static final int _FIRST = 1;
    public static final int _LAST = 2;
    public static final int _NEXT = 3;
    public static final int _PREV = 4;
    public static final int _CURR = 5;
    public static final String _UNDEF_STR = "UNDEF";
    public static final String _FIRST_STR = "FIRST";
    public static final String _LAST_STR = "LAST";
    public static final String _NEXT_STR = "NEXT";
    public static final String _PREV_STR = "PREV";
    public static final String _CURR_STR = "CURR";
    private int _action;

    /**
     * Constructor.
     * @param action String
     * @return
     * @exception
     * @roseuid 378E28C602E4
     */
    public RSIterAction(int action)
    {
           _action = action;
    }

    /**
     * Constructor.
     * @param action int
     * @return
     * @exception
     * @roseuid 39122A220244
     */
    public RSIterAction(String action)
    {
           if (action == null)
              action = _FIRST_STR;
           if ( action.compareTo( _UNDEF_STR ) == 0 )
              _action = _UNDEF;
           else if ( action.compareTo( _FIRST_STR ) == 0 )
              _action = _FIRST;
           else if ( action.compareTo( _LAST_STR ) == 0 )
              _action = _LAST;
           else if ( action.compareTo( _NEXT_STR ) == 0 )
              _action = _NEXT;
           else if ( action.compareTo( _PREV_STR ) == 0 )
              _action = _PREV;
           else if ( action.compareTo( _CURR_STR ) == 0 )
              _action = _CURR;
           else
              _action = _UNDEF;
    }

    /**
     * Is the value of the "action" FIRST?
     * @param
     * @return true/false - boolean
     * @exception
     * @roseuid 378E2873032B
     */
    public boolean isFirst()
    {
           return (_action == _FIRST);
    }

    /**
     * Is the value of the "action" LAST?
     * @param
     * @return true/false - boolean
     * @exception
     * @roseuid 378E2885001A
     */
    public boolean isLast()
    {
           return (_action == _LAST);
    }

    /**
     * Is the value of the "action" NEXT?
     * @param
     * @return true/false - boolean
     * @exception
     * @roseuid 378E288D0043
     */
    public boolean isNext()
    {
           return (_action == _NEXT);
    }

    /**
     * Is the value of the "action" PREV?
     * @param
     * @return true/false - boolean
     * @exception
     * @roseuid 378E289300B0
     */
    public boolean isPrev()
    {
           return (_action == _PREV);
    }

    /**
     * Is the value of the "action" CURR?
     * @param
     * @return true/false - boolean
     * @exception
     * @roseuid 37FBBB870075
     */
    public boolean isCurr()
    {
           return (_action == _CURR);
    }

    /**
     * Accessor for the "action" member variable
     * @param
     * @return action int
     * @exception
     * @roseuid 378E28A401B9
     */
    public int getActionVal()
    {
           return _action;
    }

    /**
     * Returns the class as a string. Usefull for debugging.
     * @param
     * @return Class as a string - String
     * @exception
     * @roseuid 378E29EF03A0
     */
    public String toString()
    {
           switch ( _action ) {
              case _FIRST:
                 return _FIRST_STR;
              case _LAST:
                 return _LAST_STR;
              case _NEXT:
                 return _NEXT_STR;
              case _PREV:
                 return _PREV_STR;
              case _CURR:
                 return _CURR_STR;
              case _UNDEF:
              default:
                 return _UNDEF_STR;
           }
    }
}
