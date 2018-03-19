//Source file: d:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\IteratorCriteria.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.io.Serializable;

public class IteratorCriteria implements Serializable {
	private long _startRecord;
	private long _numberOfRecords;
	private String _sortName;
	private String _sortOrder;
	
	/**
	 * @roseuid 3CBB88DE005A
	 */
	public IteratorCriteria() {
      //default conctructor is required to be a bean
      //leave all values to zeroes/nulls		
	}
	
	/**
	 * @param startRecord
	 * @param numberOfRecords
	 * @param sortName
	 * @param sortOrder
	 * @roseuid 3BEC79870057
	 */
	public IteratorCriteria(long startRecord, long numberOfRecords, String sortName, String sortOrder) {
      _startRecord = startRecord;
      _numberOfRecords = numberOfRecords;
      _sortName = sortName;
      _sortOrder = sortOrder;		
	}
	
	/**
	 * Access method for the _startRecord property.
	 * 
	 * @return   the current value of the _startRecord property
	 */
	public long getStartRecord() {
        return _startRecord;		
	}
	
	/**
	 * Sets the value of the _startRecord property.
	 * 
	 * @param aStartRecord the new value of the _startRecord property
	 */
	public void setStartRecord(long aStartRecord) {
		_startRecord = aStartRecord;
		}
	
	/**
	 * Access method for the _numberOfRecords property.
	 * 
	 * @return   the current value of the _numberOfRecords property
	 */
	public long getNumberOfRecords() {
        return _numberOfRecords;		
	}
	
	/**
	 * Sets the value of the _numberOfRecords property.
	 * 
	 * @param aNumberOfRecords the new value of the _numberOfRecords property
	 */
	public void setNumberOfRecords(long aNumberOfRecords) {
		_numberOfRecords = aNumberOfRecords;
		}
	
	/**
	 * Access method for the _sortName property.
	 * 
	 * @return   the current value of the _sortName property
	 */
	public String getSortName() {
      if ( _sortName == null ) { _sortName = new String(); }
      return _sortName;		
	}
	
	/**
	 * Sets the value of the _sortName property.
	 * 
	 * @param aSortName the new value of the _sortName property
	 */
	public void setSortName(String aSortName) {
		_sortName = aSortName;
		}
	
	/**
	 * Access method for the _sortOrder property.
	 * 
	 * @return   the current value of the _sortOrder property
	 */
	public String getSortOrder() {
      if ( _sortOrder == null ) { _sortOrder = new String(); }
        return _sortOrder;		
	}
	
	/**
	 * Sets the value of the _sortOrder property.
	 * 
	 * @param aSortOrder the new value of the _sortOrder property
	 */
	public void setSortOrder(String aSortOrder) {
		_sortOrder = aSortOrder;
		}
}
