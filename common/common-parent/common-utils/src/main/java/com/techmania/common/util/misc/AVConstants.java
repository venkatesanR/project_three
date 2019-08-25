//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\utils\\AVConstants.java

/**
 * Copyright
 * AddVal Technology Inc.
 * <p>
 * Copyright
 * AddVal Technology Inc.
 * <p>
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: C:\\Projects\\Common\\src\\client\\source\\com\\addval\\utils\\AVConstants.java
/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.techmania.common.util.misc;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class AVConstants {
    public static final transient String _INSERT = "INSERT";
    public static final transient String _DELETE = "DELETE";
    public static final transient String _UPDATE = "UPDATE";
    public static final transient String _DEFAULT = "DEFAULT";
    public static final transient String _DELIMITER = "||";
    public static final transient char _EQUAL_SIGN = '=';
    public static final transient String _NOT_EQUAL_SIGN = "<>";
    public static final transient char _GREATER_SIGN = '>';
    public static final transient char _LESSER_SIGN = '<';
    public static final transient String _GREATER_EQUAL_SIGN = ">=";
    public static final transient String _LESSER_EQUAL_SIGN = "<=";
    public static final transient String _LIKE_SIGN = "LIKE";
    public static final transient String _LIKE_IN_SIGN = "LIKE_IN";
    public static final transient String _IN_SIGN = "IN";

    public static final transient String _CFG_PROP_EQUAL_SIGN = "sign.equal";
    public static final transient String _CFG_PROP_NOT_EQUAL_SIGN = "sign.notequal";
    public static final transient String _CFG_PROP_GREATER_SIGN = "sign.greater";
    public static final transient String _CFG_PROP_LESSER_SIGN = "sign.lesser";
    public static final transient String _CFG_PROP_GREATER_EQUAL_SIGN = "sign.greaterequal";
    public static final transient String _CFG_PROP_LESSER_EQUAL_SIGN = "sign.lesserequal";

    public static final transient String _CFG_PROP_INT_EQUAL_SIGN = "int.sign.equal";
    public static final transient String _CFG_PROP_INT_GREATER_SIGN = "int.sign.greater";
    public static final transient String _CFG_PROP_INT_LESSER_SIGN = "int.sign.lesser";
    public static final transient String _CFG_PROP_INT_GREATER_EQUAL_SIGN = "int.sign.greaterequal";
    public static final transient String _CFG_PROP_INT_LESSER_EQUAL_SIGN = "int.sign.lesserequal";

    public static final transient int _EQUAL = 0;
    public static final transient int _NOT_EQUAL = -1;
    public static final transient int _GREATER = 100;
    public static final transient int _GREATER_EQUAL = 101;
    public static final transient int _LESSER = -100;
    public static final transient int _LESSER_EQUAL = -101;
    public static final transient int _LIKE = 1000;
    public static final transient int _IN = 1001;
    public static final transient int _CONTAINS = 1002;
    public static final transient int _ENDS = 1003;
    public static final transient int _LIKE_IN = 1004;

    public static final transient int _AND = 501;
    public static final transient int _OR = 502;

    public static final transient String _PROPERTY_FILE = "avcommon";
    public static final transient String _METADATA_SERVER = "ejb/AVMetaDataServer";
    public static final transient String _KEY_SUFFIX = "_KEY";
    public static final transient String _ID_SUFFIX = "_ID";
    public static final transient int _NO_AGGREGATE = 0;
    public static final transient int _SUM = 100;
    public static final transient int _AVG = 101;
    public static final transient int _MIN = 102;
    public static final transient int _MAX = 103;
    public static final transient int _ENDS_WITH = 1;
    public static final transient int _STARTS_WITH = -1;
    public static final transient int _ASC = 1;
    public static final transient int _DESC = -1;
    public static final transient int _FETCH_FIRST = -2;
    public static final transient int _FETCH_LAST = 2;
    public static final transient int _FETCH_FORWARD = 1;
    public static final transient int _FETCH_BACKWARD = -1;
    public static final transient int _UNDEF = 0;
    public static final transient String _ENC_PREFIX = "java:comp/env/";
    public static final transient String _DATASOURCE = "jdbc/AVDataSource";
    public static final transient String _INITIALIZE_ENV = "INITIALIZE_ENV";
    public static final transient String _DATASOURCE_ENC_PREFIX = _ENC_PREFIX + "jdbc/";
    public static final transient String _EJB_ENC_PREFIX = _ENC_PREFIX + "ejb/";
    public static final String _SQLSERVER = "SQLSERVER";
    public static final String _MSACCESS = "MSACCESS";
    public static final String _ORACLE = "ORACLE";
    public static final String _POSTGRES = "POSTGRES";
    public static final String _SQLITE = "SQLITE";
    public static final TimeZone _LOCAL_TIMEZONE = TimeZone.getDefault();
    public static final TimeZone _GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    public static final int _LOCAL_TIMEZONE_OFFSET = _LOCAL_TIMEZONE.getRawOffset();
    public static final GregorianCalendar _GMT_CALENDAR = new GregorianCalendar(_GMT_TIMEZONE);
    public static final boolean _LOCAL_TIMEZONE_USEDST = _LOCAL_TIMEZONE.useDaylightTime();
    public static final int _LOCAL_DSTSAVINGS = SimpleTimeZone.getDefault().getDSTSavings();

    public static HashSet _TIME_FORMATS = new HashSet();

    static {
        _TIME_FORMATS.add("HH24MI");
    }

    public static final String _HHMM_colon = "hh:mm";
    public static final String _HHMM = "hhmm";
}
