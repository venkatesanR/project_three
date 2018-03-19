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
import javax.servlet.http.HttpServletRequest;


public class DefaultEditSecurityManager extends DefaultListSecurityManager
{
	private static final long serialVersionUID = -1253733626496442726L;

	protected String getValueFromRequest(HttpServletRequest request, ColumnMetaData colMetadata)
    {
        String value = super.getValueFromRequest(request, colMetadata);
        if(value != null && !value.trim().equals( "" ))
            return value;

        return (String) request.getParameter(colMetadata.getName() + "_edit");
    }

    protected void setAttributes(HttpServletRequest request, ColumnMetaData colMetadata, String columnValue)
    {
        super.setAttributes(request, colMetadata, columnValue);
        if(columnValue == null || columnValue.length() == 0)
            request.setAttribute(colMetadata.getName() + "_edit", columnValue);
    }
}
