//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\DAOUtils.java

//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOUtils.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import org.apache.commons.beanutils.WrapDynaBean;
import com.addval.utils.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;

import com.addval.metadata.ColumnDataType;
import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Connection;
import org.apache.commons.beanutils.DynaProperty;

/**
 * @author AddVal Technology Inc.
 */
public class DAOUtils
{
	private static final String _module = "com.addval.dbutils.DAOUtils";
	private String _serverType = null;
	private DAOSQLStatement _statement = null;

	/**
	 * @param statement
	 * @param serverType
	 * @roseuid 3EAD5FA1031C
	 */
	public DAOUtils(DAOSQLStatement statement, String serverType)
	{
		_statement  = statement;
		_serverType = serverType;
	}

	/**
	 * Access method for the _serverType property.
	 *
	 * @return   the current value of the _serverType property
	 */
	public String getServerType()
	{
		return _serverType;
	}

	/**
	 * Sets the value of the _serverType property.
	 *
	 * @param aServerType the new value of the _serverType property
	 */
	public void setServerType(String aServerType)
	{
		_serverType = aServerType;
	}

	/**
	 * Access method for the _statement property.
	 *
	 * @return   the current value of the _statement property
	 */
	public DAOSQLStatement getStatement()
	{
		return _statement;
	}

	/**
	 * Sets the value of the _statement property.
	 *
	 * @param aStatement the new value of the _statement property
	 */
	public void setStatement(DAOSQLStatement aStatement)
	{
		_statement = aStatement;
	}

	/**
	 * Sets the properties in the PreparedStatement given a Java bean object with the
	 * required search criteria
	 * @param pstmt
	 * @param instance
	 * @throws java.sql.SQLException
	 * @roseuid 3EAD60200109
	 */
	public void setProperties(PreparedStatement pstmt, Object instance) throws SQLException
	{
		if (instance == null)
			return;
		if (instance instanceof Map) {
			setProperties( pstmt, (Map)instance);
			return;
		}
		WrapDynaBean bean   = null;
		DAOParam 	 param  = null;
		Object 		 value  = null;
		Vector 		 params = getStatement().getCriteriaParams();
		int 		 size   = params == null ? 0 : params.size();

		for (int index = 0; index < size; index++) {

			if (bean == null)
				bean = new WrapDynaBean( instance );

			param = (DAOParam)params.elementAt( index );
			if (param.hasInMode() && bean.getDynaClass().getDynaProperty( param.getName() ) != null) {
				value = bean.get( param.getName() );
				index = setPreparedStatementValue( pstmt, param, value, bean, null ) - 1;
			}
		}
	}

	/**
	 * Sets the properties in the PreparedStatement given a Hashtable of name-value
	 * pairs
	 * @param pstmt
	 * @param values
	 * @throws java.sql.SQLException
	 * @roseuid 3EAF20A601B5
	 */
	public void setProperties(PreparedStatement pstmt, Map values) throws SQLException
	{
		if (values != null) {

			DAOParam 	 param  = null;
			Object 		 value  = null;
			Vector 		 params = getStatement().getCriteriaParams();
			int 		 size   = params == null ? 0 : params.size();

			for (int index = 0; index < size; index++) {
				param = (DAOParam)params.elementAt( index );
				value = values.get( param.getName() );
				// Since it is a Map interface, null values can be passed to set NULL for columns in DB
				// If this check is removed,
				// the clients cannot call setProperties multiple times with one object
				// having the value and the other not
				// SO DO NOT REMOVE THIS CHECK
				if (param.hasInMode() && values.containsKey( param.getName() )) {
					index = setPreparedStatementValue( pstmt, param, value, null, values ) - 1;
                }
			}
		}
	}

	public Object getProperties(ResultSet rs, Class<?> clazz) throws IllegalAccessException, InstantiationException, SQLException
	{
		Object instance = clazz.newInstance();
		getProperties( rs, instance );
		return instance;
	}
	
	/**
	 * @param rs
	 * @param instance
	 * @throws java.sql.SQLException
	 * @roseuid 3EAD604701A5
	 */
	public void getProperties(ResultSet rs, Object instance) throws SQLException
	{
		WrapDynaBean 		   wrapBean = null;
		ConvertingWrapDynaBean bean   	= new ConvertingWrapDynaBean( instance );
		Vector 		 		   params 	= getStatement().getSelectParams();
		int 		 		   size   	= params == null ? 0 : params.size();
//		String		 		   name	  	= null;
		DAOParam 	 		   param  	= null;
		Object				   value	= null;
		DynaProperty		   prop		= null;

		for (int index = 0; index < size; index++) {

			// If there is no property by that name then the set method does not throw IllegalArgumentException
			param = (DAOParam)params.elementAt( index );
			value = getResultSetValue( rs, param );

			// This condition is added because ConvertingWrapDynaBean will always set
			// default values even when the object has a NON-primitive get/set method
			if (value == null) {

				prop = bean.getDynaClass().getDynaProperty( param.getName() );

				if (prop != null) {

					if (prop.getType().isPrimitive()) {

						bean.set( param.getName(), value );
					}
					else {

						if (wrapBean == null)
							wrapBean = new WrapDynaBean( instance );

						wrapBean.set( param.getName(), value );
					}
				}
			}
			else {

				bean.set( param.getName(), value );
			}
		}
	}

	/**
	 * @param rs
	 * @param propertyName
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3EB198E80213
	 */
	public Object getProperty(ResultSet rs, String propertyName) throws SQLException
	{
		DAOParam param = getStatement().getSelectParamIndex( propertyName );

		if (param == null)
			throw new XRuntime( _module, "The specified property : " + propertyName + " is not valid. Check that the property exists in the DAOSQL file and has the same name as the property passed in." );

		return getResultSetValue( rs, param );
	}

	/**
	 * @param cstmt
	 * @param instance
	 * @throws java.sql.SQLException
	 * @roseuid 3F25D209029F
	 */
	public void setProperties(CallableStatement cstmt, Object instance) throws SQLException
	{
		// Call setProperties for the CriteriaParams
		setProperties( (PreparedStatement)cstmt, instance );

		// For the SelectParams, register the out parameters
		Vector	 params = getStatement().getCriteriaParams();
		int 	 size   = params == null ? 0 : params.size();
		DAOParam param  = null;

		for (int index = 0; index < size; index++) {

			param = (DAOParam)params.elementAt( index );
			if (param.hasOutMode())
				cstmt.registerOutParameter( param.getIndex(), ColumnDataType.getSqlDataType( param.getColumnType() ) );
		}
	}

	/**
	 * @param cstmt
	 * @param values
	 * @throws java.sql.SQLException
	 * @roseuid 3F25D20C037A
	 */
	public void setProperties(CallableStatement cstmt, Map values) throws SQLException
	{
		// Call setProperties for the CriteriaParams
		setProperties( (PreparedStatement)cstmt, values );

		// For the SelectParams, register the out parameters
		Vector	 params = getStatement().getCriteriaParams();
		int 	 size   = params == null ? 0 : params.size();
		DAOParam param  = null;

		for (int index = 0; index < size; index++) {

			param = (DAOParam)params.elementAt( index );
			if (param.hasOutMode())
				cstmt.registerOutParameter( param.getIndex(), ColumnDataType.getSqlDataType( param.getColumnType() ) );
		}
	}

	/**
	 * @param cstmt
	 * @param propertyName
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3F25D20902A2
	 */
	public Object getProperty(CallableStatement cstmt, String propertyName) throws SQLException
	{
		// There should not be too many out parameters in a typical callable statement
		// hence the overhead of adding a Hashtable _criteriaParams in DAOParam has been
		// avoided and instead the params are looped through
		Vector 	 params = getStatement().getCriteriaParams();
		int 	 size   = params == null ? 0 : params.size();
//		String	 name	= null;
		DAOParam param  = null;

		for (int index = 0; index < size; index++) {

			param = (DAOParam)params.elementAt( index );
			if (param.getName().equals( propertyName ))
				break;
			else
				param = null;
		}

		if (param == null || !param.hasOutMode())
			throw new XRuntime( _module, "The specified property : " + propertyName + " is not valid. Check that the property exists in the DAOSQL file, registered as OUT parameter and has the same name as the property passed in." );

		return cstmt.getObject( param.getIndex() );
	}

	/**
	 * @param cstmt
	 * @param instance
	 * @throws java.sql.SQLException
	 * @roseuid 3F25D2400222
	 */
	public void getProperties(CallableStatement cstmt, Object instance) throws SQLException
	{
		ConvertingWrapDynaBean bean   = new ConvertingWrapDynaBean( instance );
		Vector 		 		   params = getStatement().getSelectParams();
		int 		 		   size   = params == null ? 0 : params.size();
//		String		 		   name	  = null;
		DAOParam 	 		   param  = null;

		for (int index = 0; index < size; index++) {

			// If there is no property by that name then the set method does not throw IllegalArgumentException
			param = (DAOParam)params.elementAt( index );
			if (param.hasOutMode())
				bean.set( param.getName(), cstmt.getObject( param.getIndex() ) );
		}
	}

	/**
	 * @param pstmt
	 * @param param
	 * @param value
	 * @param bean
	 * @param values
	 * @throws java.sql.SQLException
	 * @roseuid 3EAF219D037A
	 */
	private int setPreparedStatementValue(PreparedStatement pstmt, DAOParam param, Object value, WrapDynaBean bean, Map values) throws SQLException
	{
		int index = param.getIndex();
		// Handle Nulls
		//if (value == null || value.toString().length() == 0) {

		if (value == null) {
			if (param.getIsCollection()) {
				for(int i=0; i<param.getMaxInCount(); i++) {
					pstmt.setNull( index, param.getColumnType() );
					index++;
				}
				return index-1;
			}
			if (param.getColumnType() == ColumnDataType._CDT_TIMESTAMP) {
				Timestamp timestamp = new Timestamp( DateUtl.getTimestamp().getTime() );
				pstmt.setTimestamp( index, timestamp, AVConstants._GMT_CALENDAR );
				if (bean != null)
					bean.set( param.getName(), timestamp );
				if (values != null)
					values.put( param.getName(), timestamp );
			}
			else {
				pstmt.setNull( index, param.getColumnType() );
			}
			return index;
		}

        if (!(value instanceof Collection)) {
            setValue(pstmt, index, param, value, bean, values);
            return index;
        }

        Collection collection = (Collection)value;
		if (collection.size() > param.getMaxInCount())
            throw new XRuntime( _module, "The specified MaxInCount for " + param.getName() + " is " + param.getMaxInCount() + ". It is less than " + param.getName() +" Object Size " + collection.size());

        Iterator iter = collection.iterator();

		//Setting Object Value into Param
        for (int j=0; j<param.getMaxInCount(); j++) {
            Object data = null;
            if (iter.hasNext()) {
                 data = iter.next();
            }
            if (data != null)
                //changed data.toString() to data. data.toString() can accept only String in the 'IN' parameter. Now data object can accept any type. Praveen: 19th May, 2006
                setValue( pstmt, index, param, data, bean, values );
            else    //Setting null values to the param 
                pstmt.setNull( index, param.getColumnType() );
             index++;
        }

        return index-1;
	}


	private void setValue(PreparedStatement pstmt, int index, DAOParam param, Object value, WrapDynaBean bean, Map values) throws SQLException
	{
		int type = param.getColumnType();
		switch (type) {
			case ColumnDataType._CDT_STRING:
			case ColumnDataType._CDT_CHAR:
			case ColumnDataType._CDT_USER:
                if(param.hasMaxSize()){
                    String val = StrUtl.truncate((String)value, param.getMaxSize());
                    pstmt.setString( index, val );
                }
                else{
                    pstmt.setString( index, (String)value );
                }
				break;

			case ColumnDataType._CDT_CLOB:
				String input = (String)value;
				pstmt.setCharacterStream( index, DBUtl.stringToClob(input, param.getName()), input.length() );
				break;
			case ColumnDataType._CDT_BLOB:
				if(value instanceof byte[]){
				    byte[] input2 = (byte[])value;
					pstmt.setBytes(index, input2);
				}
				else {
					String input2 = (String)value;
					pstmt.setCharacterStream( index, DBUtl.stringToClob(input2, param.getName()), input2.length() );
				}
				break;

			case ColumnDataType._CDT_INT:
				pstmt.setInt( index, ((Integer)value).intValue() );
				break;

			case ColumnDataType._CDT_LONG:
			case ColumnDataType._CDT_VERSION:
				pstmt.setLong( index, ((Long)value).longValue() );
				break;

			case ColumnDataType._CDT_DOUBLE:
                if(param.hasPrecision()){
                    double val = MathUtl.round(((Double)value).doubleValue(), param.getPrecision());
                    pstmt.setDouble( index, val);
                }
                else{
                    pstmt.setDouble( index, ((Double)value).doubleValue() );
                }
				break;

			case ColumnDataType._CDT_FLOAT :
                if(param.hasPrecision()){
                    float val = (float)MathUtl.round(((Double)value).floatValue(), param.getPrecision());
                    pstmt.setFloat( index, val);
                }
                else{
                    pstmt.setFloat( index, ((Float)value).floatValue() );
                }
				break;

			case ColumnDataType._CDT_DATE:
				pstmt.setDate( index, new Date( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
				break;

			case ColumnDataType._CDT_DATETIME :
				pstmt.setTimestamp( index, new Timestamp( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
				break;

			case ColumnDataType._CDT_TIMESTAMP :
				// If timestamp was passed in use that value
				//pstmt.setTimestamp( index, new Timestamp( DateUtl.getTimestamp().getTime() ), AVConstants._GMT_CALENDAR );
				pstmt.setTimestamp( index, new Timestamp( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
				break;

			case ColumnDataType._CDT_TIME:
				pstmt.setTime( index, new Time( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
				break;

			case ColumnDataType._CDT_BOOLEAN :
				pstmt.setBoolean( index, ((Boolean)value).booleanValue() );
				break;

			case ColumnDataType._CDT_KEY :
				// If the key value is specified in the input the sequence will not be invoked
				int key = ((Integer)value).intValue();
				if (key > 0) {
					pstmt.setInt( index, key );
				}
				else {
					key = getNextSequence( pstmt.getConnection(), param.getSequenceName() );
					pstmt.setInt( index, key );

					if (bean != null)
						bean.set( param.getName(), new Integer( key ) );

					if (values != null)
						values.put( param.getName(), new Integer( key ) );
				}

				break;
			default:
				throw new XRuntime( _module, "Column Type " + param.getColumnType() + " is not recognized for :" + param.getName() );

		}
	}

	/**
	 * @param rs
	 * @param param
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3EB19DA40157
	 */
	private Object getResultSetValue(ResultSet rs, DAOParam param) throws SQLException
	{
		Object value = null;

		int index = param.getIndex();

		// Handle Nulls
		if (rs.getString( index ) != null) {

			switch (param.getColumnType()) {

				case ColumnDataType._CDT_CLOB :
					value = DBUtl.clobToString( rs.getClob(index), param.getName() );
					break;
				case ColumnDataType._CDT_BLOB :
					value = DBUtl.blobToString( rs.getBlob(index), param.getName() );
					break;
				case ColumnDataType._CDT_STRING :
				case ColumnDataType._CDT_CHAR :
				case ColumnDataType._CDT_USER :
					value = rs.getString( index );
					break;

				case ColumnDataType._CDT_INT :
				case ColumnDataType._CDT_KEY :
					value = new Integer( rs.getInt( index ) );
					break;

				case ColumnDataType._CDT_LONG :
				case ColumnDataType._CDT_VERSION:
					value = new Long( rs.getLong( index ) );
					break;

				case ColumnDataType._CDT_DOUBLE :
					value = new Double( rs.getDouble( index ) );
					break;

				case ColumnDataType._CDT_FLOAT :
					value = new Float( rs.getFloat( index ) );
					break;

				case ColumnDataType._CDT_DATE :
					value = rs.getDate( index, AVConstants._GMT_CALENDAR );
					break;

				case ColumnDataType._CDT_DATETIME :
				case ColumnDataType._CDT_TIMESTAMP :
					value = rs.getTimestamp( param.getIndex(), AVConstants._GMT_CALENDAR );
					break;

				case ColumnDataType._CDT_TIME :
					value = rs.getTime( param.getIndex(), AVConstants._GMT_CALENDAR );
					break;

				case ColumnDataType._CDT_BOOLEAN :
					//value = new Boolean( rs.getBoolean( index ) );
					value = new Boolean( rs.getInt( index ) == 1 );
					break;
				case ColumnDataType._CDT_ITERATOR :
					value=rs.getObject(index);
					break;
				default:
					throw new XRuntime(  _module, "Column Type " + param.getColumnType() + " is not recognized for :" + param.getName() );
			}
		}

		return value;
	}

	/**
	 * @param conn
	 * @param sequenceName
	 * @return int
	 * @roseuid 3EB1A654034B
	 */
	private int getNextSequence(Connection conn, String sequenceName)
	{
		return TableManager.getNextID( sequenceName, conn, getServerType() );
	}

	// useful method for getting back the result set as a Map
	public Map getMap(ResultSet rs) throws SQLException
	{
		return getProperties( rs, (Map)null );
	}

	/**
	 * @param rs
	 * @param instance
	 * @throws java.sql.SQLException
	 * @roseuid 3EAD604701A5
	 */
	// useful method for getting back the result set as a Map
	public Map getProperties(ResultSet rs, Map map) throws SQLException
	{
		if (map == null)
			map = new HashMap();
		Vector 		 		   params 	= getStatement().getSelectParams();
		int 		 		   size   	= params == null ? 0 : params.size();
		Object				   value	= null;
		for (int index = 0; index < size; index++) {
			// If there is no property by that name then the set method does not throw IllegalArgumentException
			DAOParam param = (DAOParam)params.get( index );
			value = getResultSetValue( rs, param );
			map.put( param.getName(), value );
		}
		return map;
	}
}
