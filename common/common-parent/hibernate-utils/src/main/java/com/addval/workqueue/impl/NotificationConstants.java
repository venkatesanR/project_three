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

public class NotificationConstants
{
    /**
     * Possible Notification types.
     * (These correspond to static schema in RS_NOTIFY_TYPE.RS_NOTIFY_TYPE_CODE.)
     */
    public static String _RS_NOTIFICATION_TYPE_QUEUE = "QUEUE";
    public static String _RS_NOTIFICATION_TYPE_EMAIL = "EMAIL";
    public static String _RS_NOTIFICATION_TYPE_TELEX = "TELEX";
    /**
     * Possible Prompt types.
     * (These correspond to static schema in RS_PROMPT_TYPE.RS_PROMPT_TYPE_CODE.)
     */
    public static String _RS_PROMPT_TYPE_ACK_REQUIRED = "ACK_REQUIRED";
    public static String _RS_PROMPT_TYPE_ACK_NOTREQUIRED = "ACK_NOTREQUIRED";
    public static String _RS_PROMPT_TYPE_ACK_OPTIONAL = "ACK_OPTIONAL";
    /**
     * Possible Prompt definition codes
     * (These correspond to static schema in RS_PROMPT_DEFINITION.RS_PROMPT_DEFINITION_CODE.)
     */
    public static String _RS_ACCEPT_REJECT = "ACCEPT_REJECT";
    public static String _RS_ACCEPT_REJECT_REFER = "ACCEPT_REJECT_REFER";
    public static String _RS_RESPONSE_NOT_REQUIRED = "RESPONSE_NOT_REQUIRED";
}
