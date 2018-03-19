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

package com.addval.wqutils.metadata;

import com.addval.ejbutils.dbutils.EJBResultSetMetaData;

import com.addval.metadata.EditorMetaData;

import com.addval.utils.WorkQueueUtils;

import com.addval.wqutils.utils.WQConstants;

import java.io.Serializable;

import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;


/**
 * @author AddVal Technology Inc.
 */
public class WQMetaData extends EJBResultSetMetaData implements Serializable
{
    private static final long serialVersionUID = 6640871611839166593L;
    private String _queueName = null;
    private int _queueNo = 0;
    private String _queueDesc = null;
    private String _queueGroupName = null;
    private String _queueCondition = null;
    private long _queueTimeout = 30 * 60;
    private int _messageInProcessCount = 0;
    private int _messageUnProcessedCount = 0;
    private long _autoRefreshTime = 30 * 60;
    private String _defaultPromptDefinition = null;
    private String _processAction = null;
    private String _deleteAction = null;
    private String _editorSourceName = null;
    private WorkQueueUtils workQueueUtils = null;

    // threshold time in minutes below which a message is assumed to be a new message
    // this message can be configured per queue. If this is set to -1, then the new message count will never be set
    // this value should be configured in conjuction with the time setting in app parameter enter WQ_NEW_MESSAGE_MONITOR_INTERVAL
    private long _queueNewMessageTime = -1;

    // number of new messages in the queue that have a dwell time less than a certain threshold for the queue
    private int _newMessageUnProcessedCount = 0;

    // number of messages in the queue that were already processed
    private int _messageProcessedCount = 0;

   // public WQMetaData(String queueName, int queueNo, String queueDesc, String defaultPromptDefinition, EditorMetaData editorMetaData, String queueGroupName, String queueCondition, long queueTimeout, long autoRefreshTime)
    public WQMetaData(String queueName, String queueDesc, String defaultPromptDefinition, EditorMetaData editorMetaData, String queueGroupName, String queueCondition, long queueTimeout, long autoRefreshTime)
    {
        super(editorMetaData);
        _queueName = queueName;
        _queueDesc = queueDesc;
        _queueGroupName = queueGroupName;
        _queueCondition = queueCondition;
        _queueTimeout = queueTimeout;
        _autoRefreshTime = autoRefreshTime;
        _defaultPromptDefinition = defaultPromptDefinition;
    }

    public WorkQueueUtils getWorkQueueUtils()
    {
        if(this.workQueueUtils == null)
            this.workQueueUtils = new WorkQueueUtils();

        return this.workQueueUtils;
    }

    public void setWorkQueueUtils(WorkQueueUtils workQueueUtils)
    {
        this.workQueueUtils = workQueueUtils;
    }

    public String getWhereClause(Hashtable wqFilter)
    {
        if((_queueCondition != null) && (wqFilter != null))
        {
            StringBuffer queueCondition = new StringBuffer(_queueCondition);
            String columnName = null;
            int startPos = -1;
            for(Iterator iterator = wqFilter.keySet().iterator();
                    iterator.hasNext();)
            {
                Object columnKey = iterator.next();
                if(wqFilter.get( columnKey ) instanceof String){
	                String columnVal = (String) wqFilter.get( columnKey );
	                columnName = "<%" + columnKey + "%>".toUpperCase();
	                startPos = queueCondition.toString().toUpperCase().indexOf(columnName);
	                while(startPos != -1)
	                {
	                    queueCondition = queueCondition.replace(startPos, startPos + columnName.length(), columnVal);
	                    startPos = queueCondition.toString().toUpperCase().indexOf(columnName);
	                }
                }
            }

            return queueCondition.toString();
        }

        return null;
    }

    public String getQueueWhereClause(HttpServletRequest request)
    {
        // 1) Add MESSAGE_STATUS as UNPROCESSED in WhereClause
        // 2) Add LAST_UPDATED_BY as <Current User> in WhereClause
        // 3) Apply Queue Condition
        String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
        StringBuffer queueCondition = new StringBuffer();
        queueCondition.append("( ").append(WQConstants._MESSAGE_STATUS).append(" = '").append(WQConstants._STATUS_UNPROCESSED).append("'");
        queueCondition.append(" OR (").append(WQConstants._MESSAGE_STATUS).append(" = '").append(WQConstants._STATUS_INPROCESS).append("' AND ").append("LAST_UPDATED_BY").append(" = '").append(userName).append("' ) )");
        Hashtable wqFilter = WorkQueueUtils.getWQFilters(request);
        if(wqFilter != null)
        {
            String whereClause = getWhereClause(wqFilter);
            if (whereClause != null && whereClause.length() > 0)
                queueCondition.append(" AND ").append(whereClause);
        }

        return queueCondition.toString();
    }

    public String getNewQueueMessageCountWhereClause(Hashtable wqFilter)
    {
        // 1) Add MESSAGE_STATUS as UNPROCESSED in WhereClause
        // 2) Add Dwell Time Threshold
        // 3) Apply Queue Condition
        StringBuffer queueCondition = new StringBuffer();
        queueCondition.append("( ").append(WQConstants._MESSAGE_STATUS).append(" = '").append(WQConstants._STATUS_UNPROCESSED).append("'");
        if(_queueNewMessageTime > 0)
            queueCondition.append(" AND ").append(WQConstants._WQ_DWELL_TIME_COLUMN).append(" <= ").append(_queueNewMessageTime / 60);

        queueCondition.append(" )");
        if(wqFilter != null)
        {
            String whereClause = getWhereClause(wqFilter);
            if((whereClause != null) && (whereClause.length() > 0))
                queueCondition.append(" AND ").append(whereClause);
        }

        return queueCondition.toString();
    }

    public int getMessageInProcessCount()
    {
        return _messageInProcessCount;
    }

    public void setMessageInProcessCount(int messageInProcessCount)
    {
        _messageInProcessCount = messageInProcessCount;
    }

    public int getMessageUnProcessedCount()
    {
        return _messageUnProcessedCount;
    }

    public void setMessageUnProcessedCount(int messageUnProcessedCount)
    {
        _messageUnProcessedCount = messageUnProcessedCount;
    }

    @Deprecated
    public int getQueueNo()
    {
        return _queueNo;
    }

    @Deprecated
    public void setQueueNo(int queueNo)
    {
        this._queueNo = queueNo;
    }

    public String getQueueName()
    {
        return _queueName;
    }

    public void setQueueName(String queueName)
    {
        _queueName = queueName;
    }

    public String getQueueDesc()
    {
        return _queueDesc;
    }

    public void setQueueDesc(String queueDesc)
    {
        _queueDesc = queueDesc;
    }

    public String getQueueGroupName()
    {
        return _queueGroupName;
    }

    public void setQueueGroupName(String queueGroupName)
    {
        _queueGroupName = queueGroupName;
    }

    public String getQueueCondition()
    {
        return _queueCondition;
    }

    public void setQueueCondition(String queueCondition)
    {
        _queueCondition = queueCondition;
    }

    public long getQueueTimeout()
    {
        return _queueTimeout;
    }

    public void setQueueTimeout(long queueTimeout)
    {
        _queueTimeout = queueTimeout;
    }

    public long getAutoRefreshTime()
    {
        return _autoRefreshTime;
    }

    public void setAutoRefreshTime(long autoRefreshTime)
    {
        this._autoRefreshTime = autoRefreshTime;
    }

    public long getQueueNewMessageTime()
    {
        return _queueNewMessageTime;
    }

    public void setQueueNewMessageTime(long queueNewMessageTime)
    {
        _queueNewMessageTime = queueNewMessageTime;
    }

    public int getNewMessageUnProcessedCount()
    {
        return _newMessageUnProcessedCount;
    }

    public void setNewMessageUnProcessedCount(int newMessageUnProcessedCount)
    {
        _newMessageUnProcessedCount = newMessageUnProcessedCount;
    }

    public int getMessageProcessedCount()
    {
        return _messageProcessedCount;
    }

    public void setMessageProcessedCount(int messageProcessedCount)
    {
        _messageProcessedCount = messageProcessedCount;
    }

    public String getDefaultPromptDefinition()
    {
        return _defaultPromptDefinition;
    }

    public void setDefaultPromptDefinition(String defaultPromptDefinition)
    {
        this._defaultPromptDefinition = defaultPromptDefinition;
    }

    public String getProcessAction()
    {
        return _processAction;
    }

    public void setProcessAction(String processAction)
    {
        this._processAction = processAction;
    }

    public String getDeleteAction()
    {
        return _deleteAction;
    }

    public void setDeleteAction(String deleteAction)
    {
        this._deleteAction = deleteAction;
    }
    
	public String getEditorSourceName() {
		return _editorSourceName;
	}

	public void setEditorSourceName(String editorSourceName) {
		this._editorSourceName = editorSourceName;
	}

}
