//Source file: c:\\projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\RStoObjectConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author AddVal Technology Inc.
 */
public interface RStoObjectConverter 
{
    
    /**
     * returns: boolean to indicate if the result set needs to be advanced outside of 
     * the converter
     * @param rs - a result set to read from
     * @param collection - a collection to put a newly created object to
     * @param skipMode - flag to determine build or not to build the new object
     * @return boolean
     * @throws SQLException
     * @roseuid 3BF011C1030E
     */
    public boolean buildElement(ResultSet rs, Object collection, boolean skipMode) throws SQLException;
}
