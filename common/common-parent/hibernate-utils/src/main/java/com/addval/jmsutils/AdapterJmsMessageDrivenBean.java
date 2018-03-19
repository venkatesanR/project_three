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

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;

import org.apache.log4j.Logger;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ejb.support.AbstractJmsMessageDrivenBean;

import com.addval.parser.InvalidInputException;
import com.addval.utils.LogMgr;
import com.addval.utils.XInvalidInput;

public abstract class AdapterJmsMessageDrivenBean extends AbstractJmsMessageDrivenBean
{
    private static final long serialVersionUID = -9197767680219933528L;
    private static transient Logger _logger = null;
    private MessageProcessor messageProcessor = null;
    private MessageDrivenContext _context = null;
    
    /*
     * For EAR with only MDBs (eg. commsear), AdapterJmsMessageDrivenBean is the class responsible for initializing the springLog4j.xml
     * 
     */
    public AdapterJmsMessageDrivenBean(){
		if (!LogMgr.isConfigured()) {
			debug("Initialize log4j using LogMgr.doConfigure()");
			new ClassPathXmlApplicationContext(new String[] { "classpath*:springLog4j.xml" });
		}
		if(_logger == null){
			_logger = LogMgr.getLogger(AdapterJmsMessageDrivenBean.class);
		}
    }

    // generic Adapter MDB for alert MDBs.
    // onEjbCreate() method should be implemented by the extending classes to
    // set the specific MessageProcessor
    public void setMessageDrivenContext(MessageDrivenContext context) throws EJBException
    {
        super.setMessageDrivenContext(context);
        _context = context;
        setBeanFactoryLocator(ContextSingletonBeanFactoryLocator.getInstance());
        setBeanFactoryLocatorKey(getClass().getPackage().getName());
    }

    protected MessageProcessor getMessageProcessor(String name)
    {
        return (MessageProcessor) getBeanFactory().getBean(name);
    }

    protected void setMessageProcessor(String name)
    {
        setMessageProcessor(getMessageProcessor(name));
    }

    protected void setMessageProcessor(MessageProcessor messageProcessor)
    {
        this.messageProcessor = messageProcessor;
    }

    protected MessageProcessor getMessageProcessor()
    {
        return messageProcessor;
    }

    public void onMessage(Message message)
    {
    	// When server is not able to be connected for Bean lookup it is a fatal error 
    	// and so do a rollback so that no message is lost
    	try
        {
            getMessageProcessor().processMessage(message);
        }
        catch(Exception ex)
        {
            _logger.error("Error processing the Message: \n", ex);
        	// No need to rollback if there is any Validation Application exception
        	if (!(ex instanceof InvalidInputException || ex instanceof XInvalidInput))	{
        		_context.setRollbackOnly();
        	}
        }
    }
	
    private void debug(String msg) {
		if (logger != null) {
			logger.debug(msg);
		}
		else {
			System.out.println(msg);
		}
	}
}
