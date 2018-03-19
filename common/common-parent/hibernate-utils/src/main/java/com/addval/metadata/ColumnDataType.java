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

package com.addval.metadata;

import java.io.Serializable;
import java.sql.Types;

import com.addval.utils.XRuntime;

public final class ColumnDataType implements Serializable {
	private static final long serialVersionUID = -8761047953762311788L;
	// private static final String _module = "com.addval.metadata.ColumnDataType";
	public static final int _CDT_NOTYPE = Types.OTHER;
	public static final int _CDT_STRING = Types.VARCHAR;
	public static final int _CDT_INT = Types.INTEGER;
	public static final int _CDT_LONG = Types.BIGINT;
	public static final int _CDT_DOUBLE = Types.DOUBLE;
	public static final int _CDT_FLOAT = Types.FLOAT;
	public static final int _CDT_DATE = Types.DATE;
	public static final int _CDT_DATETIME = Types.TIMESTAMP;
	public static final int _CDT_TIMESTAMP = -6;
	public static final int _CDT_TIME = Types.TIME;
	public static final int _CDT_CHAR = Types.CHAR;
	public static final int _CDT_BOOLEAN = Types.BIT;
	public static final int _CDT_DOW = -2;
	public static final int _CDT_SHORT = Types.SMALLINT;
	public static final int _CDT_KEY = -3;
	public static final int _CDT_LINK = -4;
	public static final int _CDT_VERSION = -8;
	public static final int _CDT_USER = -9;
	public static final int _CDT_ITERATOR = -10;
	public static final int _CDT_OBJECT = -11;
	public static final int _CDT_FILE = -12;
	public static final int _CDT_CLOB = Types.CLOB;
	public static final int _CDT_BLOB = Types.BLOB;
	public static final int _CDT_CREATED_TIMESTAMP = -13;
	public static final int _CDT_CREATED_USER = -14;

	public static final String _CDT_NOTYPE_ = "CDT_NOTYPE";
	public static final String _CDT_STRING_ = "CDT_STRING";
	public static final String _CDT_INT_ = "CDT_INT";
	public static final String _CDT_LONG_ = "CDT_LONG";
	public static final String _CDT_DOUBLE_ = "CDT_DOUBLE";
	public static final String _CDT_FLOAT_ = "CDT_FLOAT";
	public static final String _CDT_DATE_ = "CDT_DATE";
	public static final String _CDT_DATETIME_ = "CDT_DATETIME";
	public static final String _CDT_TIME_ = "CDT_TIME";
	public static final String _CDT_CHAR_ = "CDT_CHAR";
	public static final String _CDT_BOOLEAN_ = "CDT_BOOLEAN";
	public static final String _CDT_DOW_ = "CDT_DOW";
	public static final String _CDT_SHORT_ = "CDT_SHORT";
	public static final String _CDT_KEY_ = "CDT_KEY";
	public static final String _CDT_LINK_ = "CDT_LINK";
	public static final String _CDT_TIMESTAMP_ = "CDT_TIMESTAMP";
	public static final String _CDT_VERSION_ = "CDT_VERSION";
	public static final String _CDT_USER_ = "CDT_USER";
	public static final String _CDT_ITERATOR_ = "CDT_ITERATOR";
	public static final String _CDT_OBJECT_ = "CDT_OBJECT";
	public static final String _CDT_FILE_ = "CDT_FILE";
	public static final String _CDT_CLOB_ = "CDT_CLOB";
	public static final String _CDT_BLOB_ = "CDT_BLOB";
	public static final String _CDT_CREATED_TIMESTAMP_ = "CDT_CREATED_TIMESTAMP";
	public static final String _CDT_CREATED_USER_ = "CDT_CREATED_USER";

	/**
	 * @param type
	 * @return int
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3F25EED50167
	 */
	public static final int getSqlDataType(int type) throws XRuntime {

		// Default datatype = what ever was passed in
		// For certain types alone map the SQL data type
		int sqlType = type;

		switch (type) {

		case ColumnDataType._CDT_CREATED_TIMESTAMP:
		case ColumnDataType._CDT_TIMESTAMP:
			sqlType = ColumnDataType._CDT_DATETIME;
			break;

		case ColumnDataType._CDT_DOW:
		case ColumnDataType._CDT_KEY:
		case ColumnDataType._CDT_VERSION:
			sqlType = _CDT_INT;
			break;

		case ColumnDataType._CDT_LINK:
		case ColumnDataType._CDT_USER:
		case ColumnDataType._CDT_CREATED_USER:
		case ColumnDataType._CDT_FILE:
			sqlType = _CDT_STRING;
			break;
		}

		return sqlType;
	}
}
