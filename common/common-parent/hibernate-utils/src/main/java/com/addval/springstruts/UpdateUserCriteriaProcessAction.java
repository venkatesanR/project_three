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
import com.addval.metadata.UpdateUserCriteria;

import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

import org.apache.regexp.RE;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UpdateUserCriteriaProcessAction extends BaseAction
{
    private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(UpdateUserCriteriaProcessAction.class);
    private UpdateUserCriteriaMgr _updateUserCriteriaManager;
    private UpdateUserCriteriaUtil _updateUserCriteriaUtility;

    public void setUpdateUserCriteriaManager(UpdateUserCriteriaMgr criteria)
    {
        _updateUserCriteriaManager = criteria;
    }

    public UpdateUserCriteriaMgr getUpdateUserCriteriaManager()
    {
        return _updateUserCriteriaManager;
    }

    public void setUpdateUserCriteriaUtility(UpdateUserCriteriaUtil criteria)
    {
        _updateUserCriteriaUtility = criteria;
    }

    public UpdateUserCriteriaUtil getUpdateUserCriteriaUtility()
    {
        return _updateUserCriteriaUtility;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res)
    {
        _logger.trace("execute.traceEnter");
        UpdateUserCriteriaForm critForm = null;
        UpdateUserCriteria criteria = null;
        ActionMessages messages = new ActionMessages();
        ActionErrors errors = new ActionErrors();
        try
        {
            super.execute(mapping, form, req, res);
            critForm = (UpdateUserCriteriaForm) form;
            EditorMetaData editorMetaData = critForm.getMetadata();
            if(editorMetaData == null)
            {
                //				critForm.reset(mapping, req);
                editorMetaData = lookupMetadata(critForm.getEditorName(), critForm.getEditorType(), null, req.getSession());
                critForm.setMetadata(editorMetaData);
                critForm.initializeFromMetadata(req, mapping, editorMetaData);
                editorMetaData = critForm.getMetadata();
            }

            UpdateUserCriteriaUtil util = getUpdateUserCriteriaUtility();
            UpdateUserCriteriaMgr manager = getUpdateUserCriteriaManager();
            String kindOfAction = (critForm.getKindOfAction() != null) ? critForm.getKindOfAction() : "";
            try
            {
                errors = critForm.validate(mapping, req);
                if(errors.size() > 0)
                {
                    saveErrors(req, errors);
                    return mapping.findForward(ListAction._ERROR_FORWARD);
                }

                if(kindOfAction.equals("insert"))
                {
                    String critName = critForm.getNewCriteriaName();
                    if((critName == null) || (critName.trim().length() < 1))
                        throw new XRuntime(getClass().getName(), ResourceUtils.message(req, "error.requiredcriterianame", "Please enter Criteria Name;"));

                    RE matcher = new RE("^\\w([\\-\\s\\w]*)$");
                    if(!matcher.match(critName))
                        throw new XRuntime(getClass().getName(), ResourceUtils.message(req, "error.validcriterianame", "Please enter valid Criteria Name;"));

                    criteria = util.getUpdateUserCriteria(critForm);
                    util.validateUpdateSql(editorMetaData, criteria);
                    try
                    {
                        manager.insert(criteria);
                        critForm.setCriteriaName(criteria.getCriteriaName());
                        critForm.setCriteriaDesc(criteria.getCriteriaDesc());
                        critForm.setNewCriteriaName(null);
                        critForm.setNewCriteriaDesc(null);

						String message = ResourceUtils.message(req, "info.updatecriteria.newsuccess", "New Update Criteria added Successfully.");													
                        messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
                    }
                    catch(XRuntime xr)
                    {
                        if((xr.getMessage().indexOf("SQLException: ORA-00001:") != -1) && (critForm.getOverwrite() != null) && critForm.getOverwrite().equalsIgnoreCase("YES"))
                            kindOfAction = "update";
                        else
                            throw xr;
                    }
                }

                if(kindOfAction.equals("update"))
                {
                    criteria = util.getUpdateUserCriteria(critForm);
                    util.validateUpdateSql(editorMetaData, criteria);
                    manager.update(criteria);
                    critForm.setCriteriaName(criteria.getCriteriaName());
                    critForm.setCriteriaDesc(criteria.getCriteriaDesc());
                    critForm.setOverwrite(null);
                    critForm.setNewCriteriaName(null);
                    critForm.setNewCriteriaDesc(null);

					String message = ResourceUtils.message(req, "info.updatecriteria.updatesuccess", "Update Criteria updated Successfully.");													
                    messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
                }
                else if(kindOfAction.equals("delete"))
                {
                    criteria = util.getUpdateUserCriteria(critForm);
                    manager.delete(criteria);
                    criteria = null;
                    critForm.setCriteriaName(null);
                    critForm.setCriteriaDesc(null);

					String message = ResourceUtils.message(req, "info.updatecriteria.deletesuccess", "Update Criteria deleted Successfully.");													
                    messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
                }
                else if(kindOfAction.equals("cancel"))
                {
                    _logger.trace("execute.traceExit");
                    return mapping.findForward(ListAction._CANCEL_FORWARD);
                }
                else if(kindOfAction.length() == 0)
                {
                    String directoryName = (String) req.getAttribute("directoryName");
                    String criteriaName = (String) req.getAttribute("criteriaName");
                    if(!StrUtl.isEmpty(directoryName))
                    {
                        critForm.setDirectoryName(directoryName);
                        if(!StrUtl.isEmpty(criteriaName))
                            critForm.setCriteriaName(criteriaName);
                    }
                }

                saveMessages(req, messages);
            }
            finally
            {
                critForm.setCriteria(criteria);
                util.setDirectoryNames(critForm);
                if((!StrUtl.isEmpty(critForm.getDirectoryName()) && !kindOfAction.equals("")) || (!StrUtl.isEmpty(critForm.getDirectoryName()) && !StrUtl.isEmpty(critForm.getCriteriaName())))
                    util.setCriteriaNames(critForm);

                if(!(kindOfAction.equals("update") || kindOfAction.equals("insert")))
                    util.populateData(critForm);
            }
        }
        catch(XRuntime xr)
        {
            critForm.setKindOfAction(null);
            _logger.error("Error looking up data in UpdateUserCriteriaProcessAction");
            _logger.error(xr);
            if(xr.getMessage().indexOf("SQLException: ORA-00001:") != -1)
            {
                critForm.setOverwrite("QUESTION");
				String message = ResourceUtils.message(req, "info.updatecriteria.overwritequestion", "Warning!  : Criteria Name already Exists. Please click YES to Overwrite it");													
				messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));                
                saveMessages(req, messages);
            }
            else
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage()));

            saveErrors(req, errors);
            return mapping.findForward(ListAction._ERROR_FORWARD);
        }
        catch(Exception ex)
        {
            critForm.setKindOfAction(null);
            _logger.error("Error looking up data in UpdateUserCriteriaProcessAction");
            _logger.error(ex);
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(req, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));			
            saveErrors(req, errors);
            return mapping.findForward(ListAction._ERROR_FORWARD);
        }

        _logger.trace("execute.traceExit");
        return mapping.findForward(ListAction._DONE_FORWARD);
    }
}
