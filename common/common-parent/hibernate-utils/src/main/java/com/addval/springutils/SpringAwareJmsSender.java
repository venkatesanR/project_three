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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.util.StringUtils;

import com.addval.jmsutils.JmsSender;
import com.addval.utils.ListUtl;
import com.addval.utils.LogMgr;

public class SpringAwareJmsSender implements JmsSender, ApplicationContextAware {
	private static transient final Logger _logger = LogMgr.getLogger(SpringAwareJmsSender.class);
	private String _templateNamePrefix = null;
	private List<JmsTemplate> _templateList = null;
	private ApplicationContext context = null;

	private boolean notifyMultiClusters = false;
	private String multiClusterUrlDelimiter = ";";
	private String connectionFactoryName;
	private String defaultDestinationName;
	private String multiClusterNamingFactoryInitial = null;
	private String multiClusterJndiUrls = null;

	public SpringAwareJmsSender() {
	}

	public void setTemplateNamePrefix(String aPrefix) {
		_templateNamePrefix = aPrefix;
	}

	private String getTemplateNamePrefix() {
		return _templateNamePrefix;
	}

	private List<JmsTemplate> getTemplateList() {
		return _templateList;
	}

	public void setTemplateList(List<JmsTemplate> aList) {
		_templateList = aList;
	}

	public String getNotifyMultiClusters() {
		return String.valueOf(notifyMultiClusters);
	}

	public void setNotifyMultiClusters(String notifyMultiClustersStr) {
		if (null != notifyMultiClustersStr) {
			this.notifyMultiClusters = Boolean.valueOf(notifyMultiClustersStr);
		}
	}

	public String getMultiClusterUrlDelimiter() {
		return multiClusterUrlDelimiter;
	}

	public void setMultiClusterUrlDelimiter(String multiClusterUrlDelimiter) {
		this.multiClusterUrlDelimiter = multiClusterUrlDelimiter;
	}

	public String getMultiClusterNamingFactoryInitial() {
		return multiClusterNamingFactoryInitial;
	}

	public void setMultiClusterNamingFactoryInitial(String multiClusterNamingFactoryInitial) {
		this.multiClusterNamingFactoryInitial = multiClusterNamingFactoryInitial;
	}

	public String getMultiClusterJndiUrls() {
		return multiClusterJndiUrls;
	}

	public void setMultiClusterJndiUrls(String multiClusterJndiUrls) {
		this.multiClusterJndiUrls = multiClusterJndiUrls;
	}

	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}

	public String getDefaultDestinationName() {
		return defaultDestinationName;
	}

	public void setDefaultDestinationName(String defaultDestinationName) {
		this.defaultDestinationName = defaultDestinationName;
	}

	public int sendMessage(final Object objectToBeSent) {
		return sendMessage(objectToBeSent, null);
	}

	// to be removed after correcting cargores Code
	public int sendObject(final Object object) {
		return sendMessage(object);
	}

	// to be removed after correcting cargores Code
	public int sendObject(final Object object, final String destinationName) {
		return sendMessage(object, destinationName);
	}

	// to use this method directly, the jmsTemplate(s) should have the suitable DestinationResolver injected
	// returns the failed counts
	public int sendMessage(final Object objectToBeSent, final String destinationName) {
		int failedCount = 0;
		if (ListUtl.isEmpty(getTemplateList())) {
			init();
		}
		for (JmsTemplate tpl : getTemplateList()) {
			try {
				if (destinationName == null) {
					tpl.convertAndSend(objectToBeSent);
				}
				else {
					tpl.convertAndSend(destinationName, objectToBeSent);
				}
			}
			catch (Exception e) {
				failedCount++;
				_logger.warn(e);
			}
		}
		return failedCount;
	}

	public int sendMsg(MessageCreator messageCreator) {
		return sendMsg(messageCreator, null);
	}

	public int sendMsg(MessageCreator messageCreator, String destinationName) {
		int failedCount = 0;
		if (ListUtl.isEmpty(getTemplateList())) {
			init();
		}
		for (JmsTemplate tpl : getTemplateList()) {
			try {
				if (destinationName == null) {
					tpl.send(messageCreator);
				}
				else {
					tpl.send(destinationName, messageCreator);
				}
			}
			catch (Exception e) {
				failedCount++;
				_logger.warn(e);
			}
		}
		return failedCount;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	private synchronized void init() {
		ArrayList<JmsTemplate> templateList = new ArrayList<JmsTemplate>();
		if (notifyMultiClusters && multiClusterJndiUrls != null) {
			String[] multiClusterJndiUrlArr = StringUtils.delimitedListToStringArray(multiClusterJndiUrls, multiClusterUrlDelimiter);
			for (String jndiUrl : multiClusterJndiUrlArr) {
				try {
					templateList.add(getJmsTemplate(jndiUrl));
				}
				catch (Exception e) {
					_logger.warn(e);
				}
			}
		}
		else {
			Map<String, JmsTemplate> map = context.getBeansOfType(JmsTemplate.class);
			for (Entry<String, JmsTemplate> entry : map.entrySet()) {
				// if the namePrefix is similar add to the templateList
				if (getTemplateNamePrefix() == null || entry.getKey().indexOf(getTemplateNamePrefix()) != -1) {
					templateList.add(entry.getValue());
				}
			}
		}
		setTemplateList(templateList);
	}

	private JmsTemplate getJmsTemplate(String jndiUrl) throws NamingException, ClassNotFoundException {
		JndiTemplate jndiTemplate = new JndiTemplate();
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial", multiClusterNamingFactoryInitial);
		properties.put("java.naming.provider.url", jndiUrl);
		jndiTemplate.setEnvironment(properties);

		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiTemplate(jndiTemplate);
		jndiObjectFactoryBean.setJndiName(connectionFactoryName);
		jndiObjectFactoryBean.setLookupOnStartup(false);
		jndiObjectFactoryBean.setProxyInterface(Class.forName("javax.jms.ConnectionFactory"));
		jndiObjectFactoryBean.afterPropertiesSet();

		JndiDestinationResolver destinationResolver = new JndiDestinationResolver();
		destinationResolver.setJndiTemplate(jndiTemplate);

		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory((ConnectionFactory) jndiObjectFactoryBean.getObject());
		jmsTemplate.setDestinationResolver(destinationResolver);
		jmsTemplate.setDefaultDestinationName(defaultDestinationName);
		jmsTemplate.setPubSubDomain(true);
		return jmsTemplate;
	}
}
