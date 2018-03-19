package com.addval.springutils;

import java.util.*;

public class ServerRegistry extends Registry {

	public static final String[] SERVER_CONTEXT_CONFIG_LOCATIONS = {"classpath*:beanRefContext.xml","classpath*:beanRefContext*.xml"};
	public static final String DEFAULT_CONTEXT_ID = "productServerContext";

	public ServerRegistry() {
		super(SERVER_CONTEXT_CONFIG_LOCATIONS);
		setBeanFactoryLocatorKey(DEFAULT_CONTEXT_ID);
	}

	public ServerRegistry(String[] myXmlContextLocations) {
		super(myXmlContextLocations);
		setBeanFactoryLocatorKey(DEFAULT_CONTEXT_ID);
	}
}