package com.addval.springutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

import com.addval.utils.LogMgr;

/*
Configure utility spring id and businessInterface name as same.If it is different for some reason then configure alias property

In commandejb module

<bean id="com.addval.command.api.CommandDispatcher" class="com.addval.command.impl.CommandDispatcherUtility">
</bean>

Client JNDI lookup

<bean id="commandDispatcher" class="org.springframework.ejb.access.SimpleRemoteStatelessSessionProxyFactoryBean">
  <property name="jndiName" value="${app_jndi_prefix}CommandDispatcherBean"/>
  <property name="businessInterface" value="com.addval.command.api.CommandDispatcher"/>
  <property name="jndiTemplate" ref="appsclusterJndiTemplate"/>
  <property name="lookupHomeOnStartup" value="false"/>
  <property name="resourceRef" value="false"/>
</bean>

*/

public class AVBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	private transient Logger logger = null;
	public static String newline = System.getProperty("line.separator");

	private List<SessionBeanDefinition> ejbSessionBeanDefinitions = null;
	private static Map<String, HashMap<String, SessionBeanDefinition>> ejbSessionBeans = new HashMap<String, HashMap<String, SessionBeanDefinition>>();
	private static List<String> logs = new ArrayList<String>();

	private Map<String, String> alias = new HashMap<String, String>();
	private List<String> ejbExcludes = new ArrayList<String>();

	public Map<String, String> getAlias() {
		return alias;
	}

	public void setAlias(Map<String, String> alias) {
		this.alias = alias;
	}

	public List<String> getEjbExcludes() {
		return ejbExcludes;
	}

	public void setEjbExcludes(List<String> ejbExcludes) {
		this.ejbExcludes = ejbExcludes;
	}

	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (logger == null) {
			logger = LogMgr.getLogger(AVBeanDefinitionRegistryPostProcessor.class);
		}
		logger.info("postProcessBeanDefinitionRegistry : " + (registry.getBeanDefinitionCount()));

		StringBuffer discrepancy = new StringBuffer();
		GenericBeanDefinition bd = null;
		BeanDefinition beanDefinition = null;
		String beanClassName = null;
		String businessInterface = null;
		String jndiName = null;
		String ejbExclude = null;
		List<PropertyValue> propertyValues = null;
		TypedStringValue typedStringValue = null;
		ejbSessionBeanDefinitions = new ArrayList<SessionBeanDefinition>();

		// REMOVE BeanDefinition
		for (String beanId : registry.getBeanDefinitionNames()) {
			beanDefinition = registry.getBeanDefinition(beanId);
			beanClassName = beanDefinition.getBeanClassName();

			if (beanClassName.equalsIgnoreCase("org.springframework.ejb.access.SimpleRemoteStatelessSessionProxyFactoryBean")) {
				propertyValues = beanDefinition.getPropertyValues().getPropertyValueList();
				businessInterface = null;
				jndiName = null;
				for (PropertyValue value : propertyValues) {
					if (value.getName().equalsIgnoreCase("businessInterface")) {
						typedStringValue = (TypedStringValue) value.getValue();
						businessInterface = typedStringValue.getValue();
					}
					else if (value.getName().equalsIgnoreCase("jndiName")) {
						typedStringValue = (TypedStringValue) value.getValue();
						jndiName = typedStringValue.getValue();
					}
				}

				if (businessInterface != null && jndiName != null) {
					ejbExclude = jndiName.replace("${app_jndi_prefix}", "");
					if (!getEjbExcludes().contains(ejbExclude)) {
						logger.info("REMOVE :" + beanId + "\t:" + beanClassName + "\t:" + businessInterface);
						ejbSessionBeanDefinitions.add(new SessionBeanDefinition(beanId, jndiName, businessInterface, beanDefinition.getResourceDescription()));
						registry.removeBeanDefinition(beanId);
					}
				}
			}
		}

		// ADD BeanDefinition
		for (SessionBeanDefinition sBean : ejbSessionBeanDefinitions) {

			/* We have circular dependency issue with this setting. We should use ProxyFactoryBean
			bd = new GenericBeanDefinition();
			bd.setBeanClass(SpringBeanAliasFactoryBean.class);
			bd.getPropertyValues().add("target", sBean.getBusinessInterface());
			registry.registerBeanDefinition(sBean.getBeanId(), bd);
			*/

			ejbExclude = sBean.getJndiName().replace("${app_jndi_prefix}", "");
			if (!getEjbExcludes().contains(ejbExclude)) {
				bd = new GenericBeanDefinition();
				bd.setBeanClass(ProxyFactoryBean.class);
				RootBeanDefinition lazyBean = new RootBeanDefinition(LazyInitTargetSource.class);
				if (getAlias().containsKey(sBean.getBeanId())) {
					lazyBean.getPropertyValues().add("targetBeanName", getAlias().get(sBean.getBeanId()));
				}
				else {
					lazyBean.getPropertyValues().add("targetBeanName", sBean.getBusinessInterface());
				}

				bd.getPropertyValues().add("targetSource", new BeanDefinitionHolder(lazyBean, sBean.getBeanId() + "LazyBean"));

				registry.registerBeanDefinition(sBean.getBeanId(), bd);

				logger.info("ADD :" + sBean.getBeanId() + "=" + sBean.getBusinessInterface() + (getAlias().containsKey(sBean.getBeanId()) ? "->" + getAlias().get(sBean.getBeanId()) : ""));

				if (!ejbSessionBeans.containsKey(sBean.getBusinessInterface())) {
					ejbSessionBeans.put(sBean.getBusinessInterface(), new HashMap<String, SessionBeanDefinition>());
					ejbSessionBeans.get(sBean.getBusinessInterface()).put(sBean.getJndiName(), sBean);
				}
				else {
					ejbSessionBeans.get(sBean.getBusinessInterface()).put(sBean.getJndiName(), sBean);
				}
			}
		}

		for (String key : ejbSessionBeans.keySet()) {
			if (ejbSessionBeans.get(key).size() > 1) {
				for (SessionBeanDefinition sBean : ejbSessionBeans.get(key).values()) {
					if (!getAlias().containsKey(sBean.getBeanId()) && !logs.contains(sBean.toString())) {
						logs.add(sBean.toString());
						System.out.println(sBean);
					}
				}
			}
		}

		if (discrepancy.length() > 0) {
			System.out.println("Configure alias bean name in AVBeanDefinitionRegistryPostProcessor bean");
			System.out.println(discrepancy);
		}
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		logger.info("postProcessBeanFactory : " + (beanFactory.getBeanDefinitionCount()));
	}

	private class SessionBeanDefinition {
		private String beanId = null;
		private String jndiName = null;
		private String businessInterface = null;
		private String resourceDescription = null;

		SessionBeanDefinition(String beanId, String jndiName, String businessInterface, String resourceDescription) {
			this.beanId = beanId;
			this.jndiName = jndiName;
			this.businessInterface = businessInterface;
			this.resourceDescription = resourceDescription;
		}

		public String getBeanId() {
			return this.beanId;
		}

		public String getJndiName() {
			return this.jndiName;
		}

		public String getBusinessInterface() {
			return this.businessInterface;
		}

		public String getResourceDescription() {
			return this.resourceDescription;
		}

		public String toString() {
			// return resourceDescription + newline + beanId + " : " + jndiName + " : " + businessInterface;
			return "\t" + businessInterface + " : " + jndiName + " : " + beanId;
		}

	}

}