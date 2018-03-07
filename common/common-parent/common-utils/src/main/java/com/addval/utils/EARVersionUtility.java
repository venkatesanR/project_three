package com.addval.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class EARVersionUtility {
	private static transient final Logger logger = LogMgr.getLogger(EARVersionUtility.class);

	public HashMap<String, String> getEarVersionProperties() throws Exception {
		HashMap<String, String> versionProperties = new HashMap<String, String>();
		PathMatchingResourcePatternResolver resourcePattern = new PathMatchingResourcePatternResolver();
		Resource resources[] = null;
		Resource resource = null;
		resources = resourcePattern.getResources("classpath*:*-ear-version.properties");
		if (resources != null && resources.length > 0) {
			resource = resources[0];
			String version = convertStreamToString(resource.getInputStream());
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

	public String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		String trimmedLine = null;
		boolean comments = false;
		int pos = -1;
		while ((line = reader.readLine()) != null) {
			trimmedLine = StringUtils.trim(line);
			pos = trimmedLine.indexOf("/*");
			if (pos != -1) {
				comments = true;
			}
			if (comments) {
				pos = trimmedLine.indexOf("*/");
				if (pos != -1) {
					line = trimmedLine.substring(pos + "*/".length());
					comments = false;
				}
				else {
					continue;
				}
			}
			trimmedLine = StringUtils.trim(line);
			if (!(trimmedLine.startsWith("--") || trimmedLine.toUpperCase().startsWith("SET DEFINE OFF;") || trimmedLine.toUpperCase().startsWith("COMMIT;") || trimmedLine.startsWith("/"))) {
				// Remove SQL Comments
				sb.append(line);
			}
		}
		String sqlContent = sb.toString();
		sqlContent = StringUtils.replace(sqlContent, System.getProperty("line.separator"), " ");
		is.close();
		return sqlContent;
	}

}
