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
package com.addval.jmsutils;

import org.springframework.jms.core.MessageCreator;

public interface JmsSender
{
    // to be removed after correcting cargores Code
    public int sendObject(final Object object);

    // to be removed after correcting cargores Code
    public int sendObject(final Object object, final String destinationName);

    public int sendMessage(final Object objectToSend);

	public int sendMessage(final Object objectToSend, final String destinationName);	
	
	public int sendMsg(MessageCreator messageCreator);

	public int sendMsg(MessageCreator messageCreator, String destinationName);
}