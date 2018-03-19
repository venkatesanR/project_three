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

import com.addval.utils.LogMgr;

import org.apache.log4j.Logger;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;

import org.springframework.remoting.RemoteLookupFailureException;

import javax.jms.Message;
import javax.jms.TextMessage;


/*
 * generic class to distribute a jms message to multiple queues based on criteria
 * */
public class JmsMessageDistributor implements MessageProcessor
{
    private Logger _logger = null;
    private Logger _errLogger = null;
    private String _baseQueueName = null;
    private DestinationResolver _destinationResolver = null;
    private JmsTemplate _jmsTemplate = null;
    private int _poolSize = 1;

    public JmsMessageDistributor()
    {
        setLogger(LogMgr.getLogger("JmsMessageDistributor"));
        setLogger(LogMgr.getLogger("JmsMessageDistributor_Error"));
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate)
    {
        _jmsTemplate = jmsTemplate;
    }

    protected JmsTemplate getJmsTemplate()
    {
        return _jmsTemplate;
    }

    public void setBaseQueueName(String baseQueueName)
    {
        _baseQueueName = baseQueueName;
    }

    protected String getBaseQueueName()
    {
        return _baseQueueName;
    }

    protected void setLogger(Logger logger)
    {
        _logger = logger;
    }

    protected void setErrorLogger(Logger logger)
    {
        _errLogger = logger;
    }

    protected Logger getLogger()
    {
        return _logger;
    }

    protected Logger getErrorLogger()
    {
        return _errLogger;
    }

    public void setQueuePoolSize(int size)
    {
        _poolSize = size;
    }

    protected int getQueuePoolSize()
    {
        return _poolSize;
        //        return getJmsTemplates().size();
    }

    public void setDestinationResolver(DestinationResolver resolver)
    {
        _destinationResolver = resolver;
    }

    protected DestinationResolver getDestinationResolver()
    {
        return _destinationResolver;
    }

    public void processMessage(Message message) throws Exception
    {
        if(!(message instanceof TextMessage))
            getErrorLogger().error("Unknown message reached bean " + message);
        else
            processMessage((TextMessage) message);
    }

    protected void processMessage(TextMessage message) throws Exception
    {
        processMessage(message, message.getText());
    }

    protected void processMessage(Message message, String msg) throws Exception
    {
        try
        {
            if(msg != null)
                getLogger().info(msg);

            String queueName = getQueueName(message, msg);
            if(queueName == null)
            {
                getErrorLogger().error("Non-standard Message Encountered. " + message);
                return;
            }

            getJmsTemplate().convertAndSend(queueName, message);
            getLogger().info("The message is distributed to Queue :" + queueName);
        }
        catch(RemoteLookupFailureException e)
        {
            getErrorLogger().error("Unable to obtain the remote object : ", e);
            throw e;
        }
        catch(Exception e)
        {
            getErrorLogger().error("****************** Error processing the Message: \n" + msg, e);
        }
    }

    // sub classes should have their own way to arrive at the queueName for distribution
    protected String getQueueName(Message message, String msg) throws Exception
    {
        return getBaseQueueName() + getSuffix(msg.hashCode());
    }

    protected int getSuffix(int value)
    {
    	if (getQueuePoolSize() <1)
    		throw new IllegalStateException( "queuePoolSize should be at least 1. Currently the value is " + getQueuePoolSize() );
        return Math.abs(value) % getQueuePoolSize();
    }
}
