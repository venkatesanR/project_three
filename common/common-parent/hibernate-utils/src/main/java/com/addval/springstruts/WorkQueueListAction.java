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

import com.addval.metadata.EditorMetaData;

import com.addval.utils.StrUtl;
import com.addval.utils.WorkQueueUtils;

import com.addval.workqueue.api.WQSMetaData;

import com.addval.wqutils.metadata.WQMetaData;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class WorkQueueListAction extends ListAction
{
    private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WorkQueueListAction.class);
    private WQSMetaData wqSMetaData = null;

    private WQSMetaData getWqSMetaData()
    {
        return wqSMetaData;
    }

    public void setWqSMetaData(WQSMetaData wqSMetaData)
    {
        this.wqSMetaData = wqSMetaData;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        try
        {
            WorkQueueListForm listForm = (WorkQueueListForm) form;
            String queueName = listForm.getQueueName();
            Boolean isQueueMetadataExists = false;
            if(queueName == null)
            {
                queueName = request.getParameter("queueName");
                listForm.setQueueName(queueName);
            }

            String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";
            List<LabelValueBean> queueNames = new ArrayList<LabelValueBean>();
            if(!StrUtl.isEmptyTrimmed(queueName))
            {
                List<WQMetaData> wqMetaDatas = lookupWqSMetaData(getWqSMetaData(), request.getSession(), WorkQueueUtils.getQueueNames(request));
                if(wqMetaDatas != null)
                {
                    for(WQMetaData wqMetaData : wqMetaDatas)
                    {
                        if(wqMetaData.getQueueName().equals(queueName))
                        {
                        	String previousEditorName=listForm.getEditorName();
                			String componentPrefix = request.getParameter("componentPrefix") != null ? request.getParameter("componentPrefix") : "";
                        	EditorMetaData editorMetaData = lookupMetadata(wqMetaData.getEditorMetaData().getName(), wqMetaData.getEditorMetaData().getType(), userName,null,componentPrefix, request.getSession(),true);
                            listForm.setEditorName(wqMetaData.getEditorMetaData().getName());
                            listForm.setMetadata(editorMetaData);
                            listForm.initializeFromMetadata(request, mapping, editorMetaData);
                            String pageSize = request.getParameter("PAGESIZE");
                    		if (!StrUtl.isEmptyTrimmed(pageSize)) {
                    			listForm.setPageSize(pageSize);
                    		}
                            String queueCriteria = wqMetaData.getQueueWhereClause(request);
                            request.setAttribute("QUEUE_CRITERIA", queueCriteria);
                            listForm.setWQMetaData(wqMetaData);
                            if(!wqMetaData.getEditorMetaData().getName().equals(previousEditorName)){ // Clearing DisplayMetaData based on the comparision of old & new editorname.
                            	listForm.setDisplayMetadata(null);
                        	}
                            isQueueMetadataExists = true;
                        }

                        queueNames.add(new LabelValueBean(wqMetaData.getQueueDesc(), wqMetaData.getQueueName()));
                    }
                }
            }
            if(!isQueueMetadataExists){
            	return mapping.findForward("error");
            }
            listForm.setQueueNames(queueNames);
            return super.execute(mapping, listForm, request, response);
        }
        catch(com.addval.utils.XRuntime ex)
        {
            _logger.error("Error looking up data in WorkQueueListAction", ex );
            ActionErrors errors = new ActionErrors();
            String errMsg = ex.getMessage();
            int index = errMsg.indexOf("\n");
            if(index > 0)
                errMsg = errMsg.substring(0, index);

            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
            saveErrors(request, errors);
            return mapping.findForward(ListProcessAction._SHOWLIST_FORWARD);
        }
        catch(java.lang.Exception ex)
        {
            _logger.error("Error looking up data in WorkQueueListAction", ex);
            String message = "Critical System Error. Please contact your System Administrator.";
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", message));
            saveErrors(request, errors);
            return mapping.findForward(ListProcessAction._SHOWLIST_FORWARD);
        }
    }
}
