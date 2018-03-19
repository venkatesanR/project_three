package com.addval.springutils;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

public class AVSpringBeanAutowiringInterceptor extends SpringBeanAutowiringInterceptor  {

   private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(AVSpringBeanAutowiringInterceptor.class);

	private ServerRegistry registry = null;

	public AVSpringBeanAutowiringInterceptor(){
		super();

		try {
			registry = new ServerRegistry();
		} catch (Exception e) {
			_logger.error(e);
		}
	}
	protected String getBeanFactoryLocatorKey(Object target) {
		String targetKey = target.getClass().getName();
		String locatorKey =targetKey.substring(0,targetKey.lastIndexOf("."));
		return locatorKey;
	}
	protected BeanFactoryLocator getBeanFactoryLocator( Object target ) {
		return registry.getBeanFactoryLocator();
	}
}
