
package com.techmania.spboot.dbutils;

import java.sql.Types;

public final interface ColumnDataType {
    int _CDT_NOTYPE = Types.OTHER;
    int _CDT_STRING = Types.VARCHAR;
    int _CDT_INT = Types.INTEGER;
    int _CDT_LONG = Types.BIGINT;
    int _CDT_DOUBLE = Types.DOUBLE;
    int _CDT_FLOAT = Types.FLOAT;
    int _CDT_DATE = Types.DATE;
    int _CDT_DATETIME = Types.TIMESTAMP;
    int _CDT_TIMESTAMP = -6;
    int _CDT_TIME = Types.TIME;
    int _CDT_CHAR = Types.CHAR;
    int _CDT_BOOLEAN = Types.BIT;
    int _CDT_DOW = -2;
    int _CDT_SHORT = Types.SMALLINT;
    int _CDT_KEY = -3;
    int _CDT_LINK = -4;
    int _CDT_VERSION = -8;
    int _CDT_USER = -9;
    int _CDT_ITERATOR = -10;
    int _CDT_OBJECT = -11;
    int _CDT_FILE = -12;
    int _CDT_CLOB = Types.CLOB;
    int _CDT_BLOB = Types.BLOB;
    int _CDT_CREATED_TIMESTAMP = -13;
    int _CDT_CREATED_USER = -14;

    String _CDT_NOTYPE_ = "CDT_NOTYPE";
    String _CDT_STRING_ = "CDT_STRING";
    String _CDT_INT_ = "CDT_INT";
    String _CDT_LONG_ = "CDT_LONG";
    String _CDT_DOUBLE_ = "CDT_DOUBLE";
    String _CDT_FLOAT_ = "CDT_FLOAT";
    String _CDT_DATE_ = "CDT_DATE";
    String _CDT_DATETIME_ = "CDT_DATETIME";
    String _CDT_TIME_ = "CDT_TIME";
    String _CDT_CHAR_ = "CDT_CHAR";
    String _CDT_BOOLEAN_ = "CDT_BOOLEAN";
    String _CDT_DOW_ = "CDT_DOW";
    String _CDT_SHORT_ = "CDT_SHORT";
    String _CDT_KEY_ = "CDT_KEY";
    String _CDT_LINK_ = "CDT_LINK";
    String _CDT_TIMESTAMP_ = "CDT_TIMESTAMP";
    String _CDT_VERSION_ = "CDT_VERSION";
    String _CDT_USER_ = "CDT_USER";
    String _CDT_ITERATOR_ = "CDT_ITERATOR";
    String _CDT_OBJECT_ = "CDT_OBJECT";
    String _CDT_FILE_ = "CDT_FILE";
    String _CDT_CLOB_ = "CDT_CLOB";
    String _CDT_BLOB_ = "CDT_BLOB";
    String _CDT_CREATED_TIMESTAMP_ = "CDT_CREATED_TIMESTAMP";
    String _CDT_CREATED_USER_ = "CDT_CREATED_USER";

    default int getSqlDataType(int type) {

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
