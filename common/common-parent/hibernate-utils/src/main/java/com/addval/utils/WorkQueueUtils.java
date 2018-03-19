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
package com.addval.utils;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;

import com.addval.environment.Environment;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;

import com.addval.parser.InvalidInputException;

import com.addval.wqutils.utils.WQParam;
import com.addval.wqutils.utils.WQQueue;

import org.apache.commons.beanutils.WrapDynaBean;

import java.io.Serializable;

import java.sql.Date;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class WorkQueueUtils implements Serializable
{
    private static final long serialVersionUID = -1330135937438892008L;
    private static Class thisClass = null;
    private static final String module = "com.addval.utils.WorkQueueUtils";
    private static final String WORKQUEUE_UTILS_CLASS_NAME = "WorkQueueUtils.class";
    private static final String SUBSYSTEM = "workqueue_client";

    public static WorkQueueUtils getInstance()
    {
        if(thisClass == null)
        {
            try
            {
                String className = Environment.getInstance(SUBSYSTEM).getCnfgFileMgr().getPropertyValue(WORKQUEUE_UTILS_CLASS_NAME, module);
                thisClass = Class.forName(className);
            }
            catch(Exception e)
            {
                try
                {
                    thisClass = Class.forName(module);
                }
                catch(Exception ex)
                {
                    return null;
                }
            }
        }

        try
        {
            return (WorkQueueUtils) thisClass.newInstance();
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static Hashtable<String, ?> getWQFilters(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        return (Hashtable<String, ?>) session.getAttribute("USER_PROFILE");
    }

    public static Vector<String> getQueueNames(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if (session.getAttribute("QUEUE_NAMES") == null)
        	return null;
        List<String> queueNames = (List<String>) session.getAttribute("QUEUE_NAMES");
        return new Vector<String>( queueNames );
    }

    public static EJBResultSet getUpdateEJBResultSet(EditorMetaData editorMetaData, Map params)
    {
    	EJBResultSet ejbResultSet = getNewEjbResultSet( editorMetaData );
        int size = editorMetaData.getColumnCount();
        ejbResultSet.updateRow();
        for(int index = 1; index <= size; index++)
        {
        	ColumnMetaData columnMetaData = editorMetaData.getColumnMetaData(index);
            if (!params.containsKey(columnMetaData.getName()))
            	continue;
            String colValue = (String) params.get(columnMetaData.getName());
            if (colValue != null && colValue.length() > 0)
                ejbResultSet.updateString( index, colValue );
        }

        return ejbResultSet;
    }

    public static EJBResultSet getDeleteEJBResultSet(EditorMetaData editorMetaData, Map params)
    {
        EJBResultSet ejbResultSet = getNewEjbResultSet( editorMetaData );
        int size = editorMetaData.getColumnCount();
        if (size == 0)
            throw new com.addval.utils.XRuntime("Utils.getDeleteEJBResultSet", "No column exist in the editor metadata");
        boolean keysExist = false;
        // This is a delete row
        ejbResultSet.deleteRow();
        for(int index = 1; index <= size; index++)
        {
        	ColumnMetaData columnMetaData = editorMetaData.getColumnMetaData(index);
            if (!params.containsKey(columnMetaData.getName()))
            	continue;
            String keyValue = (String) params.get(columnMetaData.getName());
            if((keyValue != null) && (keyValue.length() > 0))
            {
                ejbResultSet.updateString(index, keyValue);
                keysExist = true;
            }
        }

        if(!keysExist)
            throw new com.addval.utils.XRuntime("Utils.getDeleteEJBResultSet", "No keys exist for deleting a record");

        return ejbResultSet;
    }

    public static EJBResultSet getInsertEJBResultSet(EditorMetaData editorMetaData, Map params)
    {
        EJBResultSet ejbResultSet = getNewEjbResultSet( editorMetaData );
        ejbResultSet.insertRow();
        Object value = null;
        for(int index = 1; index <= editorMetaData.getColumnCount(); index++)
        {
            if(!params.containsKey(editorMetaData.getColumnMetaData(index).getName()))
                continue;

            value = params.get(editorMetaData.getColumnMetaData(index).getName());
            if(value == null)
                continue;

            if(value instanceof String)
                ejbResultSet.updateString(index, (String) value);
            else if(value instanceof Date)
                ejbResultSet.updateDate(index, (Date) value);
            else if(value instanceof java.util.Date)
                ejbResultSet.updateDate(index, new Date(((java.util.Date) value).getTime()));
            else
                throw new RuntimeException("Value is not a Date or String - " + value + " " + value.getClass().getName());
        }

        return ejbResultSet;
    }

    public static HashMap getQueueParams(String subsystem, String queueName) throws InvalidInputException
    {
        HashMap<String, String> params = new HashMap<String, String>();
        WQQueue wqQueue = getWQQueue(subsystem, queueName);
        Hashtable<String, WQParam> wqparams = wqQueue.getWQParams();
        for(Iterator iterator = wqparams.values().iterator(); iterator.hasNext();)
        {
        	WQParam param = (WQParam) iterator.next();
            params.put(param.getKey(), null);
        }

        return params;
    }

    public static void setQueueParamValues(String subsystem, String queueName, Map<String, Object> params, Object instance) throws InvalidInputException
    {
        if(instance == null)
            return;

        WQQueue wqQueue = getWQQueue(subsystem, queueName);
        Hashtable<String, WQParam> wqparams = wqQueue.getWQParams();
        WrapDynaBean bean = new WrapDynaBean(instance);
        for(Iterator iterator = wqparams.values().iterator(); iterator.hasNext();)
        {
        	WQParam param = (WQParam) iterator.next();
            if (bean.getDynaClass().getDynaProperty(param.getName()) != null)
                params.put(param.getKey(), bean.get( param.getName() ) );
        }
    }

    public static EJBResultSet getNewEjbResultSet(EditorMetaData editorMetaData)
    {
        EJBResultSetMetaData ejbResultSetMetaData = new EJBResultSetMetaData(editorMetaData);
        EJBCriteria ejbCriteria = ejbResultSetMetaData.getNewCriteria();
        return new EJBResultSet(ejbResultSetMetaData, ejbCriteria);
    }
    
    private static WQQueue getWQQueue(String subsystem, String queuename) throws InvalidInputException
    {
        Map<String, WQQueue> queues = (Map) Environment.getInstance(subsystem).getCacheMgr().get("WQQueue");
        WQQueue queue = (WQQueue) queues.get( queuename );
        if (queue == null)
            throw new InvalidInputException("WorkQueueUtils:getWQQueue() : " + queuename + " is not found.");

        return queue;
    }

    public static void setAttributes(EJBResultSet ejbRS, HttpServletRequest request) throws InvalidInputException
    {
        if(ejbRS == null)
            return;

        ejbRS.beforeFirst();
        if(!ejbRS.next())
            return;

        Vector<ColumnMetaData> keyColumns = ((EJBResultSetMetaData) ejbRS.getMetaData()).getEditorMetaData().getKeyColumns();
        if(keyColumns == null)
            return;

        for(ColumnMetaData columnMetaData : keyColumns)
            request.setAttribute(columnMetaData.getName() + "_KEY", ejbRS.getString(columnMetaData.getName()));
    }
}
