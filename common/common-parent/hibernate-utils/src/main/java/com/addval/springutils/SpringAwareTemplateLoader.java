package com.addval.springutils;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.addval.utils.LogMgr;

public class SpringAwareTemplateLoader implements ApplicationListener {
	static transient Logger logger = LogMgr.getLogger(SpringAwareTemplateLoader.class);
	private boolean executeScriptsOnAppsStartup = false;
	 
	public boolean isExecuteScriptsOnAppsStartup() {
		return executeScriptsOnAppsStartup;
	}

	public void setExecuteScriptsOnAppsStartup(boolean executeScriptsOnAppsStartup) {
		this.executeScriptsOnAppsStartup = executeScriptsOnAppsStartup;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (!(event instanceof ContextRefreshedEvent)) {
			return;
		}
		if (isExecuteScriptsOnAppsStartup()) {
			ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();
			Map map = ctx.getBeansOfType(LoadTemplates.class, false, false);
			Object[] values = map.values().toArray();
			LoadTemplates loader = null;
			for (int i = 0; i < values.length; ++i) {
				loader = (LoadTemplates) values[i];
				loader.load();
			}
		}
	}
}
