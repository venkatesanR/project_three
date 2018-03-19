package com.addval.springutils;


import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.BeanFactory;

public class Registry {
	private BeanFactoryLocator _beanFactoryLocator;
	private BeanFactoryReference _beanFactoryReference;
	private String[] _xmlContextLocations;
	private String _locatorKey = null;

	public Registry() {
	}

	public Registry(String[] myXmlContextLocations) {
		_xmlContextLocations = myXmlContextLocations;
		configure();
	}

	public void setBeanFactoryLocatorKey(String locatorKey) {
		_beanFactoryReference = _beanFactoryLocator.useBeanFactory(locatorKey);
		_locatorKey = locatorKey;
	}

	public String getBeanFactoryLocatorKey() {
		return _locatorKey;
	}

	public BeanFactory getBeanFactory() {
			return _beanFactoryReference.getFactory();
	}

	public Object getBean(String beanName) {
		return getBeanFactory().getBean(beanName);
	}

	public BeanFactoryLocator getBeanFactoryLocator() {
			return _beanFactoryLocator;
	}


	protected void configure()
	{
			_beanFactoryLocator = 	Registry.loadConfigXml(_xmlContextLocations[0]);
	}

	protected static synchronized BeanFactoryLocator loadConfigXml(String locations)
	{
			return ContextSingletonBeanFactoryLocator.getInstance(locations);
	}



}
