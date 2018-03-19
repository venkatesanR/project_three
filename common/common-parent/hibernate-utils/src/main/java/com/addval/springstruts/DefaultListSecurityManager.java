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

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class DefaultListSecurityManager implements FormSecurityManager
{
    private static final long serialVersionUID = 912302407164120993L;

    /**
    * @param mapping
    * @param request
    * @param form
    * @roseuid 3DE26F150131
    */
    public void enforceSecurity(ActionMapping mapping, HttpServletRequest request, ActionForm form)
    {
        //System.out.println("Enforcing List Security ");
        ListForm listForm = (ListForm) form;
        EditorMetaData metadata = listForm.getMetadata();
        boolean isOwner = request.isUserInRole("owner");

        //System.out.println("The user is a owner " + isOwner);
        if(metadata == null)
            return;

        //System.out.println("Metadata is not null ");
        for(int idx = 1; idx <= metadata.getColumnCount(); ++idx)
        {
            ColumnMetaData colMetadata = metadata.getColumnMetaData(idx);
            if(!colMetadata.isSearchable() || !colMetadata.isSecured())
                continue;

            if(isOwner)
                request.setAttribute(colMetadata.getName() + "_unlock", "true");

            String columnValue = getValueFromRequest(request, colMetadata);
            if((columnValue == null) || (columnValue.length() == 0))
                columnValue = (String) request.getSession().getAttribute(colMetadata.getName());

            if((columnValue == null) || (columnValue.length() == 0))
            {
                // Check if it is in the User_Profile
                HttpSession session = request.getSession();
                Map<String, String> userProfile = (Map<String, String>) session.getAttribute("USER_PROFILE");
                if(userProfile != null)
                    columnValue = (String) userProfile.get(colMetadata.getName());
            }

            setAttributes(request, colMetadata, columnValue);
        }
    }

    protected String getValueFromRequest(HttpServletRequest request, ColumnMetaData colMetadata)
    {
        return (String) request.getParameter(colMetadata.getName() + "_lookUp");
    }

    protected void setAttributes(HttpServletRequest request, ColumnMetaData colMetadata, String columnValue)
    {
        if((columnValue != null) && (columnValue.length() > 0))
            //System.out.println("Setting " + colMetadata.getName() + "_lookUp to = " + columnValue);
            request.setAttribute(colMetadata.getName() + "_lookUp", columnValue);
    }
}
