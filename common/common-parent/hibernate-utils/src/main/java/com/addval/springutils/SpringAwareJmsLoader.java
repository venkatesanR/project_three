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

import java.util.Map;

import com.addval.environment.Environment;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;

public class SpringAwareJmsLoader implements ApplicationListener
{
    private Environment environment = null;

    protected Environment getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }

    public void onApplicationEvent(ApplicationEvent event)
    {
		if (!(event instanceof ContextRefreshedEvent))
			return;

        Map<String, JmsOperations> jmsSenders = environment.getJmsSenders();

        if(jmsSenders != null && !jmsSenders.isEmpty())
            // templateList is already configured. do not do anything
            return;

		ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();
        // get all the jms templates
        Map<java.lang.String,org.springframework.jms.core.JmsTemplate> tplMap = ctx.getBeansOfType(JmsTemplate.class);

        java.util.Hashtable<java.lang.String,org.springframework.jms.core.JmsOperations> opsMap = new java.util.Hashtable<java.lang.String,org.springframework.jms.core.JmsOperations>();

   		java.util.Iterator e = tplMap.keySet().iterator();

		 while (e.hasNext())      {
			 String key = (String) e.next();

		 	opsMap.put(key, ((org.springframework.jms.core.JmsOperations) tplMap.get(key)) );
		}

        environment.setJmsSenders(opsMap);

    }
}
