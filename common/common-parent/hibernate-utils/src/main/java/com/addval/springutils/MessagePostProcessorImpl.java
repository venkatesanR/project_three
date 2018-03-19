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
package com.addval.springutils;

import com.addval.jmsutils.MessageProperty;
import com.addval.utils.LogMgr;

import org.apache.log4j.Logger;
import org.springframework.jms.core.MessagePostProcessor;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;


public class MessagePostProcessorImpl implements MessagePostProcessor
{
    private static transient final Logger _logger = LogMgr.getLogger(MessagePostProcessorImpl.class);
    private List<MessageProperty> _properties = null;

    public MessagePostProcessorImpl()
    {
    }

    public MessagePostProcessorImpl(List<MessageProperty> properties)
    {
        _properties = properties;
    }

    public List<MessageProperty> getProperties()
    {
        return _properties;
    }

    public void setProperties(List<MessageProperty> properties)
    {
        _properties = properties;
    }

    public Message postProcessMessage(Message message) throws JMSException
    {
    	_logger.debug( "postProcessMessage called for message posted in " + message.getJMSDestination() + " ID is " + message.getJMSMessageID());
    	if(getProperties() == null) {
        	_logger.debug( "No Properties specified for message posted in " + message.getJMSDestination() + " ID is " + message.getJMSMessageID());
            return message;
    	}

        for(MessageProperty property : getProperties())
            setMessageProperty(message, property);

        return message;
    }

    public void setMessageProperty(Message message, MessageProperty property) throws JMSException
    {
        if(property == null)
            return;

        String type = property.getType();
        if(type == null)
            return;
        _logger.debug( "Property is being set to Message - " + property );
        if(type.equals(MessageProperty._STRING))
            message.setStringProperty(property.getName(), property.getStringValue());
        else if(type.equals(MessageProperty._LONG))
            message.setLongProperty(property.getName(), property.getLongValue());
        else if(type.equals(MessageProperty._DOUBLE))
            message.setDoubleProperty(property.getName(), property.getDoubleValue());
        else if(type.equals(MessageProperty._FLOAT))
            message.setFloatProperty(property.getName(), property.getFloatValue());
        else if(type.equals(MessageProperty._INT))
            message.setIntProperty(property.getName(), property.getIntValue());
        else if(type.equals(MessageProperty._SHORT))
            message.setShortProperty(property.getName(), property.getShortValue());
        else if(type.equals(MessageProperty._BYTE))
            message.setByteProperty(property.getName(), property.getByteValue());
        else if(type.equals(MessageProperty._BOOLEAN))
            message.setBooleanProperty(property.getName(), property.getBooleanValue());
        else
            message.setObjectProperty(property.getName(), property.getValue());
    }
}
