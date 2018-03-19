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

package com.addval.ejbutils.server;

import com.addval.metadata.BulkUpdate;
import com.addval.metadata.EditorFilter;
import com.addval.metadata.EditorMetaData;

import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

import org.apache.commons.lang.StringUtils;

import java.util.StringTokenizer;
import java.util.Vector;


public class BulkUpdateSqlBuilder
{
    private EditorMetaData metadata = null;
    private BulkUpdate bulkUpdate = null;

    public BulkUpdateSqlBuilder(EditorMetaData metadata, BulkUpdate bulkUpdate)
    {
        setMetadata(metadata);
        setBulkUpdate(bulkUpdate);
    }

    public EditorMetaData getMetadata()
    {
        return metadata;
    }

    public void setMetadata(EditorMetaData metadata)
    {
        this.metadata = metadata;
    }

    public BulkUpdate getBulkUpdate()
    {
        return bulkUpdate;
    }

    public void setBulkUpdate(BulkUpdate bulkUpdate)
    {
        this.bulkUpdate = bulkUpdate;
    }

    public String getUpdateSql() throws XRuntime
    {
        StringBuffer sqlBuf = new StringBuffer();
        try
        {
            String source = null;
            StringBuffer updateValue = new StringBuffer();
            StringBuffer updateWhere = new StringBuffer();
            source = getMetadata().getSource();
            if(source == null)
                throw new Exception("Editor Source name should not be Null. Please Contact System Administrator");

            if(getBulkUpdate().getUpdateValue() != null)
            {
                String firstOperators = "";
                String secondOperators = "";
                String thirdOperators = "";
                String finalOperators = "";
                StringTokenizer updateOTokens = new StringTokenizer(getBulkUpdate().getUpdateValue(), ",", false);
                while(updateOTokens.hasMoreTokens())
                {
                    String updateSet = updateOTokens.nextToken();
                    String values[] = updateSet.split( "\\|" );
                    String columnOperator = null;
                    if (values.length == 3)
                    	columnOperator = values[1];
                    else
                    	columnOperator = values[2];
                    if(columnOperator == null)
                    	continue;
                    // Order the columnOperator to be applied as follows
                    // Multiplication (*) or Division (/) first
                    // Addition (+) or Subtraction (-) second
                    // Equals (=) third
                    // other operators last
                    if(columnOperator.equals("*") || columnOperator.equals("/"))
                        firstOperators  += "," + updateSet;
                    else if(columnOperator.equals("+") || columnOperator.equals("-"))
                        secondOperators += "," + updateSet;
                    else if(columnOperator.equals("="))
                        thirdOperators  += "," + updateSet;
                    else 
                        finalOperators  += "," + updateSet;
                }

                String updateString = firstOperators + secondOperators + thirdOperators + finalOperators;
                updateOTokens = new StringTokenizer(updateString.substring(1), ",", false);
                while(updateOTokens.hasMoreTokens())
                {
                    String updateSet = updateOTokens.nextToken();
                    String values[] = updateSet.split( "\\|" );
                    int i=0;
                    String columnName = values[i++];
                    // suffix will determine whether the columnName is to b used or the columnName + suffix (usually _OVD)
                    String columnSuffix = "";
                    if (values.length == 4)
                    	columnSuffix = values[i++];
                    String columnOperator = values[i++];
                    if(columnOperator == null)
                    	continue;
                    String columnValue = values[i++];
                    updateValue.append(", ").append(columnName).append( columnSuffix ).append(" = ");
                    if(columnOperator.trim().equals("="))
                        updateValue.append("'").append(columnValue).append("'");
                    else
                        updateValue.append(" NVL( ").append(columnName).append( columnSuffix ).append(",").append(columnName).append(") ").append(columnOperator).append(columnValue);
                }
            }
            if(updateValue.length() == 0)
                throw new Exception("Update Values should not be Null.");

            if(!StrUtl.isEmptyTrimmed(getBulkUpdate().getFilter()))
            {
                updateWhere.append(getBulkUpdate().getFilter());
                updateWhere = replaceEditorFilters(metadata.getEditorFilters(), updateWhere);
            }

            if(updateWhere.toString().length() == 0)
                throw new Exception("Update Where Clause should not be Null.");

            sqlBuf.append("UPDATE ").append(source).append(" SET ").append(updateValue.substring(1)).append(" WHERE ").append(updateWhere);
        }
        catch(Exception ex)
        {
            throw new XRuntime(getClass().getName(), ex.getMessage());
        }

        return sqlBuf.toString();
    }

    private StringBuffer replaceEditorFilters(Vector<EditorFilter> editorFilters, StringBuffer whereClause)
    {
        if((whereClause.length() == 0) || (editorFilters.size() == 0))
            return whereClause;

        String whereClauseStr = whereClause.toString();
        String filterName = null;
        String filterDesc = null;
        String filterSql = null;
        String filterValue = null;
        String replaceFrom = null;
        String replaceTo = null;
        int startPos = -1;
        int endPos = -1;
        for(EditorFilter editorFilter : editorFilters)
        {
            filterName = editorFilter.getFilterName().toUpperCase();
            startPos = whereClauseStr.toUpperCase().indexOf(filterName);
            while(startPos != -1)
            {
                filterValue = StringUtils.substringBetween(whereClauseStr.substring(startPos), "[", "]");
                if(filterValue != null)
                {
                    filterSql = editorFilter.getFilterSql();
                    filterDesc = StrUtl.isEmptyTrimmed(editorFilter.getFilterDesc()) ? "" : editorFilter.getFilterDesc();
                    int filterDescSize = StringUtils.split(filterDesc, ",").length;
                    int filterValueSize = StringUtils.split(filterValue, ",").length;
                    int filterSqlInputSize = StringUtils.countMatches(filterSql, "?");
                    if((filterDescSize != filterValueSize) || (filterDescSize != filterSqlInputSize))
                        throw new XRuntime(getClass().getName(), "Pre-Defined Filter parameter count doesn't match for " + filterName + ". Expected " + filterSqlInputSize + " parameter(s)");

                    String[] filterValues = StringUtils.split(filterValue, ",");
                    for(int i = 0; i < filterValues.length; i++)
                        filterSql = StringUtils.replaceOnce(filterSql, "?", filterValues[i]);

                    replaceTo = filterSql;
                    endPos = whereClauseStr.substring(startPos).indexOf("]");
                    replaceFrom = StringUtils.substring(whereClauseStr, startPos, startPos + endPos + 1);
                    whereClauseStr = StringUtils.replace(whereClauseStr, replaceFrom, replaceTo);
                }

                startPos = whereClauseStr.toUpperCase().indexOf(filterName);
            }
        }

        return new StringBuffer(whereClauseStr);
    }
}
