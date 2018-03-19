//Source file: D:\Projects\AVOptimizer\source\com\addval\dbutils\RSPageDesc.java

/* AddVal Technology Inc. */

package com.addval.dbutils;


/**
 * Helps in accessing records for user interface. Describes the various record control buttons on a screen. This object will help in deciding if the Next button Prev button etc., are active or not. * @version 
 * @author Sankar Dhanushkodi
 * @revision
 * @see RSIterator
 * @see RSIterAction
 */
public class RSPageDesc 
{
    private int _rowCount;
    private int _firstRowIndex;
    private int _lastRowIndex;
    private int _pageSize;
    
    /**
     * Constructor. Initializes the class.
     * @param  rowCount int. Total number of rows in the record
     * set. 
     * @param  firstRowIndex int. The first row index based on
     * the current page parameters.
     * @param  lastRowIndex int. The last row index based on the
     * current page parameters.
     * @param  pageSize int. The number of rows the user
     * wants returned
     * @return
     * @exception
     * @roseuid 378FC2C90370
     */
    public RSPageDesc(int rowCount, int firstRowIndex, int lastRowIndex, int pageSize) 
    {
      _rowCount      = rowCount;
      _firstRowIndex = firstRowIndex;
      _lastRowIndex  = lastRowIndex;
      _pageSize      = pageSize;
    }
    
    /**
     * Is this the first row in the current page
     * @param
     * @return true/false - boolean
     * @exception
     * @roseuid 378FC3580004
     */
    public boolean isFirst() 
    {
      return _firstRowIndex == 1;
    }
    
    /**
     * Is this the last page in the current page.
     * @param
     * @return true/false boolean
     * @exception
     * @roseuid 378FC3580022
     */
    public boolean isLast() 
    {
      //return (_rowCount - _firstRowIndex) <= _pageSize;
      return ( _lastRowIndex == _rowCount );
    }
    
    /**
     * The "go to first" button for the current page is enabled
     * based on the current location of the cursor
     * @param
     * @return true/false boolean
     * @exception
     * @roseuid 378FC8F4009F
     */
    public boolean enableFirst() 
    {
      return !isFirst();
    }
    
    /**
     * The "go to last" button for the current page is enabled
     * based on the current location of the cursor
     * @param
     * @return true/false boolean
     * @exception
     * @roseuid 378FC8FE01EE
     */
    public boolean enableLast() 
    {
      return !isLast();
    }
    
    /**
     * The "go to next page" button for the current page is 
     * enabled based on the current location of the cursor
     * @param
     * @return true/false boolean
     * @exception
     * @roseuid 378FC3580040
     */
    public boolean enableNext() 
    {
      return _lastRowIndex < _rowCount;
    }
    
    /**
     * The "go to prev page" button for the current page is 
     * enabled based on the current location of the cursor
     * @param
     * @return true/false boolean
     * @exception
     * @roseuid 378FC358005E
     */
    public boolean enablePrev() 
    {
      return !isFirst();
    }
    
    /**
     * What is the least row number in this current page?
     * @param
     * @return - Least row numer - int
     * @exception
     * @roseuid 378FC358007C
     */
    public int getRangeMin() 
    {
      return _firstRowIndex;
    }
    
    /**
     * What is the highest row number in this current page?
     * @param
     * @return -Highest row numer - int
     * @exception
     * @roseuid 378FC358009A
     */
    public int getRangeMax() 
    {
      return _lastRowIndex;
    }
    
    /**
     * Total number of rows in the record set.
     * @param
     * @return - Total number of rows - int
     * @exception
     * @roseuid 378FC35800B8
     */
    public int getRowCount() 
    {
      return _rowCount;
    }
    
    /**
     * Total number of rows in the current page.
     * @param
     * @return - Total number of rows int the current page - int
     * @exception
     * @roseuid 378FC35800D6
     */
    public int getPageSize() 
    {
      return _pageSize;
    }
}
