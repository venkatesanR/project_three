//Source file: d:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\IteratorStatus.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.io.Serializable;

public class IteratorStatus implements Serializable {
	private boolean _next;
	private boolean _previous;
	private long _startPosition;
	private long _pageSize;
	
	/**
	 * Determines if the _next property is true.
	 * 
	 * @return   <code>true<code> if the _next property is true
	 */
	public boolean getNext() {
        return _next;		
	}
	
	/**
	 * Sets the value of the _next property.
	 * 
	 * @param aNext the new value of the _next property
	 */
	public void setNext(boolean aNext) {
		_next = aNext;
		}
	
	/**
	 * Determines if the _previous property is true.
	 * 
	 * @return   <code>true<code> if the _previous property is true
	 */
	public boolean getPrevious() {
        return _previous;		
	}
	
	/**
	 * Sets the value of the _previous property.
	 * 
	 * @param aPrevious the new value of the _previous property
	 */
	public void setPrevious(boolean aPrevious) {
		_previous = aPrevious;
		}
	
	/**
	 * Access method for the _startPosition property.
	 * 
	 * @return   the current value of the _startPosition property
	 */
	public long getStartPosition() {
        return _startPosition;		
	}
	
	/**
	 * Sets the value of the _startPosition property.
	 * 
	 * @param aStartPosition the new value of the _startPosition property
	 */
	public void setStartPosition(long aStartPosition) {
		_startPosition = aStartPosition;
		}
	
	/**
	 * Access method for the _pageSize property.
	 * 
	 * @return   the current value of the _pageSize property
	 */
	public long getPageSize() {
        return _pageSize;		
	}
	
	/**
	 * Sets the value of the _pageSize property.
	 * 
	 * @param aPageSize the new value of the _pageSize property
	 */
	public void setPageSize(long aPageSize) {
		_pageSize = aPageSize;
		}
}
