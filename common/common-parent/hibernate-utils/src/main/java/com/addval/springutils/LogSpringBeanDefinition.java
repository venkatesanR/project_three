package com.addval.springutils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.addval.utils.LogMgr;

public class LogSpringBeanDefinition implements ApplicationListener<ContextRefreshedEvent> {
	private static final transient Logger _logger = LogMgr.getLogger(LogSpringBeanDefinition.class);
	public static String newline = System.getProperty("line.separator");

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (_logger.isDebugEnabled()) {
			if (event.getApplicationContext() instanceof ClassPathXmlApplicationContext) {
				ClassPathXmlApplicationContext ctx = (ClassPathXmlApplicationContext) event.getApplicationContext();
				logBeanNames(ctx, ctx.getBeanFactory());
			}
			else if (event.getApplicationContext() instanceof XmlWebApplicationContext) {
				XmlWebApplicationContext ctx = (XmlWebApplicationContext) event.getApplicationContext();
				logBeanNames(ctx, ctx.getBeanFactory());
			}
		}
	}

	private void logBeanNames(ApplicationContext ctx, ConfigurableListableBeanFactory factory) {
		StringBuffer out = new StringBuffer();
		out.append(newline).append("Bean Count : ").append(ctx.getBeanDefinitionCount());
		String[] beanNames = ctx.getBeanDefinitionNames();
		BeanDefinition beanDefinition = null;
		for (int i = 0; i < beanNames.length; ++i) {
			beanDefinition = factory.getBeanDefinition(beanNames[i]);
			out.append(newline).append(String.format("%3d", (i + 1)) + " : " + String.format("%10s", beanDefinition.getScope()) + " : " + String.format("%75s", beanNames[i]) + " ---> " + beanDefinition.getBeanClassName());
		}
		out.append(newline).append(newline);
		_logger.debug(out.toString());
	}
}
