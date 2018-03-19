/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */
package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import com.addval.metadata.RecordStatus;
import com.addval.utils.XRuntime;
import java.util.Vector;
import org.w3c.dom.Node;

/**
 * An internal object that contains information on one single record in an
 * EJBResultset
 */
public class EJBRecord implements Serializable 
{
	private static final long serialVersionUID = -354045724267658744L;
	private static final transient String _module = "EJBRecord";
	private int _status = RecordStatus._RMS_UNCHANGED;
	private int _syncStatus = RecordStatus._RSS_UNSYNC;
	private transient int _lastIndex = 0;
	private String _errorMsg = null;
	private Vector _columns = null;

	/**
	 * @roseuid 3B8316AC015A
	 */
	public EJBRecord() {

		 _columns = new Vector();
	}

	/**
	 * @param columns
	 * @roseuid 3AF8319E0358
	 */
	public EJBRecord(Vector columns) {

        _columns = columns;
	}

	/**
	 * @param columnName
	 * @return com.addval.ejbutils.dbutils.EJBColumn
	 */
	public EJBColumn getColumn(String columnName) 
	{
		for (int i=1; i<=getColumns().size(); i++) {
			EJBColumn column = (EJBColumn)getColumn( i );
			if (column.getName().equals( columnName ))
				return column;
		}
		return null;
	}

	/**
	 * @param index
	 * @return com.addval.ejbutils.dbutils.EJBColumn
	 * @roseuid 3AF831C2025F
	 */
	public EJBColumn getColumn(int index) {

        try {

            _lastIndex = index;
            return (EJBColumn)_columns.elementAt( index - 1 );
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new XRuntime( _module, "Index starts with 1 and ends with ColumnCount. Index is invalid : " + String.valueOf( index ) + " : " + e.getMessage() );
        }
	}

	/**
	 * Returns the last column that was looked up
	 * @return com.addval.ejbutils.dbutils.EJBColumn
	 * @roseuid 3B02DC860398
	 */
	public EJBColumn getColumn() {

        return getColumn( _lastIndex );
	}

	/**
	 * @param column
	 * @roseuid 3B05CF500252
	 */
	public void addColumn(EJBColumn column) {

        _columns.add( column );
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3AF831DF02CF
	 */
	public Vector getColumns() {

        return _columns;
	}

	/**
	 * @return int
	 * @roseuid 3AF8325400F6
	 */
	public int getStatus() {

        return _status;
	}

	/**
	 * @param v
	 * @roseuid 3AF832540146
	 */
	public void setStatus(int v) {

        _status = v;
	}

	/**
	 * @return int
	 * @roseuid 3AF832540273
	 */
	public int getSyncStatus() {

        return _syncStatus;
	}

	/**
	 * @param v
	 * @roseuid 3AF8325402F5
	 */
	public void setSyncStatus(int v) {

        _syncStatus = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B8313B901CC
	 */
	public String getErrorMsg() {

		return _errorMsg;
	}

	/**
	 * @param v
	 * @roseuid 3B8313C003B7
	 */
	public void setErrorMsg(String v) {

		_errorMsg = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C8D380D0184
	 */
	public synchronized String toXML() {

		StringBuffer xml 	= new StringBuffer();
		final String SPACE 	= " ";

		xml.append( "<record" + SPACE );
		xml.append( "status=" 		+ "\"" + getStatus() 	 + "\""  + SPACE );
		xml.append( "syncstatus=" 	+ "\"" + getSyncStatus() + "\""  + SPACE );
		xml.append( "errorMsg=" 	+ "\"" + getErrorMsg() 	 + "\">" + SPACE );
		xml.append( System.getProperty( "line.separator" ) );

		Vector columns = getColumns();
		int size = columns == null ? 0 : columns.size();

		for (int index = 0; index < size; index++)
			xml.append( ((EJBColumn)columns.elementAt( index )).toXML() );

		xml.append( "</record>" );

		return xml.toString();
	}

	/**
	 * @param node
	 * @roseuid 3C8D380D01AC
	 */
	public void fromXML(Node node) {

	}
}
