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
package com.addval.workqueue.impl;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.ejbutils.server.EJBSTableManager;

import com.addval.environment.Environment;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;

import com.addval.utils.AVConstants;
import com.addval.utils.CnfgFileMgr;
import com.addval.utils.DateUtl;
import com.addval.utils.WorkQueueUtils;

import com.addval.workqueue.api.WQSMetaData;
import com.addval.workqueue.api.WQServer;

import com.addval.wqutils.metadata.WQMetaData;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;


public class BaseNotificationServerUtility
{
    private EJBSEditorMetaData editorMetaDataServer = null;
    private EJBSTableManager tableManager = null;
    private WQSMetaData wqSMetaData = null;
    private WQServer wqServer = null;
    private Environment environment = null;
    private WorkQueueUtils workQueueUtils = null;
    private CnfgFileMgr cnfgFileMgr = null;

    public WQSMetaData getWqSMetaData()
    {
        return wqSMetaData;
    }

    public void setWqSMetaData(WQSMetaData wqSMetaData)
    {
        this.wqSMetaData = wqSMetaData;
    }

    public WQServer getWqServer()
    {
        return wqServer;
    }

    public void setWqServer(WQServer wqServer)
    {
        this.wqServer = wqServer;
    }

    public EJBSEditorMetaData getEditorMetaDataServer()
    {
        return editorMetaDataServer;
    }

    public void setEditorMetaDataServer(EJBSEditorMetaData editorMetaDataServer)
    {
        this.editorMetaDataServer = editorMetaDataServer;
    }

    public EJBSTableManager getTableManager()
    {
        return tableManager;
    }

    public void setTableManager(EJBSTableManager tableManager)
    {
        this.tableManager = tableManager;
    }

    public Environment getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }

    public WorkQueueUtils getWorkQueueUtils()
    {
        return workQueueUtils;
    }

    public void setWorkQueueUtils(WorkQueueUtils workQueueUtils)
    {
        this.workQueueUtils = workQueueUtils;
    }

    public String getDateFormat()
    {
        return getCnfgFileMgr().getPropertyValue("dateFormat", null);
    }

    public String getDateTimeFormat()
    {
        return getCnfgFileMgr().getPropertyValue("dateTimeFormat", null);
    }

    public String getDateTimeStr(Date date)
    {
        if(date == null)
            return null;

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(getDateTimeFormat());
        dateTimeFormatter.setTimeZone(AVConstants._GMT_TIMEZONE);
        return dateTimeFormatter.format(date);
    }

    public String getCurrentDateTimeStr()
    {
        return getDateTimeStr(new Date(DateUtl.getTimestamp().getTime()));
    }

    protected EJBResultSet getQueueDeleteEJBResultSet(EJBResultSet ejbRS, Notification notification, WQMetaData wqMetaData)
    {
        String keyValue = null;
        EJBResultSetMetaData ejbResultSetMetaData = ejbRS.getEJBResultSetMetaData();
        EJBCriteria ejbCriteria = ejbResultSetMetaData.getNewCriteria();
        EJBResultSet ejbResultSet = new EJBResultSet(ejbResultSetMetaData, ejbCriteria);
        ejbResultSet.deleteRow();
        for(Iterator<ColumnMetaData> iterator = wqMetaData.getEditorMetaData().getKeyColumns().iterator(); iterator.hasNext();)
        {
            String name = iterator.next().getName();
            keyValue = ejbRS.getString( name );
            if(keyValue != null)
                ejbResultSet.updateString( name, keyValue);
        }

        return ejbResultSet;
    }

    protected EJBResultSet getUpdateEJBResultSet(EJBResultSet ejbRS, EditorMetaData editorMetaData)
    {
        String keyValue = null;
        EJBResultSetMetaData ejbResultSetMetaData = ejbRS.getEJBResultSetMetaData();
        EJBCriteria ejbCriteria = ejbResultSetMetaData.getNewCriteria();
        EJBResultSet ejbResultSet = new EJBResultSet(ejbResultSetMetaData, ejbCriteria);
        ejbResultSet.updateRow();
        ejbRS.beforeFirst();
        if(!ejbRS.next())
        	return ejbResultSet;
        for(Iterator<ColumnMetaData> iterator = editorMetaData.getKeyColumns().iterator(); iterator.hasNext();)
        {
        	String name = iterator.next().getName();
            keyValue = ejbRS.getString( name );
            if(keyValue != null)
                ejbResultSet.updateString( name, keyValue);
        }
        return ejbResultSet;
    }

    public CnfgFileMgr getCnfgFileMgr()
    {
        return cnfgFileMgr;
    }

    public void setCnfgFileMgr(CnfgFileMgr cnfgFileMgr)
    {
        this.cnfgFileMgr = cnfgFileMgr;
    }
}
