package com.addval.springutils;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.addval.utils.AVConstants;
import com.addval.utils.LogMgr;
import com.addval.utils.StrUtl;

public class SpringAwareDBScriptsLoader implements ApplicationListener {
	static transient Logger logger = LogMgr.getLogger(SpringAwareDBScriptsLoader.class);
	private boolean executeScriptsOnAppsStartup = false;
	private boolean executeSqliteScriptsOnAppsStartup = true;
	private String configuredServerType;
		
	public String getConfiguredServerType() {
		return configuredServerType;
	}

	public void setConfiguredServerType(String configuredServerType) {
		this.configuredServerType = configuredServerType;
	}

	public boolean isExecuteSqliteScriptsOnAppsStartup() {
		return executeSqliteScriptsOnAppsStartup;
	}

	public void setExecuteSqliteScriptsOnAppsStartup(boolean executeSqliteScriptsOnAppsStartup) {
		this.executeSqliteScriptsOnAppsStartup = executeSqliteScriptsOnAppsStartup;
	}

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
		ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();
		Map map = ctx.getBeansOfType(LoadDBScripts.class, false, false);
		Object[] values = map.values().toArray();
		LoadDBScripts loader = null;
		String serverType = null;					
		for (int i = 0; i < values.length; ++i) {
			loader = (LoadDBScripts) values[i];
			serverType = loader.getServerType();
			if (executeScriptsOnAppsStartup && StrUtl.equalsIgnoreCase(configuredServerType, serverType)) {
				loader.executeScripts();
			}
			else if (executeSqliteScriptsOnAppsStartup && StrUtl.equalsIgnoreCase(configuredServerType, serverType)){				
					loader.executeScripts();								
			}
		}
	}
}
