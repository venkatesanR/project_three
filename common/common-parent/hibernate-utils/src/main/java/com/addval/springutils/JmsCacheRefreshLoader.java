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

import com.addval.utils.CacheInitializer;
import com.addval.utils.CacheMgr;
import com.addval.utils.CacheRefreshDetail;
import com.addval.utils.StrUtl;

import java.util.Map;
import java.util.StringTokenizer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * A message listener that can be used to refresh the caches, by sending a message to a topic
 */
public class JmsCacheRefreshLoader implements MessageListener {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(JmsCacheRefreshLoader.class);
	private final String _delimiter = ",";
	CacheMgr _cacheMgr = null;

	public JmsCacheRefreshLoader() {
	}

	public CacheMgr getCacheManager() {
		return _cacheMgr;
	}

	public void setCacheManager(CacheMgr cacheMgr) {
		_cacheMgr = cacheMgr;
	}

	/**
	 * @param message
	 * @roseuid 3EA9DF8202B0
	 */
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				_logger.info("Object message format reached - " + message);
				CacheRefreshDetail detail = (CacheRefreshDetail) ((ObjectMessage) message).getObject();
				if(detail != null){
					refreshCache(detail);
				}
				else {
					_logger.error( "CacheRefreshDetail is Null");
				}
			}
			else if (message instanceof TextMessage) {
				TextMessage txtmessage = (TextMessage) message;
				String line = txtmessage.getText().trim();
				StringTokenizer st = new StringTokenizer(line, _delimiter);
				String cacheName = st.nextToken();
				String objName = st.nextToken();
				if (cacheName != null && objName != null) {
					resetCache(cacheName, objName);
				}
				else {
					_logger.warn("Unknown cache refresh message: " + line);
				}
			}
			else {
				_logger.warn("Wrong message format reached - " + message);
			}
		}
		catch (Exception e) {
			_logger.error(e);
		}
		finally {
			try {
				message.acknowledge();
			}
			catch (Exception e) {
				_logger.warn(e);
			}
		}
	}

	private void refreshCache(CacheRefreshDetail detail) {
		try {
			Map<String, CacheInitializer> initializers = getCacheManager().getCacheInitializers();
			if (initializers == null) {
				return;
			}
			if(StrUtl.isEmptyTrimmed(detail.getObjectName())){
				_logger.error( "Cache object is null");
				return;
			}
			CacheInitializer init = (CacheInitializer) initializers.get(detail.getObjectName());
			if (init != null) {
				getCacheManager().refresh(detail);
			}
		}
		catch (Exception e) {
			// Ignoring the error
			_logger.error(e);
		}
	}

	private void resetCache(String cacheName, String objName) {
		try {
			Map<String, CacheInitializer> initializers = getCacheManager().getCacheInitializers();
			if (initializers == null) {
				return;
			}
			if(StrUtl.isEmptyTrimmed(objName)){
				_logger.error( "Cache object is null");
				return;
			}			
			CacheInitializer init = (CacheInitializer) initializers.get(objName);
			if (init != null) {
				getCacheManager().refresh(init.getObjectName());
			}
		}
		catch (Exception e) {
			// Ignoring the error
			_logger.error(e);
		}
	}
}
