//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBCriteriaColumn.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBCriteriaColumn.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import com.addval.utils.AVConstants;
import com.addval.metadata.ColumnMetaData;

/**
 * EJBCriteria contains a collection of EJBCriteriaColumns representing the each 
 * search criterion
 */
public final class EJBCriteriaColumn extends EJBColumn implements Serializable {
	private int _operator = AVConstants._EQUAL;
	private int _ordering = AVConstants._ASC;
	
	/**
	 * @param name
	 * @param intValue
	 * @param dblValue
	 * @param isNull
	 * @param dateTimeValue
	 * @roseuid 3AF9C20B03C4
	 */
	protected EJBCriteriaColumn(String name, int intValue, double dblValue, boolean isNull, EJBDateTime dateTimeValue) {

		super( name, intValue, dblValue, isNull, dateTimeValue );		
	}
	
	/**
	 * @param columnMetaData
	 * @roseuid 3AFAFD1C00AC
	 */
	public EJBCriteriaColumn(ColumnMetaData columnMetaData) {

		super( columnMetaData );		
	}
	
	/**
	 * @param operator
	 * @roseuid 3B2113070043
	 */
	public void setOperator(int operator) {

		_operator = operator;		
	}
	
	/**
	 * @return int
	 * @roseuid 3B273B4F007A
	 */
	public int getOperator() {

		return _operator;		
	}
	
	/**
	 * @param ordering
	 * @roseuid 3B661836028C
	 */
	protected void setOrdering(int ordering) {

		_ordering = ordering;		
	}
	
	/**
	 * @return int
	 * @roseuid 3B66183602B4
	 */
	public int getOrdering() {

		return _ordering;		
	}
}
