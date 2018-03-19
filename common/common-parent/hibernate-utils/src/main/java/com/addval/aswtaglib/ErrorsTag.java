package com.addval.aswtaglib;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;

import com.addval.springstruts.ResourceUtils;

/**
 * Custom tag that renders error messages if an appropriate request attribute has been created. The tag looks for a request attribute with a reserved key, and assumes that it is either a String, a String array, containing message keys to be looked up in the application's MessageResources, or an
 * object of type <code>org.apache.struts.action.ActionErrors</code>.
 * <p>
 * The following optional message keys will be utilized if corresponding messages exist for them in the application resources:
 * <ul>
 * <li><b>errors.header</b> - If present, the corresponding message will be rendered prior to the individual list of error messages.
 * <li><b>errors.footer</b> - If present, the corresponding message will be rendered following the individual list of error messages.
 * <li><b>
 * </ul>
 * 
 */

public class ErrorsTag extends org.apache.struts.taglib.html.ErrorsTag {

	protected ArrayList propertyNames = null;
	protected String sectionName = null;
	protected int errorIndex = 0;
	protected String indexedListProperty = null;
	protected boolean roundedCorner = true;

	public int getErrorIndex() {
		return errorIndex;
	}

	public void setErrorIndex(int errorIndex) {
		this.errorIndex = errorIndex;
	}

	public boolean isRoundedCorner() {
		return roundedCorner;
	}

	public void setRoundedCorner(boolean roundedCorner) {
		this.roundedCorner = roundedCorner;
	}

	public String getIndexedListProperty() {
		return indexedListProperty;
	}

	public void setIndexedListProperty(String indexedListProperty) {
		this.indexedListProperty = indexedListProperty;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setPropertyNames(Object propertyNamesObj) throws JspTagException {
		if (propertyNamesObj != null && propertyNamesObj instanceof ArrayList) {
			propertyNames = (ArrayList) propertyNamesObj;
		}
		else {
			throw new IllegalArgumentException("ErrorsTag: Invalid PropertyNames.");
		}
	}

	public int doStartTag() throws JspException {
		// Were any error messages specified?
		ActionErrors errors = new ActionErrors();
		try {
			Object value = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
			if (value == null) {
				;
			}
			else if (value instanceof String) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError((String) value));
			}
			else if (value instanceof String[]) {
				String keys[] = (String[]) value;
				for (int i = 0; i < keys.length; i++)
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(keys[i]));
			}
			else if (value instanceof ActionErrors) {
				errors = (ActionErrors) value;
			}
			else {
				JspException e = new JspException(messages.getMessage("errorsTag.errors", value.getClass().getName()));
				RequestUtils.saveException(pageContext, e);
				throw e;
			}
		}
		catch (Exception e) {
			;
		}
		if (errors.empty()) {
			return (EVAL_BODY_INCLUDE);
		}

		Iterator reports = null;
		if (propertyNames == null && property == null) {
			reports = errors.get();
		}
		else {
			ArrayList errorReports = new ArrayList();
			if (propertyNames != null) {
				String fieldPropertyName = null;
				for (int i = 0; i < propertyNames.size(); i++) {
					fieldPropertyName = (String) propertyNames.get(i);
					if (indexedListProperty != null) {
						ArrayList indexedPropertyNames = getIndexedPropertyNames(errors, indexedListProperty, fieldPropertyName);
						for (int j = 0; j < indexedPropertyNames.size(); j++) {
							fieldPropertyName = (String) indexedPropertyNames.get(j);
							reports = errors.get(fieldPropertyName);
							while (reports.hasNext()) {
								errorReports.add(reports.next());
							}
						}
						if (indexedPropertyNames.size() == 0) {
							reports = errors.get(fieldPropertyName);
							while (reports.hasNext()) {
								errorReports.add(reports.next());
							}
						}
					}
					else {
						reports = errors.get(fieldPropertyName);
						while (reports.hasNext()) {
							errorReports.add(reports.next());
						}
					}
				}
			}
			if (property != null) {
				reports = errors.get(property);
				while (reports.hasNext()) {
					errorReports.add(reports.next());
				}
			}
			reports = errorReports.iterator();
		}

		StringBuffer results = new StringBuffer();
		String message = null;

		// Set the default values
		String banner = ResourceUtils.message(pageContext, "error.banner", "You must correct the following errors before you may continue.");
		String sectionBanner = ResourceUtils.message(pageContext, "error.sectionbanner", "You must correct the following errors in ");

		/*
		 * if(RequestUtils.present(pageContext, bundle, locale,
		 * "errors.banner")){ banner = RequestUtils.message(pageContext,
		 * bundle,locale, "errors.banner"); }
		 * if(RequestUtils.present(pageContext, bundle, locale,
		 * "errors.sectionBanner")){ sectionBanner =
		 * RequestUtils.message(pageContext, bundle,locale,
		 * "errors.sectionBanner"); }
		 */

		String newline = System.getProperty("line.separator");
		boolean propertyMsgPresent = reports.hasNext();
		if ((message != null) && (propertyNames == null && property == null) || propertyMsgPresent) {
			if (sectionName == null) {
				results.append("<div class=\"columnB\">").append(newline);
				results.append("<div class=\"groupBox\">").append(newline);
				if (isRoundedCorner()) {
					results.append("<DIV class=\"boxTitle1\"><DIV class=\"cornerTopLeft\"></DIV><DIV class=\"cornerTopRight\"></DIV></DIV>").append(newline);
				}
				results.append("<div class=\"outerborder\">").append(newline);
				results.append("<TABLE class=\"messageTable\">").append(newline);
				results.append("<TBODY>").append(newline);
				results.append("<TR id='errorTag" + errorIndex + "'>").append(newline);
				results.append("<TD class=\"icon\" rowSpan=\"3\"><DIV class=\"errorIcon\"></DIV></TD>").append(newline);
				results.append("<TD class=\"errorTitle\" colSpan=\"2\">").append(banner).append("</TD>").append(newline);
				results.append("</TR>").append(newline);
				results.append("<TR>").append(newline);
			}
			else {
				results.append("<TABLE>").append(newline);
				results.append("<TBODY>").append(newline);
				results.append("<TR id='errorTag" + errorIndex + "'>").append(newline);
				results.append("<TD class=\"icon\" rowSpan=\"3\"><DIV class=\"errorIcon\"></DIV></TD>").append(newline);
				results.append("<TD class=\"errorTitle\" colSpan=\"2\">").append(banner).append("</TD>").append(newline);
				results.append("</TR>").append(newline);
				results.append("</TBODY>").append(newline);
				results.append("</TABLE>").append(newline);
				results.append("<TABLE>").append(newline);
				results.append("<TBODY>").append(newline);
				results.append("<TR>").append(newline);

			}
			results.append("<TD>").append(newline);
			results.append("<UL class=\"error\">").append(newline);
		}

		StringBuffer errorsLI = new StringBuffer();
		ActionError report = null;
		while (reports.hasNext()) {
			report = (ActionError) reports.next();
			message = RequestUtils.message(pageContext, bundle, locale, report.getKey(), report.getValues());
			if (message != null && sectionName != null) {
				errorsLI.append("<LI>").append(message).append(" [").append(sectionName).append(" tab]").append("</LI>").append(newline);
			}
			else {
				if (message.contains(".java")) {
					errorsLI = new StringBuffer();
					String criticalError = ResourceUtils.message(pageContext, "error.criticalerror", "Critical System Error. Please contact system administrator.");
					errorsLI.append("<LI>").append(criticalError).append("</LI>").append(newline);
					break;
				}
				errorsLI.append("<LI>").append(message).append("</LI>").append(newline);
			}
		}

		if (errorsLI.length() > 0) {
			results.append(errorsLI);
		}

		if ((message != null) && (propertyNames == null && property == null) || propertyMsgPresent) {
			results.append("</UL>").append(newline);
			results.append("</TD>").append(newline);
			results.append("</TR>").append(newline);
			results.append("</TBODY>").append(newline);

			results.append("</TABLE>").append(newline);
			results.append("</DIV>").append(newline);

			if (sectionName == null) {
				if (isRoundedCorner()) {
					results.append("<DIV class=\"groupBoxFooter\"><DIV class=\"groupBoxFooterLeftImg\"></DIV><DIV class=\"groupBoxFooterRightImg\"></DIV></DIV>").append(newline);
				}
				results.append("</div>").append(newline);
				results.append("</div>").append(newline);
			}
		}
		message = null;

		// Print the results to our output writer
		ResponseUtils.write(pageContext, results.toString());
		// Continue processing this page
		return (EVAL_BODY_INCLUDE);
	}

	public void release() {
		super.release();
		propertyNames = null;
		sectionName = null;
	}

	private ArrayList getIndexedPropertyNames(ActionErrors errors, String indexedListProperty, String fieldPropertyName) {
		ArrayList indexedPropertyNames = new ArrayList();
		String propertyName = null;
		for (Iterator iterator = errors.properties(); iterator.hasNext();) {
			propertyName = (String) iterator.next();
			if (propertyName.startsWith(indexedListProperty + "[") && propertyName.endsWith("]." + fieldPropertyName)) {
				indexedPropertyNames.add(propertyName);
			}
		}
		return indexedPropertyNames;
	}
}
