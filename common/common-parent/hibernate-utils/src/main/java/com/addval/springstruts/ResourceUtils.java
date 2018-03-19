package com.addval.springstruts;

import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.RequestUtils;

import com.addval.environment.Environment;

public class ResourceUtils {
	public static String labelSuffix = "-label";
	public static String errorMessageSuffix = "-errmsg";
	public static String titleSuffix = "-title";
	public static String menuPrefix = "menu-";
	private static String bundleName = "ApplicationResources";
	private static Properties serverSideErrors = null;
	private static MessageResources resources = null;
	private static final transient Logger _logger = com.addval.utils.LogMgr.getLogger(ResourceUtils.class);

	/*
	 * This method loads the server side error properties and finds the entry that match with the message.
	 * Then the key for the message is returned from the entry object.
	 * If there is no matching entry found then it logs it as Unhandled processing remark
	 */
	private static String handleServerSideErrors(java.lang.String message) {
		if (serverSideErrors == null) {
			serverSideErrors = Environment.getInstance("serverside_errors").getCnfgFileMgr().getProperties();
		}
		if (serverSideErrors != null && serverSideErrors.containsValue(message)) {
			for (Entry<Object, Object> entry : serverSideErrors.entrySet()) {
				if (entry.getValue().toString().equals(message)) {
					return entry.getKey().toString();
				}
			}
		}
		else {
			_logger.warn("Unhandleed server side errors : " + message);
		}
		return null;
	}

	public static String message(javax.servlet.jsp.PageContext pageContext, java.lang.String key, java.lang.String defaultValue) throws JspException {
		boolean isPresent = false;
		String returnValue = defaultValue;

		isPresent = RequestUtils.present(pageContext, Globals.MESSAGES_KEY, Globals.LOCALE_KEY, key);

		if (isPresent) {
			returnValue = RequestUtils.message(pageContext, Globals.MESSAGES_KEY, Globals.LOCALE_KEY, key);
		}

		return returnValue;
	}

	public static String message(javax.servlet.jsp.PageContext pageContext, java.lang.String key, java.lang.Object[] args, java.lang.String defaultValue) throws JspException {
		boolean isPresent = false;
		String returnValue = defaultValue;

		isPresent = RequestUtils.present(pageContext, Globals.MESSAGES_KEY, Globals.LOCALE_KEY, key);

		if (isPresent) {
			returnValue = RequestUtils.message(pageContext, Globals.MESSAGES_KEY, Globals.LOCALE_KEY, key, args);
		}

		return returnValue;
	}

	public static String columnCaption(javax.servlet.jsp.PageContext pageContext, java.lang.String key, java.lang.String defaultValue) throws JspException {
		String lookupKey = key + labelSuffix;
		return message(pageContext, lookupKey, defaultValue);
	}

	public static String columnCaption(javax.servlet.http.HttpServletRequest request, java.lang.String key, java.lang.String defaultValue) throws JspException {
		String lookupKey = key + labelSuffix;
		return message(request, lookupKey, defaultValue);
	}

	public static String editorTitle(javax.servlet.jsp.PageContext pageContext, java.lang.String key, java.lang.String defaultValue) throws JspException {
		String lookupKey = key + titleSuffix;
		return message(pageContext, lookupKey, defaultValue);
	}

	public static String editorTitle(javax.servlet.http.HttpServletRequest request, java.lang.String key, java.lang.String defaultValue) throws JspException {
		String lookupKey = key + titleSuffix;
		return message(request, lookupKey, defaultValue);
	}

	public static String menuLabel(javax.servlet.http.HttpServletRequest request, java.lang.String key, java.lang.String defaultValue) {
		String menuLabelKey = menuPrefix + key;
		return message(request, menuLabelKey, defaultValue);
	}

	public static String columnErrorMessage(javax.servlet.jsp.PageContext pageContext, java.lang.String key, java.lang.String defaultValue) throws JspException {
		String lookupKey = key + errorMessageSuffix;
		return message(pageContext, lookupKey, defaultValue);
	}

	public static String message(javax.servlet.http.HttpServletRequest request, java.lang.String key, java.lang.String defaultValue) {
		boolean isPresent = false;
		String returnValue = defaultValue;
		MessageResources resources = getMessageResources();
		HttpSession currSession = request.getSession(false);
		Locale locale = (Locale) currSession.getAttribute(Globals.LOCALE_KEY);

		/* This condition is to handle the validation/error messages set as processing remark in the XML communication between products.
		 * Only in this scenario both the key and the default value will be set with same value. 
		 */
		if (key.equals(defaultValue)) {
			String serverErrorKey = handleServerSideErrors(key);
			if (serverErrorKey != null) {
				key = serverErrorKey;
			}
		}

		if (locale == null) {
			locale = request.getLocale();
		}

		if (resources != null && locale != null) {
			isPresent = resources.isPresent(locale, key);
			if (isPresent) {
				returnValue = resources.getMessage(locale, key);
			}
		}

		return returnValue;
	}

	public static String message(javax.servlet.http.HttpServletRequest request, java.lang.String key, java.lang.String defaultValue, java.lang.String arg1) {
		boolean isPresent = false;
		String returnValue = defaultValue;

		MessageResources resources = getMessageResources();
		HttpSession currSession = request.getSession(false);
		Locale locale = (Locale) currSession.getAttribute(Globals.LOCALE_KEY);
		if (locale == null) {
			locale = request.getLocale();
		}

		if (resources != null && locale != null) {
			isPresent = resources.isPresent(locale, key);
			if (isPresent) {
				returnValue = resources.getMessage(locale, key, arg1);
			}
		}
		return returnValue;
	}

	public static String message(javax.servlet.http.HttpServletRequest request, java.lang.String key, java.lang.String defaultValue, java.lang.String arg1, java.lang.String arg2) {
		boolean isPresent = false;
		String returnValue = defaultValue;

		MessageResources resources = getMessageResources();
		HttpSession currSession = request.getSession(false);
		Locale locale = (Locale) currSession.getAttribute(Globals.LOCALE_KEY);
		if (locale == null) {
			locale = request.getLocale();
		}

		if (resources != null && locale != null) {
			isPresent = resources.isPresent(locale, key);
			if (isPresent) {
				returnValue = resources.getMessage(locale, key, arg1, arg2);
			}
		}
		return returnValue;
	}

	private static MessageResources getMessageResources() {
		if (resources == null) {
			resources = MessageResources.getMessageResources(bundleName);
		}
		return resources;
	}

}
