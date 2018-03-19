package com.addval.ui;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.addval.utils.LogMgr;

public class WARVersionUtility {
	private static transient final Logger logger = LogMgr.getLogger(WARVersionUtility.class);

	public HashMap<String, String> getWarVersionProperties() throws Exception {
		HashMap<String, String> versionProperties = new HashMap<String, String>();
		PathMatchingResourcePatternResolver resourcePattern = new PathMatchingResourcePatternResolver();
		Resource resources[] = null;
		Resource resource = null;
		resources = resourcePattern.getResources("classpath*:*-war-version.properties");
		if (resources != null && resources.length > 0) {
			resource = resources[0];
			String version = FileUtils.readFileToString( resource.getFile() ); 
			String keyValues[] = StringUtils.split(version, ",");
			String keyValue[] = null;
			for (int i = 0; i < keyValues.length; i++) {
				keyValue = StringUtils.split(keyValues[i], "=");
				if (keyValue.length == 2) {
					versionProperties.put(keyValue[0], keyValue[1]);
				}
			}
		}
		return versionProperties;
	}
}
