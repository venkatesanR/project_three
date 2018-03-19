package com.addval.springutils;

public class ClientRegistry extends Registry {
	public static final String[] CLIENT_CONTEXT_CONFIG_LOCATIONS = {"classpath*:client-beanRefContext.xml","classpath*:client-beanRefContext*.xml"};
	public static final String DEFAULT_CONTEXT_ID = "productClientContext";

	public ClientRegistry() {
		super(CLIENT_CONTEXT_CONFIG_LOCATIONS);
		setBeanFactoryLocatorKey(DEFAULT_CONTEXT_ID);
	}

	public ClientRegistry(String[] myXmlContextLocations) {
		super(myXmlContextLocations);
		setBeanFactoryLocatorKey(DEFAULT_CONTEXT_ID);
	}
}