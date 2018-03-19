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

import com.addval.wqutils.metadata.WQMetaData;

import java.util.List;

import org.apache.struts.util.LabelValueBean;


public class WorkQueueListForm extends ListForm
{
    private static final long serialVersionUID = -249688286522292168L;
    private String queueName = null;
    private WQMetaData wqMetaData = null;
    private List<LabelValueBean> queueNames = null;

    public List<LabelValueBean> getQueueNames()
    {
        return queueNames;
    }

    public void setQueueNames(List<LabelValueBean> queueNames)
    {
        this.queueNames = queueNames;
    }

    public WQMetaData getWQMetaData()
    {
        return wqMetaData;
    }

    public void setWQMetaData(WQMetaData wqMetaData)
    {
        this.wqMetaData = wqMetaData;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }
}
