//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\struts\\EjbUtils.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EjbUtils.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.struts;

import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.utils.AVConstants;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class to collect information from a HTTP request (parameters or
 * attributes) and populate EJBResultset, EJBCriteria etc.
 * The class relies on some naming conventions followed by the AddVal tag
 * libraries for the request parameters.
 * parameter names that end with _lookUp are search criteria that needs to be used
 * in where clauses
 * parameter names that end with operator_lookUp can be used to specify >, <, =,
 * <> as operators in where clauses
 * parameter names that end with _KEY are used as primary keys to lookup for
 * editing records
 * parameter called SORT_NAME or sort_Name is used to specify the order by column
 * parameter called SORT_ORDER or sort_Order is used to specify the ascending
 * (ASC) or descending (DESC) order
 * Other columns that are present in the metadata
 */
public class EjbUtils
{

	/**
	 * @param editormetadata
	 * @param request
	 * @param forList
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3DCC799801D6
	 */
	public static EJBCriteria getEJBCriteria(EditorMetaData editormetadata, HttpServletRequest request, boolean forList)
	{
	    EJBCriteria ejbCriteria = ( new EJBResultSetMetaData(editormetadata) ).getNewCriteria();
	    if ( forList ) {
	        ejbCriteria.where ( buildLookUpWhere(request, editormetadata), getOperator(request, editormetadata) );
	        ejbCriteria.orderBy ( setOrderBy(editormetadata,request) );
	    }
	    else {
	        ejbCriteria.where ( buildEditWhere(request, editormetadata), getOperator(request, editormetadata) );
	    }
		String queueCriteria = (String) request.getAttribute( "QUEUE_CRITERIA" );
		if ( queueCriteria != null && queueCriteria.length( ) != 0 )
			ejbCriteria.setQueueCriteria(queueCriteria);
	    return ejbCriteria;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return java.util.Hashtable
	 * @roseuid 3DCC7998035C
	 */
	public static Hashtable buildLookUpWhere(HttpServletRequest request, EditorMetaData metadata)
	{
	    Hashtable where = new Hashtable( );
        boolean isExactSearch = true;
        if(request.getAttribute("exactSearch") != null) {
            isExactSearch = ((Boolean)request.getAttribute("exactSearch")).booleanValue();
        }
        ColumnMetaData columnMetaData;

	    for ( Enumeration enumeration = request.getParameterNames( ); enumeration.hasMoreElements( ); ) {
	        String columnName = ( String ) enumeration.nextElement( );
			// System.out.println("Column Name is: " + columnName);
	        if (columnName != null && columnName.length( ) != 0 )
	          if ( columnName.endsWith( "_lookUp" ) && !columnName.endsWith( "operator_lookUp" ) ) {
	            String columnValue = request.getParameter( columnName ).trim( );
	            String actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "_lookUp" ) );
	            if ( columnValue == null || columnValue.trim().length( ) == 0 || (metadata.getColumnMetaData(actColumnName) == null) )
					continue;
                    columnMetaData = metadata.getColumnMetaData(actColumnName);
                    if(columnMetaData != null) {
						if(columnMetaData.getType() == ColumnDataType._CDT_STRING ) {
							if(isExactSearch)
								where.put( actColumnName, columnValue.trim() );
							else
								where.put ( actColumnName, columnValue.trim()+"%" );
						 }
						 else {
							where.put( actColumnName, columnValue.trim() );
						 }
					}
	          }
	    }
	    for ( Enumeration enumeration = request.getAttributeNames( ); enumeration.hasMoreElements( ); ) {
	        String columnName = ( String ) enumeration.nextElement( );
	        if ( columnName.endsWith( "_lookUp" ) && !columnName.endsWith( "operator_lookUp" ) ) {
	            String columnValue = (String) request.getAttribute( columnName );
	            String actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "_lookUp" ) );

	            if ( columnValue != null && columnValue.trim().length( ) != 0 && (metadata.getColumnMetaData(actColumnName) != null) )
	                where.put( actColumnName, columnValue.trim() );
	        }
	    }
		// Now add any secure columns (that are not searchable) to the lookup
		// Searchable columns would already be added by the above code
        Vector 	 		columnsMetaData = new Vector();
        Iterator 		iterator 		= metadata.getColumnsMetaData().iterator();

        //System.out.println ( "Now checking for secure columns to add to lookup" );
        Hashtable secureValues = new Hashtable();
        while (iterator.hasNext()) {

            columnMetaData = (ColumnMetaData)iterator.next();
            if (columnMetaData.isSecured() && !columnMetaData.isSearchable() ) {
				// find the value for this secure column
	            String columnValue = (String)request.getAttribute( columnMetaData.getName() + "_lookUp" );
		        //System.out.println ( "  Found a secure column to add to lookup" + columnMetaData.getName() + ":value:" + columnValue );
	            if ( columnValue != null && columnValue.trim().length( ) != 0 )
	                where.put( columnMetaData.getName(), columnValue.trim() );
			}
        }
		//System.out.println("After Computing where ");
	    return where;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return java.util.Hashtable
	 * @roseuid 3DCC799900E7
	 */
	public static Hashtable buildEditWhere(HttpServletRequest request, EditorMetaData metadata)
	{
		// SATYA commented it out because of undesirable side effects when looking up
		// a result set for editing. For editing the lookup should strictly be based on
		// keys

       // Hashtable where = buildLookUpWhere( request, metadata );

        Hashtable where = new Hashtable();
		String columnName = null;
		String columnValue = null;
		String actColumnName = null;

		for ( Enumeration enumeration = request.getParameterNames( ); enumeration.hasMoreElements( ); ) {

            columnName = ( String ) enumeration.nextElement( );
			if ( columnName.endsWith( "_KEY" ) ) {
                columnValue = request.getParameter( columnName );
                actColumnName = columnName.toUpperCase().substring( 0, columnName.lastIndexOf("_KEY") );
                if ( columnValue != null && columnValue.length() > 0 && (metadata.getColumnMetaData(actColumnName) != null) && metadata.getColumnMetaData(actColumnName).getType() != ColumnDataType._CDT_TIMESTAMP )
                    where.put(actColumnName , columnValue.trim() );
            }
        }

		for ( Enumeration enumeration = request.getAttributeNames( ); enumeration.hasMoreElements( ); ) {
			columnName = ( String ) enumeration.nextElement( );
			if ( columnName.endsWith( "_KEY" ) ) {
				columnValue = (String) request.getAttribute( columnName );
				actColumnName = columnName.toUpperCase().substring( 0, columnName.lastIndexOf("_KEY") );
				if ( columnValue != null && columnValue.length() > 0 && (metadata.getColumnMetaData(actColumnName) != null) && metadata.getColumnMetaData(actColumnName).getType() != ColumnDataType._CDT_TIMESTAMP )
					where.put(actColumnName , columnValue.trim() );
			}
		}
        return where;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return java.util.Hashtable
	 * @roseuid 3DCC79990200
	 */
	public static Hashtable getOperator(HttpServletRequest request, EditorMetaData metadata)
	{
        Hashtable operations = new Hashtable( );
        boolean isExactSearch = true;
        if(request.getAttribute("exactSearch") != null) {
            isExactSearch = ((Boolean)request.getAttribute("exactSearch")).booleanValue();
        }

        String columnName = null;
        String columnValue = null;
        String actColumnName = null;
        ColumnMetaData columnMetaData = null;

	    for ( Enumeration enumeration = request.getParameterNames( ); enumeration.hasMoreElements( ); ) {
            columnName = ( String ) enumeration.nextElement( );
            if (columnName != null && columnName.length( ) != 0 )
            if ( columnName.endsWith( "operator_lookUp" ) ) {
	            columnValue = request.getParameter( columnName );
	            actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "operator_lookUp" ) );
	            if ( columnValue != null && columnValue.length( ) != 0 && (metadata.getColumnMetaData(actColumnName) != null))
	                operations.put(actColumnName, request.getParameter( columnName ).trim( ) );
            }
            else if (!isExactSearch && (columnName.endsWith("_lookUp") || columnName.endsWith("_look") || columnName.endsWith("_edit") || columnName.endsWith("_search")  && !columnName.endsWith( "operator_lookUp" ) )) {
                columnValue = getValueFromRequest(request,columnName);
                if(columnName.endsWith("_edit"))
                    actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "_edit" ) );
                if(columnName.endsWith("_lookUp"))
                    actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "_lookUp" ) );
                if(columnName.endsWith("_look"))
                    actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "_look" ) );
                if(columnName.endsWith("_search"))
                    actColumnName = columnName.toUpperCase( ).substring( 0, columnName.indexOf( "_search" ) );

                if ( columnValue == null || columnValue.trim().length( ) == 0 || (metadata.getColumnMetaData(actColumnName) == null) )
                    continue;
                columnMetaData = metadata.getColumnMetaData(actColumnName);
                if(columnMetaData == null || columnMetaData.getType() != ColumnDataType._CDT_STRING )
                    continue;

                operations.put ( actColumnName , Integer.toString(AVConstants._LIKE) );
            }
	    }
	    return operations;
	}

	/**
	 * @param metadata
	 * @param request
	 * @return java.util.Vector
	 * @roseuid 3DCC799902F9
	 */
	public static Vector setOrderBy(EditorMetaData metadata, HttpServletRequest request)
	{
	    Vector orderby = new Vector( );
	    String  sortName = request.getParameter("SORT_NAME" ) != null ? request.getParameter( "SORT_NAME" ) :"";

	    if (sortName.equals(""))
	    	sortName = request.getParameter("sort_Name" ) != null ? request.getParameter( "sort_Name" ) :"";

	    if (sortName.equals(""))
	    	sortName = (String) request.getAttribute( "SORT_NAME" ) != null ? (String) request.getAttribute( "SORT_NAME" ) : "";


	    String sortOrder = request.getParameter("SORT_ORDER") != null ? request.getParameter( "SORT_ORDER" ) :"";

	    if (sortOrder.equals(""))
	    	sortOrder = request.getParameter("sort_Order" ) != null ? request.getParameter( "sort_Order" ) :"ASC";

	    if (sortOrder.equals(""))
	    	sortOrder = (String) request.getAttribute( "SORT_ORDER" ) != null ? (String) request.getAttribute( "SORT_ORDER" ) : "";

	    int intSortOrder = sortOrder.equalsIgnoreCase("ASC") ? AVConstants._ASC : AVConstants._DESC;

        ColumnMetaData columnMetaDatas[] = getColumsBySortOrderSeq( metadata.getDisplayableColumns());
        ColumnMetaData columnMetaData  = null;
        for(int i=0;i<columnMetaDatas.length;i++){
            columnMetaData = columnMetaDatas[i];
            if( columnMetaData.getType() != ColumnDataType._CDT_LINK ){
                if( sortName.equals( columnMetaData.getName() ) ) {
                    orderby.add ( 0, columnMetaData.getName() + AVConstants._DELIMITER + intSortOrder );
                }
                else {
                    orderby.add ( columnMetaData.getName() + AVConstants._DELIMITER + columnMetaData.getOrdering() );
                }
            }
        }
	    return orderby;
	}

    public static ColumnMetaData[] getColumsBySortOrderSeq(Vector displayableColumns) {

		List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>();
				columns.addAll(displayableColumns);
				Collections.sort(columns, new Comparator<ColumnMetaData>() {
					public int compare(ColumnMetaData arg1, ColumnMetaData arg2) {
						if (arg1.getSortOrderSeq() > -1 && arg2.getSortOrderSeq() > -1) {
							return Integer.valueOf(arg1.getSortOrderSeq()).compareTo(Integer.valueOf(arg2.getSortOrderSeq()));
						}
						return -1;
					}
				});
		return (ColumnMetaData[]) columns.toArray(new ColumnMetaData[0]);      
    }

	/**
	 * @param editorMetaData
	 * @param request
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC799A0071
	 */
	public static EJBResultSet getDeleteEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request)
	{
        EJBResultSetMetaData ejbResultSetMetaData = new EJBResultSetMetaData( editorMetaData );
        EJBCriteria          ejbCriteria          = ejbResultSetMetaData.getNewCriteria();
        EJBResultSet         ejbResultSet         = new EJBResultSet( ejbResultSetMetaData , ejbCriteria );
		String  			 keyValue 			  = null;
		int 				 size 				  = editorMetaData.getColumnCount();
		ColumnMetaData 		 columnMetaData 	  = null;
		boolean				 keysExist			  = false;

        boolean isMulitpleDelete = request.getParameter ( editorMetaData.getName() + "_DELETE" ) != null;

        if(isMulitpleDelete){

            String [] deleteRows =  request.getParameterValues ( editorMetaData.getName() + "_DELETE" );
            String deleteQueryString = null;
            HashMap keyValuePair = null;
            String fldName = null;
            String fldValue = null;

            for(int i=0;i<deleteRows.length;i++) {

                deleteQueryString = deleteRows[i];

                keyValuePair = new HashMap();
                String namevaluepair[] = StringUtils.split(deleteQueryString,"&");

                for(int j=0;j<namevaluepair.length;j++){

                    String namevalue[] = StringUtils.split(namevaluepair[j],"=");
                    fldName = namevalue[0];
                    fldValue =(namevalue.length > 1)?namevalue[1]:null;
                    if ( fldValue != null && fldValue.length() > 0 ) {
                        keyValuePair.put(fldName,fldValue);
                    }

                }

                ejbResultSet.deleteRow();
                keysExist = false;

                for (int index = 1; index <= size; index++ ) {

                    columnMetaData = editorMetaData.getColumnMetaData( index );

                    if ( keyValuePair.containsKey( columnMetaData.getName() + "_KEY" ) ) {

                        ejbResultSet.updateString ( index, (String) keyValuePair.get(columnMetaData.getName() + "_KEY") );
                        keysExist = true;
                    }
                }

                if (keyValuePair.isEmpty() || !keysExist){
                    throw new com.addval.utils.XRuntime( "Utils.getDeleteEJBResultSet", "No keys exist for deleting a record" );
                }

            }
        }
        else {
            // This is a delete row
            ejbResultSet.deleteRow();
            for (int index = 1; index <= size; index++ ) {
                columnMetaData = editorMetaData.getColumnMetaData( index );
                keyValue = request.getParameter ( columnMetaData.getName() + "_KEY" );

                if ( keyValue != null && keyValue.length() > 0 ) {
                    ejbResultSet.updateString ( index, keyValue );
                    keysExist = true;
                }
            }
            if (size == 0 || !keysExist)
                throw new com.addval.utils.XRuntime( "Utils.getDeleteEJBResultSet", "No keys exist for deleting a record" );

        }

        return ejbResultSet;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @param fromList
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC799A017E
	 */
	public static EJBResultSet getInsertEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request, boolean fromList)
	{
        EJBCriteria ejbCriteria = getEJBCriteria ( editorMetaData, request, fromList );
        EJBResultSet ejbResultSet = new EJBResultSet ( new EJBResultSetMetaData(editorMetaData), ejbCriteria );
        // System.out.println ( "after new insertEJBResultSet()" );
        ejbResultSet.insertRow();
        for ( int index=1; index<=editorMetaData.getColumnCount(); index++ )
        {
            // System.out.println ( "index = "+editorMetaData.getColumnMetaData(index).getName() );
            String  value = request.getParameter ( editorMetaData.getColumnMetaData(index).getName() + "_edit" );
            //System.out.println ( "value = "+value );

            // Changed on 06/05/2003 - Prasad.
            // The reason this code was there is because call to updateString() will set "available" property
            // in EJBResultSet. Then the server will build the query.
            // But if the editor did not have the column at all we should not update the value in the database.
            // So the logic has been changed to say - if the column is updatable, then the column was there
            // So the client should send the value
            if (value != null) {
            	if (value.length() > 0) {

            		ejbResultSet.updateString( index, value );
            	}
            	else {
            		// This means the length is zero and the column is updatable
            		if (editorMetaData.getColumnMetaData( index ).isUpdatable())
              			ejbResultSet.updateString( index, value );
              	}
            }
            String  keyValue = request.getParameter( editorMetaData.getColumnMetaData(index).getName() + "_PARENT_KEY" );
            if ( keyValue != null && keyValue.length()>0 )
            {
                //System.out.println ( "key value = "+keyValue );
                ejbResultSet.updateString ( index, keyValue );
            }

            //if ( value!=null && value.length()>0 )
            //	ejbResultSet.updateString ( index, value );
        }

        return ejbResultSet;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @param fromList
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC799A0297
	 */
	public static EJBResultSet getNewEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request, boolean fromList)
	{

        EJBCriteria ejbCriteria = getEJBCriteria( editorMetaData, request, fromList );
        EJBResultSet ejbResultSet = new EJBResultSet( new EJBResultSetMetaData( editorMetaData ), ejbCriteria );

        //System.out.println ( "after new EJBResultSet()" );

        ejbResultSet.updateRow();

        for ( int index=1; index<=editorMetaData.getColumnCount(); index++ )
        {
            //System.out.println ( "index = "+index );
            String  value = request.getParameter ( editorMetaData.getColumnMetaData(index).getName() + "_edit" );
            // Changed on 06/05/2003 - Prasad.
            // The reason this code was there is because call to updateString() will set "available" property
            // in EJBResultSet. Then the server will build the query.
            // But if the editor did not have the column at all we should not update the value in the database.
            // So the logic has been changed to say - if the column is updatable, then the column was there
            // So the client should send the value
            if (value != null) {
            	if (value.length() > 0) {

            		ejbResultSet.updateString( index, value );
            	}
            	else {
            		// This means the length is zero and the column is updatable
            		if (editorMetaData.getColumnMetaData( index ).isUpdatable())
              			ejbResultSet.updateString( index, value );
              	}
            }

            String  keyValue = request.getParameter ( editorMetaData.getColumnMetaData(index).getName() + "_KEY" );
            if ( keyValue!=null && keyValue.length()>0 )
            {
                //System.out.println ( "key value = "+keyValue );
                ejbResultSet.updateString ( index, keyValue );
            }
            //System.out.println ( "after ejbResultSet.updateString()" );
        }
        return ejbResultSet;
	}

	/**
	 * @param
	 * @param request
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3DD3D53000ED
	 */
    private static String getValueFromRequest(HttpServletRequest request,String fieldName) {
        String fieldValue = null;
        fieldValue = request.getParameter( fieldName ).trim( );
        if((fieldValue == null || fieldValue.length() == 0) && request.getAttribute( fieldName ) != null) {
            fieldValue = ((String)request.getAttribute( fieldName )).trim( );
        }
        return fieldValue;
    }

    public static EJBCriteria getEJBCriteriaForLookup(EditorMetaData editormetadata, HttpServletRequest request)
	{
        Hashtable where = new Hashtable( );
        Hashtable operations = new Hashtable();
        boolean isExactMatchLookup = (request.getParameter( "exactMatchLookup") != null);

        String fieldName = null;
        String fieldValue = null;
        String dbFieldName = null;

        for ( Enumeration enumeration = request.getParameterNames( ); enumeration.hasMoreElements( ); ) {
            fieldName = ( String ) enumeration.nextElement( );
            if (fieldName == null && fieldName.length( ) == 0 )
                continue;

            if (fieldName.endsWith("_lookUp") || fieldName.endsWith("_look") || fieldName.endsWith("_edit") || fieldName.endsWith("_search")  && !fieldName.endsWith( "operator_lookUp" ) ) {
                fieldValue = getValueFromRequest(request,fieldName);
                if(fieldName.endsWith("_edit"))
                    dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_edit" ) );
                if(fieldName.endsWith("_lookUp"))
                    dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_lookUp" ) );
                if(fieldName.endsWith("_look"))
                    dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_look" ) );
                if(fieldName.endsWith("_search"))
                    dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_search" ) );

                if ( fieldValue == null || fieldValue.length( ) == 0 || (editormetadata.getColumnMetaData(dbFieldName) == null) )
                    continue;

                if (isExactMatchLookup) {
                    where.put ( dbFieldName, fieldValue );
                }
                else {
                    where.put ( dbFieldName, fieldValue+"%" );
                    operations.put ( dbFieldName , Integer.toString(AVConstants._LIKE) );
                }
            }
        }
        EJBCriteria ejbCriteria = ( new EJBResultSetMetaData(editormetadata) ).getNewCriteria();
        ejbCriteria.where ( where, operations );
        ejbCriteria.orderBy ( setOrderBy(editormetadata,request) );
        return ejbCriteria;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DDD983801A0
	 */
	public static EJBResultSet getDetailEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request)
	{
        EJBCriteria ejbCriteria = getEJBCriteria ( editorMetaData, request, false );
        EJBResultSet ejbResultSet = new EJBResultSet ( new EJBResultSetMetaData(editorMetaData), ejbCriteria );

		String[] detailArray = null;

        for ( int index=1; index<=editorMetaData.getColumnCount(); index++ )
        {
            //System.out.println ( "index = "+editorMetaData.getColumnMetaData(index).getName() );
            detailArray = request.getParameterValues ( editorMetaData.getColumnMetaData(index).getName() + "_array" );

			if (detailArray != null)
			   break;
		}

		if (detailArray != null)
		{
			for( int rec=0; rec<detailArray.length; rec++)
			{
		        ejbResultSet.insertRow();

		        for ( int index=1; index<=editorMetaData.getColumnCount(); index++ )
		        {
		            String  value = request.getParameter ( editorMetaData.getColumnMetaData(index).getName() + "_edit" );
		            //System.out.println ( "value = "+value );
		            if ( value!=null && value.length()>0 )
		            {
		                ejbResultSet.updateString ( index, value );
		            }

		            value = request.getParameter ( editorMetaData.getColumnMetaData(index).getName() + "_KEY" );
		            //System.out.println ( "value = "+value );
		            if ( value!=null && value.length()>0 )
		            {
		                ejbResultSet.updateString ( index, value );
		            }


					String[] valueArray = null;
		            valueArray = request.getParameterValues ( editorMetaData.getColumnMetaData(index).getName() + "_array" );

					if (valueArray!=null && rec<valueArray.length)
					{
						ejbResultSet.updateString (index, valueArray[rec]);
					}
				}

			}

		}


        return ejbResultSet;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @param parentMetaData
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DDDA98603E4
	 */
	public static EJBResultSet getDetailDeleteEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request, EditorMetaData parentMetaData)
	{
        EJBResultSetMetaData ejbResultSetMetaData = new EJBResultSetMetaData( editorMetaData );
        EJBCriteria          ejbCriteria          = ejbResultSetMetaData.getNewCriteria();
        EJBResultSet         ejbResultSet         = new EJBResultSet( ejbResultSetMetaData , ejbCriteria );
		String  			 keyValue 			  = null;
		int 				 size 				  = editorMetaData.getColumnCount();
		ColumnMetaData 		 columnMetaData 	  = null;
		ColumnMetaData 		 parentColumnMetaData = null;
		boolean				 keysExist			  = false;

        // This is a delete row
        ejbResultSet.deleteRow();

        for ( int index=1; index<=parentMetaData.getColumnCount(); index++ )
        {

			parentColumnMetaData =  parentMetaData.getColumnMetaData(index);

			if (parentColumnMetaData.isKey() || parentColumnMetaData.isEditKey())
			{

				for (int index2 = 1; index2 <= size; index2++ )
				{

					columnMetaData = editorMetaData.getColumnMetaData( index2 );

					if (columnMetaData.getName().equals(parentColumnMetaData.getName()))
					{
						keyValue = request.getParameter ( columnMetaData.getName() + "_KEY" );

						if ( keyValue != null && keyValue.length() > 0 )
						{

							ejbResultSet.updateString ( index2, keyValue );
							keysExist = true;
						}

						keyValue = request.getParameter ( columnMetaData.getName() + "_edit" );

						if ( keyValue != null && keyValue.length() > 0 )
						{

							ejbResultSet.updateString ( index2, keyValue );
							keysExist = true;
						}

					}
				}

			}
		}


		return ejbResultSet;
	}

	/**
	 * @param ejbmetadata
	 * @param request
	 * @param forList
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3E5FE714025B
	 */
	public static EJBCriteria getEJBCriteriaForEJBResultSetMetaData(EJBResultSetMetaData ejbmetadata, HttpServletRequest request, boolean forList)
	{

	  EditorMetaData editormetadata = ejbmetadata.getEditorMetaData();

	  EJBCriteria ejbCriteria = ejbmetadata.getNewCriteria();

	  if ( forList ) {
	    ejbCriteria.where ( buildLookUpWhere(request, editormetadata), getOperator(request, editormetadata) );
	    ejbCriteria.orderBy ( setOrderBy(editormetadata,request) );
	  }
	  else {
		ejbCriteria.where ( buildEditWhere(request, editormetadata), getOperator(request, editormetadata) );
	  }

	  //System.out.println("After GetEJBCriteriaForEJBResultSet ");
	  return ejbCriteria;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return boolean
	 * @roseuid 3EF5BFFC032E
	 */
	public static boolean isLookupFilterPresent(HttpServletRequest request, EditorMetaData metadata)
	{
		boolean filterPresent = false;

        String fieldName  = null;
        String fieldValue = null;
        String dbFieldName = null;
	    fieldName  = request.getParameter ( "displayFieldName"  );

        if (fieldName != null && (fieldName.endsWith("_lookUp") || fieldName.endsWith("_look") || fieldName.endsWith("_edit") || fieldName.endsWith("_search")  && !fieldName.endsWith( "operator_lookUp" ) ) ) {

            fieldValue = request.getParameter ( "displayFieldValue" );
            filterPresent = (fieldValue != null && fieldValue.length() > 0 );

        }
        else {

            for ( Enumeration enumeration = request.getParameterNames( ); enumeration.hasMoreElements( ); ) {
                fieldName = ( String ) enumeration.nextElement( );

                if (fieldName == null && fieldName.length( ) == 0 )
                    continue;

                if (fieldName.endsWith("_lookUp") || fieldName.endsWith("_look") || fieldName.endsWith("_edit") || fieldName.endsWith("_search")  && !fieldName.endsWith( "operator_lookUp" ) ) {
                    fieldValue = getValueFromRequest(request,fieldName);
                    if(fieldName.endsWith("_edit"))
                        dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_edit" ) );
                    if(fieldName.endsWith("_lookUp"))
                        dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_lookUp" ) );
                    if(fieldName.endsWith("_look"))
                        dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_look" ) );
                    if(fieldName.endsWith("_search"))
                        dbFieldName = fieldName.toUpperCase( ).substring( 0, fieldName.indexOf( "_search" ) );
                    if ( fieldValue == null || fieldValue.length( ) == 0 || (metadata.getColumnMetaData(dbFieldName) == null) )
                        continue;

                    filterPresent = true;
                    break;
                }
            }
        }
        return filterPresent;
	}

}

