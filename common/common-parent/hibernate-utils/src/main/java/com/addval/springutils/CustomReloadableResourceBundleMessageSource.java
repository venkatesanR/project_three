package com.addval.springutils;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class CustomReloadableResourceBundleMessageSource  extends ReloadableResourceBundleMessageSource {
	public Properties getAllProperties(Locale locale) {
		clearCacheIncludingAncestors();
		Properties properties = getMergedProperties(locale).getProperties();
		return properties;
	}
}
