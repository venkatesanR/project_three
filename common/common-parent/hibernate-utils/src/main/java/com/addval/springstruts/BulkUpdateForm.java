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

import com.addval.metadata.BulkUpdate;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;

import com.addval.utils.StrUtl;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.LabelValueBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;


public class BulkUpdateForm extends BaseForm
{
    private static final long serialVersionUID = 3216976055567167007L;
    private String subsystem = null;
    private String kindOfAction = null;
    private String confirmation = null;
    private String newUpdateName = null;
    private String updateName = null;
    private String updateDesc = null;
    private ArrayList updateNames = null;
    private EditorMetaData metadata = null;
    private String editorName = null;
    private String editorType = "DEFAULT";
    private String directoryName = null;
    private Vector<UpdateColumn> updateColumns = null;
    private String filters = null;
    private BulkUpdate bulkUpdate = null;
    private String callBackLink = null;

    public BulkUpdateForm()
    {
        setUpdateColumns(new Vector(5));
    }

    public BulkUpdate getBulkUpdate()
    {
        return bulkUpdate;
    }

    public void setBulkUpdate(BulkUpdate bulkUpdate)
    {
        this.bulkUpdate = bulkUpdate;
    }

    public String getFilters()
    {
        return (filters != null) ? filters.trim() : filters;
    }

    public void setFilters(String filters)
    {
        this.filters = filters;
    }

    public String getKindOfAction()
    {
        return kindOfAction;
    }

    public void setKindOfAction(String kindOfAction)
    {
        this.kindOfAction = kindOfAction;
    }

    public String getConfirmation()
    {
        return confirmation;
    }

    public void setConfirmation(String confirmation)
    {
        this.confirmation = confirmation;
    }

    public EditorMetaData getMetadata()
    {
        return metadata;
    }

    public void setMetadata(EditorMetaData metadata)
    {
        this.metadata = metadata;
    }

    public String getSubsystem()
    {
        return subsystem;
    }

    public void setSubsystem(String subsystem)
    {
        this.subsystem = subsystem;
    }

    public String getNewUpdateName()
    {
        return newUpdateName;
    }

    public void setNewUpdateName(String newUpdateName)
    {
        this.newUpdateName = newUpdateName;
    }

    public String getUpdateName()
    {
        return updateName;
    }

    public void setUpdateName(String updateName)
    {
        this.updateName = updateName;
    }

    public String getUpdateDesc()
    {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc)
    {
        this.updateDesc = updateDesc;
    }

    public ArrayList getUpdateNames()
    {
        return updateNames;
    }

    public void setUpdateNames(ArrayList updateNames)
    {
        this.updateNames = updateNames;
    }

    public String getEditorName()
    {
        return editorName;
    }

    public void setEditorName(String editorName)
    {
        this.editorName = editorName;
    }

    public String getEditorType()
    {
        return editorType;
    }

    public void setEditorType(String editorType)
    {
        this.editorType = editorType;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public String getCallBackLink()
    {
        return this.callBackLink;
    }

    public void setCallBackLink(String callBackLink)
    {
        this.callBackLink = callBackLink;
    }

    public String getDateFormat()
    {
        String dateFormat = "dd/MM/yy";
        // Date Format read from the EditorMetadata Columns which has CDT_DATE
        // type.
        Vector<ColumnMetaData> searchableColumns = getMetadata().getSearchableColumns();
        if(searchableColumns != null)
        {
            for(ColumnMetaData columnMetaData : searchableColumns)
            {
                if(columnMetaData.getType() == ColumnDataType._CDT_DATE)
                {
                    if(!StrUtl.isEmptyTrimmed(columnMetaData.getFormat()))
                    {
                        dateFormat = columnMetaData.getFormat();
                        break;
                    }
                }
            }
        }

        return dateFormat;
    }

    public ArrayList getUpdateColumnNames()
    {
        ArrayList updateColumnNames = new ArrayList();
        updateColumnNames.add(new LabelValueBean("", ""));
        Vector<ColumnMetaData> updateableColumns = getUpdateableColumns();
        for(ColumnMetaData columnMetaData : updateableColumns)
            updateColumnNames.add(new LabelValueBean(columnMetaData.getCaption(), columnMetaData.getName()));

        return updateColumnNames;
    }

    public ArrayList getUpdateColumnOperators()
    {
        ArrayList updateColumnOperators = new ArrayList();
        updateColumnOperators.add(new LabelValueBean("", ""));
        updateColumnOperators.add(new LabelValueBean("*", "*"));
        updateColumnOperators.add(new LabelValueBean("+", "+"));
        updateColumnOperators.add(new LabelValueBean("-", "-"));
        updateColumnOperators.add(new LabelValueBean("/", "/"));
        updateColumnOperators.add(new LabelValueBean("=", "="));
        return updateColumnOperators;
    }

    public Vector<UpdateColumn> getUpdateColumnList()
    {
        return updateColumns;
    }

    public Vector<UpdateColumn> getUpdateColumns()
    {
        return updateColumns;
    }

    public void setUpdateColumns(Vector<UpdateColumn> updateColumns)
    {
        this.updateColumns = updateColumns;
    }

    public void setUpdateColumns(int i, UpdateColumn updateColumn)
    {
        if(i >= updateColumns.size())
        {
            int oldsize = updateColumns.size();
            for(int idx = oldsize; idx <= i; ++idx)
                updateColumns.add(new UpdateColumn());

            updateColumns.setElementAt(updateColumn, i);
        }
        else
            updateColumns.setElementAt(updateColumn, i);
    }

    public UpdateColumn getUpdateColumns(int i)
    {
        if(i < updateColumns.size())
        {
            if(updateColumns.elementAt(i) == null)
                setUpdateColumns(i, new UpdateColumn());

            return (UpdateColumn) updateColumns.elementAt(i);
        }
        else
        {
            setUpdateColumns(i, new UpdateColumn());
            return (UpdateColumn) updateColumns.elementAt(i);
        }
    }

    public Vector getUpdateableColumns()
    {
        if(metadata == null)
            return null;

        Vector columnsMetaData = new Vector();
        ColumnMetaData columnMetaData = null;
        int columnDataType = 0;
        for(Iterator iterator = metadata.getColumnsMetaData().iterator();
                iterator.hasNext();)
        {
            columnMetaData = (ColumnMetaData) iterator.next();
            columnDataType = columnMetaData.getType();
            if((columnDataType != ColumnDataType._CDT_LINK) && (columnDataType != ColumnDataType._CDT_TIMESTAMP) && (columnDataType != ColumnDataType._CDT_VERSION) && (columnDataType != ColumnDataType._CDT_USER) && columnMetaData.isUpdatable() && !columnMetaData.isEditKey())
                columnsMetaData.add(columnMetaData);
        }

        return (columnsMetaData.size() > 0) ? columnsMetaData : null;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
    {
        ActionErrors errors = new ActionErrors();
        if(!StrUtl.isEmptyTrimmed(getKindOfAction()) && !"cancel".equalsIgnoreCase(getKindOfAction()))
        {
            if(StrUtl.isEmptyTrimmed(getDirectoryName()))
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.directoryname.required"));

            if("insert".equalsIgnoreCase(getKindOfAction()) || "update".equalsIgnoreCase(getKindOfAction()))
            {
                boolean errorAdded = false;
                for(UpdateColumn updateColumn : getUpdateColumns())
                {
                    if(!StrUtl.isEmptyTrimmed(updateColumn.getColumnName()) && StrUtl.isEmptyTrimmed(updateColumn.getColumnValue()))
                        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatevalue.required"));

                    if(StrUtl.isEmptyTrimmed(updateColumn.getColumnName()) && !StrUtl.isEmptyTrimmed(updateColumn.getColumnValue()))
                        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatevalue.required"));

                    List numericOperators = new ArrayList();
                    numericOperators.add("*");
                    numericOperators.add("+");
                    numericOperators.add("-");
                    numericOperators.add("/");
                    if(!StrUtl.isEmptyTrimmed(updateColumn.getColumnValue()) && numericOperators.contains(updateColumn.getColumnOperator()))
                    {
                        try
                        {
                            RE _matcher = new RE("^(\\d*(\\.\\d+)|(\\d*))$");
                            if((_matcher == null) || !_matcher.match(updateColumn.getColumnValue()))
                            {
                                if(errorAdded == false)
                                {
                                    errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatevalue.invalid"));
                                    errorAdded = true;
                                }
                            }
                        }
                        catch(RESyntaxException ex)
                        {
                            if(errorAdded == false)
                            {
                                errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatevalue.invalid"));
                                errorAdded = true;
                            }
                        }
                    }
                }

                if((getUpdateColumns().size() == 0) && StrUtl.isEmptyTrimmed(getFilters()))
                    errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatefilter.required"));
                else if(getUpdateColumns().size() == 0)
                    errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatevalue.required"));
                else if(StrUtl.isEmptyTrimmed(getFilters()))
                    errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.filter.required"));
                else if(getKindOfAction().equals("insert") && StrUtl.isEmptyTrimmed(getNewUpdateName()))
                    errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.updatename.required"));
            }
        }

        return errors;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request)
    {
        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
        BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());
        setSubsystem(formConfig.getSubsystem());
        if(StrUtl.isEmptyTrimmed(getSubsystem()))
        {
            BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();
            setSubsystem(baseConfig.getSubsystem());
        }

        String editorName = formConfig.getEditorName();
        if(StrUtl.isEmptyTrimmed(editorName))
            editorName = (String) request.getAttribute("EDITOR_NAME");

        if(StrUtl.isEmptyTrimmed(editorName))
            editorName = (String) request.getParameter("EDITOR_NAME");

        if(StrUtl.isEmptyTrimmed(editorName))
            editorName = request.getParameter("editorName");

        String editorType = formConfig.getEditorType();
        if(StrUtl.isEmptyTrimmed(editorType))
            editorType = (String) request.getAttribute("EDITOR_TYPE");

        if(StrUtl.isEmptyTrimmed(editorType))
            editorType = (String) request.getParameter("EDITOR_TYPE");

        if(StrUtl.isEmptyTrimmed(editorType))
            editorType = request.getParameter("editorType");

        setEditorName(editorName);
        setEditorType(editorType);
        if((metadata == null) && !StrUtl.isEmptyTrimmed(getEditorName()))
        {
            String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";
            metadata = lookupMetadata(getEditorName(), getEditorType(), userName, request.getSession());
            initializeFromMetadata(request, mapping, metadata);
        }
    }

    public void initializeFromMetadata(HttpServletRequest request, ActionMapping mapping, EditorMetaData metadata)
    {
        org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);
        BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());

        // if editorName is not configured in formConfig
        String editorName = formConfig.getEditorName();
        if(StrUtl.isEmpty(editorName))
        {
        }
    }
}
