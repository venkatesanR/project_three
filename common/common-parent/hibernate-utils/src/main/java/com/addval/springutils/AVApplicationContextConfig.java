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
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.addval.utils.LogMgr;

/**
 * Some notes about this class, and the conventions for its use within Addval standards
 *
 * "classpath*:" versus "classpath:" "classpath:" will search the class path for the specified file-name-pattern, and will STOP when it finds the first matching file-name. "classpath*:" will search the class path, and will return ALL matching file-names.
 *
 * (See http://www.carbonfive.com/community/archives/2007/05/using_classpath.html for more info.)
 *
 * Use of "*" within file-name: Use of "*" and "?" wild-cards within paths and file-names is supported by spring, via org.springframework.util.AntPathMatcher. If the file MUST exist (by Addval standards), then no "*" is embedded within file name. If the file is not found, spring will throw an
 * exception. If the file is OPTIONAL, then a "*" has been put somewhere in the file-name. This is done ONLY to avoid the spring exception if the file does not exist.
 *
 * Usage of test-appplicationContext.xml file. During mvn test phase, there will never be more than one test-appplicationContext.xml used. If it is a client test, it will be the file in <myModule>/target/test-classes/client. If it is a server test, it will be the file in
 * <myModule>/target/test-classes/server. The ONLY test-applicationContext.xml file that can be found on the CLASSPATH is the one for the module being tested under maven. (That is because no /test programs or /test/resource files are EVER loaded into a .jar by maven.)
 */
public class AVApplicationContextConfig {
	private transient Logger logger = null;
	String _contextType = "server"; // can be server or client.
	String _scope = "product"; // can be product or module
	String _serverMode = "standalone"; // can be container or standalone or standalonedebug
	private static String envServerMode = null; // Get servermode from system property.
	int _serverCount = 0;
	String _pathPrefix = null;

	public AVApplicationContextConfig() {
		try {
			if (!LogMgr.isConfigured()) {
				debug("Initialize log4j using LogMgr.doConfigure()");
				ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath*:springLog4j.xml" });
			}
			logger = LogMgr.getLogger(AVApplicationContextConfig.class);
		}
		catch (Exception ex) {
			String errMsg = "AVApplicationContextConfig(): initialize log4j";
			debug(ex.getMessage());
		}
	}

	public AVApplicationContextConfig(String contextType, int serverCount) {
		setContextType(contextType);
		setServerCount(serverCount);
	}

	public String getContextType() {
		return _contextType;
	}

	public void setContextType(String aContextType) {
		_contextType = aContextType;
	}

	public String getScope() {
		return _scope;
	}

	public void setScope(String aScope) {
		_scope = aScope;
	}

	public int getServerCount() {
		return _serverCount;
	}

	public void setServerCount(int aCount) {
		_serverCount = aCount;
	}

	public String getServerMode() {
		return _serverMode;
	}

	public void setServerMode(String aMode) {
		if(envServerMode == null){
			envServerMode = aMode;
		}
		_serverMode = aMode;
	}

	public void setPathPrefix(String aPrefix) {
		_pathPrefix = aPrefix;
	}

	public String getPathPrefix() {
		return _pathPrefix;
	}

	public void setConfigLocations(ArrayList configLocation) {
	}

	public ArrayList getConfigLocations() {
		if (getPathPrefix() == null) {
			String errMsg = "AVApplicationContextConfig.getConfigLocations(): pathPrefix has not been specified; this is normal ONLY for '-parent' module.";
			debug(errMsg);
		}
		ArrayList list = new ArrayList();
		boolean debugMode = getEnvServerMode() != null && getEnvServerMode().indexOf("debug") != -1;
		if (getScope().equals("product")) {
			list.add("classpath*:" + getContextType() + "/main-" + getContextType() + "-applicationContext.xml");
			list.add("classpath*:" + getContextType() + "/springrest-applicationContext.xml");
			if (getContextType().equals("server")) {
				if(!debugMode){
					list.add("classpath*:" + getContextType() + "/serverInternal-applicationContext.xml");
					list.add("classpath*:" + getContextType() + "/project-serverInternal-applicationContext*.xml");
				}
				if (getServerMode().indexOf("standalone") != -1) {
					list.add("classpath:" + getContextType() + "/standalone-access-applicationContext.xml");
					list.add("classpath*:" + getContextType() + "/project-standalone-access-applicationContext*.xml");
				}
				else {
					list.add("classpath:" + getContextType() + "/container-access-applicationContext.xml");
					list.add("classpath*:" + getContextType() + "/project-container-access-applicationContext*.xml");
				}

				if (getServerMode().indexOf("debug") != -1) {
					list.add("classpath*:" + "com/addval/**/impl/applicationContext.xml");
					list.add("classpath*:" + "com_addval_*_impl_project-applicationContext.xml");
					list.add("classpath*:" + getContextType() + "/test*applicationContext.xml");
				}
			}
			else if (getContextType().equals("client")) {
				list.add("classpath*:" + "com/addval/**/client/applicationContext.xml");
				list.add("classpath*:" + "com_addval_*_client_project-applicationContext.xml");
			}

			// search in additional files for project specific overrides in case it is present
			list.add("classpath*:" + getContextType() + "/project-" + getContextType() + "-applicationContext*.xml");
		}
		else if (getScope().equals("module")) {
			String pathPrefix = getPathPrefix().replace('.', '/');
			String projectPathPrefix = pathPrefix.replace('/', '_');
			list.add("classpath:" + pathPrefix + "/applicationContext.xml");
			/* In module level AVApplicationContextConfig we are not setting ServerMode : beanRefContext.xml */
			if (debugMode) {
				list.add("classpath*:" + getContextType() + "/test*applicationContext.xml"); 
			}
			if (!debugMode && getContextType().equals("server")) {
				list.add("classpath*:" + pathPrefix + "/serverInternal*applicationContext.xml");
				// added for WebSphere 6.1 - Kalidasan - Start
				list.add("classpath*:" + pathPrefix + "/serverInternal-applicationContext.xml");
				// added for WebSphere 6.1 - Kalidasan - End
				list.add("classpath*:" + projectPathPrefix + "_project-serverInternal*applicationContext.xml");
			}

			// search in additional files for project specific overrides in case it is present
			list.add("classpath*:" + projectPathPrefix + "_project-applicationContext*.xml");
		}
		else {
			String errMsg = "AVApplicationContextConfig.getConfigLocations(): unrecognized value, scope=" + getScope() + "; must be 'product' or 'module'";
			debug(errMsg);
			throw new RuntimeException(errMsg);
		}

		debug("getConfigLocations for module with pathPrefix=" + getPathPrefix() + ", contextType=" + getContextType() + ", serverMode=" + getServerMode() + ", scope=" + getScope() + ", serverCount=" + getServerCount() + ": config locations=" + list);
		return list;
	}

	private void debug(String msg) {
		if (logger != null) {
			logger.debug(msg);
		}
		else {
			System.out.println(msg);
		}
	}
	private String getEnvServerMode(){
		if(envServerMode == null && getServerMode().equalsIgnoreCase("standalone")){
			Properties p = System.getProperties();
			Enumeration keys = p.keys();
			String sysPropName = null;
			while (keys.hasMoreElements()) {
				sysPropName = (String)keys.nextElement();
				if(sysPropName.indexOf("_server_mode") != -1){ //"cargores_server_mode or cargoops_server_mode or ....
					envServerMode = (String)p.get(sysPropName);
					break;
				}
			}
		}
		return envServerMode;
	}
}
