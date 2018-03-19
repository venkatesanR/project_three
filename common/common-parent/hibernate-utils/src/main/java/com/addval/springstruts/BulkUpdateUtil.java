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

package com.addval.springstruts;

import com.addval.ejbutils.server.BulkUpdateMgr;

import com.addval.metadata.BulkUpdate;
import com.addval.metadata.EditorMetaData;

import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.util.LabelValueBean;


import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;


public class BulkUpdateUtil
{
    public void initializeForm(HttpServletRequest request, BulkUpdateForm bulkUpdateForm, ArrayList<BulkUpdate> bulkUpdates)
    {
        ArrayList<LabelValueBean> updateNames = new ArrayList<LabelValueBean>();
        for(BulkUpdate bk : bulkUpdates)
            updateNames.add(new LabelValueBean(bk.getUpdateName(), bk.getUpdateName()));

        bulkUpdateForm.setUpdateNames(updateNames);
        String kindOfAction = (bulkUpdateForm.getKindOfAction() != null) ? bulkUpdateForm.getKindOfAction() : "";
        if(kindOfAction.equals("") || kindOfAction.equals("select"))
        {
            BulkUpdate bulkUpdate = bulkUpdateForm.getBulkUpdate();
            String desc = null;
            String name = null;
            String filter = null;
            Vector<UpdateColumn> updateColumns = null; 
            if(bulkUpdate != null)
            {
            	updateColumns = populateUpdateColumns(bulkUpdate.getUpdateValue());
                filter = bulkUpdate.getFilter();
                desc = bulkUpdate.getUpdateDesc();
                name = bulkUpdate.getUpdateName();
                
            }
            else {
            	updateColumns = new Vector<UpdateColumn>(5);
            }
            bulkUpdateForm.setUpdateColumns( updateColumns );
            bulkUpdateForm.setFilters( filter );
            bulkUpdateForm.setUpdateDesc( desc );
            bulkUpdateForm.setUpdateName( name );
        }
    }

    public Vector<UpdateColumn> populateUpdateColumns(String updateValue)
    {
        Vector<UpdateColumn> updateColumns = new Vector<UpdateColumn>(5);
        if(updateValue == null)
            return updateColumns;
        StringTokenizer updateOTokens = null;
        String updateSet = null;
        String columnName = null;
        String columnOperator = null;
        String columnValue = null;
        UpdateColumn updateColumn = null;

        updateOTokens = new StringTokenizer(updateValue, ",", false);
        while(updateOTokens.hasMoreTokens())
        {
            updateSet = updateOTokens.nextToken();
            String[] values = updateSet.split("\\|");
            int i = 0;
            columnName = values[i++];
            if(values.length == 4)
                i++;

            columnOperator = values[i++];
            columnValue = values[i++];
            updateColumn = new UpdateColumn();
            updateColumn.setColumnName(columnName);
            updateColumn.setColumnOperator(columnOperator);
            updateColumn.setColumnValue(columnValue);
            updateColumns.add(updateColumn);
        }

        return updateColumns;
    }

    public BulkUpdate getBulkUpdate(BulkUpdateForm bulkUpdateForm, HttpServletRequest request) throws Exception
    {
        String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
        BulkUpdate bulkUpdate = new BulkUpdate();
        bulkUpdate.setEditorName(bulkUpdateForm.getEditorName());
        bulkUpdate.setDirectoryName(bulkUpdateForm.getDirectoryName());
        bulkUpdate.setUpdateName(getUpdateName(bulkUpdateForm));
        bulkUpdate.setUpdateDesc(getUpdateDesc(bulkUpdateForm));
        bulkUpdate.setFilter(bulkUpdateForm.getFilters());
        bulkUpdate.setUpdateValue(getUpdateValue(bulkUpdateForm));
        bulkUpdate.setLastUpdatedBy(userName);
        return bulkUpdate;
    }

    public ActionErrors validateBulkUpdate(BulkUpdateMgr bulkUpdateMgr, EditorMetaData metadata, BulkUpdate bulkUpdate, String kindOfAction) throws XRuntime
    {
        ActionErrors errors = new ActionErrors();
        try
        {
            bulkUpdateMgr.validate(metadata, bulkUpdate);
        }
        catch(Exception ex)
        {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Update Name. Please check the Update."));
        }

        return errors;
    }

    private String getUpdateName(BulkUpdateForm bulkUpdateForm)
    {
        String reportName = null;
        if(!StrUtl.isEmptyTrimmed(bulkUpdateForm.getUpdateName()))
            reportName = bulkUpdateForm.getUpdateName();
        else if(!StrUtl.isEmptyTrimmed(bulkUpdateForm.getNewUpdateName()))
            reportName = bulkUpdateForm.getNewUpdateName().trim();

        return reportName;
    }

    private String getUpdateDesc(BulkUpdateForm bulkUpdateForm)
    {
        String reportDesc = null;
        if(!StrUtl.isEmptyTrimmed(bulkUpdateForm.getUpdateDesc()))
            reportDesc = bulkUpdateForm.getUpdateDesc();

        return reportDesc;
    }

    public String getUpdateValue(BulkUpdateForm bulkUpdateForm)
    {
        StringBuffer updateValue = new StringBuffer();
        if(bulkUpdateForm.getUpdateColumns() == null)
            return null;

        for(UpdateColumn updateColumn : bulkUpdateForm.getUpdateColumns())
        {
            if(!StrUtl.isEmptyTrimmed(updateColumn.getColumnName()) && !StrUtl.isEmptyTrimmed(updateColumn.getColumnValue()))
                updateValue.append(",").append(updateColumn.getColumnName()).append("|").append(getSuffix()).append("|").append(updateColumn.getColumnOperator()).append("|").append(updateColumn.getColumnValue());
        }

        return (updateValue.toString().length() > 0) ? updateValue.toString().substring(1) : null;
    }

    protected String getSuffix()
    {
        return "_OVD";
    }
}
