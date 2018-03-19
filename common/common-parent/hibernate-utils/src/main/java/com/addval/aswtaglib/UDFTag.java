package com.addval.aswtaglib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import com.addval.springstruts.ResourceUtils;
import com.addval.udf.api.UdfApplicationUsageException;
import com.addval.udfutils.UdfDomainUtils;
import com.addval.utils.StrUtl;
import com.addval.utils.udf.xmlschema.x2011.XField;
import com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinitionDocument;
import com.addval.utils.udf.xmlschema.x2011.XUdfFieldDefinition;
import com.addval.utils.udf.xmlschema.x2011.XUdfFieldGroup;
import com.addval.utils.udf.xmlschema.x2011.XUdfUIComponentMetadata;

public class UDFTag extends GenericBodyTag {
	public static String newline = System.getProperty("line.separator");
	private String groupName = null;
	private String groupType = null;
	private List<XField> fields = null;
	private boolean readOnly = false;
	private boolean angularJS = false;
	private Map<String, String> udfValues = null;
	private String formName = null;
	private XUdfDomainDefinitionDocument metadata = null;
	private UdfDomainUtils udfDomainUtils = null;

	public UDFTag() {

	}

	public void setGroupname(String groupName) {
		this.groupName = groupName;
	}

	public void setGrouptype(String groupType) {
		this.groupType = groupType;
	}

	public void setFields(List<XField> fields) {
		this.fields = fields;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isAngularJS() {
		return angularJS;
	}

	public void setAngularJS(boolean angularJS) {
		this.angularJS = angularJS;
	}

	public Map<String, String> getUdfValues() {
		return udfValues;
	}

	public void setUdfValues(Map<String, String> udfValues) {
		this.udfValues = udfValues;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setMetadata(XUdfDomainDefinitionDocument metadata) {
		this.metadata = metadata;
	}

	public UdfDomainUtils getUdfDomainUtils() throws UdfApplicationUsageException {
		if (udfDomainUtils == null && this.metadata != null) {
			udfDomainUtils = new UdfDomainUtils(this.metadata, this.groupType);
		}
		return udfDomainUtils;
	}

	public int doStartTag() throws JspTagException {
		HashMap<String, StringBuffer> htmlMap = new HashMap<String, StringBuffer>();
		HashMap<String, Integer> colsPerRowMap = new HashMap<String, Integer>();
		Hashtable<String, Integer> colsPerRowMapCopy = new Hashtable<String, Integer>();

		StringBuffer htmlHidden = new StringBuffer();
		StringBuffer htmlError = new StringBuffer();
		StringBuffer html = null;
		try {
			List<XUdfFieldGroup> udfFieldGroups = getUdfDomainUtils().matchFieldGroup(this.fields);
			String group = "";
			Integer colsPerRow = null;
			Integer colsPerRowCopy = null;

			for (XUdfFieldGroup udfFieldGroup : udfFieldGroups) {
				group = udfFieldGroup.getName();
				colsPerRow = udfFieldGroup.getUiColsPerRow();
				htmlMap.put(group, new StringBuffer());
				colsPerRowMap.put(group, Integer.valueOf(colsPerRow));
				colsPerRowMapCopy.put(group, Integer.valueOf(colsPerRow));
			}

			List<XUdfFieldDefinition> udfFieldDefinitions = getUdfDomainUtils().matchFieldDefinition(this.fields);
			List<String> newFields = getNewFields(this.fields, udfFieldDefinitions);

			// System.out.println(newline + "Check List...." + newline);
			XUdfUIComponentMetadata uiCmd = null;
			String fieldType = null;
			String fieldLabel = null;
			String fieldName = null;
			String fieldValue = null;
			String fieldDescValue = null;
			String fieldFormat = null;
			String fieldWidth = null;
			String fieldValidate = null;
			String optionValue = null;
			String optionLabel = null;
			String optionSelected = null;

			String dropdownValues = null;
			String dataDelimeter = null;
			String dropdownClassname = null;
			String lookupLink = null;
			String dependantFields = null;

			boolean mandatory = false;
			boolean isReadOnly = false;
			String onChangeFn = null;
			String ajaxLoaderId = null;
			String newFieldCss = null;
			String id = null;
			String dataModel = "UDF_" + this.groupName;

			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
				uiCmd = udfFieldDefinition.getUIComponentMetadata();
				if (uiCmd != null) {
					fieldName = "UDF_" + this.groupName + "_" + udfFieldDefinition.getType() + "_" + udfFieldDefinition.getName();
					fieldLabel = uiCmd.getAttributeLabel();
					fieldLabel = ResourceUtils.message(pageContext, fieldLabel, fieldLabel);

					fieldValue = escapeHtml(uiCmd.getAttributeDefaultValue());
					newFieldCss = newFields.contains(udfFieldDefinition.getName()) ? "newudf" : "";
					// System.out.println(udfFieldDefinition.getType() + " : " + udfFieldDefinition.getName() + " : " + fieldValue);

					if (udfFieldDefinition.getIsFieldGroupTrigger()) {
						if (isAngularJS()) {
							getUdfValues().put(fieldName, fieldValue);
						}
						else {
							// HIDDEN
							htmlHidden.append("<INPUT type=\"hidden\" name=\"" + fieldName + "\" value=\"" + fieldValue + "\"/>");
						}
					}
					else {
						// (TEXT|LONGTEXT|BOOLEAN|INTEGER|DOUBLE|DATE|OPTION|ENUM)
						fieldType = uiCmd.getAttributeType();
						fieldFormat = uiCmd.getAttributeFormat();
						fieldWidth = uiCmd.getAttributeWidth();
						fieldValidate = uiCmd.getValidate();
						dropdownValues = uiCmd.getEnumDropdownValuesList();
						dataDelimeter = uiCmd.getAttributeDataDelimeter();
						dropdownClassname = uiCmd.getEnumDropdownClassname();
						lookupLink = uiCmd.getAttributeLookupLink();
						dependantFields = udfFieldDefinition.getDependantFields();
						mandatory = (fieldValidate != null && fieldValidate.toLowerCase().indexOf("required") != -1);
						isReadOnly = uiCmd.getIsReadOnly();
						onChangeFn = !StrUtl.isEmptyTrimmed(dependantFields) ? "clearDependantFields(this,'" + dependantFields + "');refreshUDF(this);" : "";
						group = udfFieldDefinition.getGroup();

						html = htmlMap.get(group);
						html.append("<td><table><tr>");
						html.append("<td><p>");
						if (mandatory) {
							html.append("<span class=\"required\">*</span>");
						}
						html.append(fieldLabel);
						html.append("</p></td></tr>");
						html.append("<tr><td class=\"content\">");
						if (isReadOnly || this.readOnly) {
							fieldDescValue = fieldValue;
							if (!StrUtl.isEmptyTrimmed(fieldValue)) {
								if ("BOOLEAN".equalsIgnoreCase(fieldType)) {
									fieldDescValue = Boolean.valueOf(fieldValue) ? "Yes" : "No";
								}
							}
							html.append(fieldDescValue);
							if (isAngularJS()) {
								getUdfValues().put(fieldName, fieldValue);
							}
							else {
								html.append("<INPUT type=\"hidden\" name=\"" + fieldName + "\" value=\"" + fieldValue + "\">");
							}
						}
						else if ("TEXT".equalsIgnoreCase(fieldType) || "INTEGER".equalsIgnoreCase(fieldType) || "DOUBLE".equalsIgnoreCase(fieldType) || "DATE".equalsIgnoreCase(fieldType)) {
							html.append("<input ");
							html.append(" type =\"text\" ");
							html.append(" name =\"").append(fieldName).append("\"");
							html.append(" value =\"").append(fieldValue).append("\"");
							html.append(" id =\"").append(fieldName).append("\"");
							if (!StrUtl.isEmptyTrimmed(fieldWidth)) {
								String width[] = StringUtils.split(fieldWidth, ",");
								if (width.length > 0) {
									html.append(" size =\"").append(width[0]).append("\"");
								}
								if (width.length > 1) {
									if (isAngularJS()) {
										if (!"DATE".equalsIgnoreCase(fieldType)) {
											html.append(" maxlength =\"").append(width[1]).append("\"");
										}
									}
									else {
										html.append(" maxlength =\"").append(width[1]).append("\"");
									}
								}
							}
							if (!StrUtl.isEmptyTrimmed(onChangeFn)) {
								if (isAngularJS()) {
									html.append(" ng-change =\"" + onChangeFn + "\"");
								}
								else {
									html.append(" onchange =\"" + onChangeFn + "\"");
								}
							}
							if (!StrUtl.isEmptyTrimmed(newFieldCss)) {
								html.append(" class =\"" + newFieldCss + "\"");
							}

							if ("DATE".equalsIgnoreCase(fieldType)) {
								if (isAngularJS()) {
									html.append(" placeholder=\"{{calendar.dateFormat}}\"");
									html.append(" datepicker-popup=\"{{calendar.dateFormat}}\"");
									html.append(" is-open=\"calendar.opened." + fieldName + "\"");
									html.append(" datepicker-options=\"calendar.dateOptions\"");
								}
								else {
									fieldFormat = !StrUtl.isEmptyTrimmed(fieldFormat) ? fieldFormat : "dd-MMM-yyyy";
									html.append(" onBlur=\"isValidDate(this,'" + fieldFormat + "' )\"");
									html.append("Class=\"WaterMark\"");
									html.append(" WaterMarkText=\"").append(fieldFormat).append("\"");
								}
							}

							if (isAngularJS()) {
								html.append(" ng-model =\"" + dataModel + "." + fieldName + "\"");
								getUdfValues().put(fieldName, fieldValue);
							}

							html.append("/>");

							if ("DATE".equalsIgnoreCase(fieldType) && isAngularJS()) {
								html.append("<a ng-click=\"calendar.open($event,'" + fieldName + "')\"><span class=\"glyphicon glyphicon-calendar\"/></a>");
								html.append("<div class=\"validation-error\" ng-show=\"" + getFormName() + "." + fieldName + ".$invalid && " + getFormName() + "." + fieldName + ".$dirty\">");
								html.append("<div ng-messages=\"" + getFormName() + "." + fieldName + ".$error\" ng-messages-include=\"aswcustommessages.html\" asw-msgs></div>");
								html.append("</div>");
							}
						}
						else if ("BOOLEAN".equalsIgnoreCase(fieldType)) {
							html.append("<input ");
							html.append(" type =\"checkbox\" ");
							fieldValue = StrUtl.isEmptyTrimmed(fieldValue) ? "false" : fieldValue;
							html.append(" name =\"").append(fieldName).append("\"");
							html.append(" value =\"").append(fieldValue).append("\"");

							if (Boolean.valueOf(fieldValue)) {
								html.append(" checked ");
							}

							html.append(" onclick =\"this.value=this.checked\" ");

							if (!StrUtl.isEmptyTrimmed(onChangeFn)) {
								html.append(" onChange =\"" + onChangeFn + "\"");
							}
							if (!StrUtl.isEmptyTrimmed(newFieldCss)) {
								html.append(" class =\"" + newFieldCss + "\"");
							}

							html.append("/>");
						}
						else if ("LONGTEXT".equalsIgnoreCase(fieldType)) {
							html.append("<TEXTAREA ");
							html.append(" name =\"").append(fieldName).append("\"");
							if (!StrUtl.isEmptyTrimmed(fieldWidth)) {
								String width[] = StringUtils.split(fieldWidth, ",");
								if (width.length > 0) {
									html.append(" rows =\"").append(width[0]).append("\"");
								}
								if (width.length > 1) {
									html.append(" cols =\"").append(width[1]).append("\"");
								}
							}
							if (!StrUtl.isEmptyTrimmed(onChangeFn)) {
								html.append(" onChange =\"" + onChangeFn + "\"");
							}
							if (!StrUtl.isEmptyTrimmed(newFieldCss)) {
								html.append(" class =\"" + newFieldCss + "\"");
							}

							html.append(">");
							html.append(fieldValue);
							html.append("</TEXTAREA>");
						}
						else if ("OPTION".equalsIgnoreCase(fieldType)) {
							// TODO
						}
						else if ("ENUM".equalsIgnoreCase(fieldType)) {
							html.append("<SELECT ");
							html.append(" name =\"").append(fieldName).append("\"");
							if (!StrUtl.isEmptyTrimmed(onChangeFn)) {
								html.append(" onChange =\"" + onChangeFn + "\"");
							}
							if (!StrUtl.isEmptyTrimmed(newFieldCss)) {
								html.append(" class =\"" + newFieldCss + "\"");
							}

							html.append(">");

							html.append("<OPTION VALUE=''> </OPTION>");

							if (!StrUtl.isEmptyTrimmed(dropdownClassname)) {
								try {
									Class<?> enumObj = Class.forName(dropdownClassname);
									List optionLabelValues = Arrays.asList(enumObj.getEnumConstants());
									Enum optionLabelValue = null;
									for (int i = 0; i < optionLabelValues.size(); i++) {
										optionLabelValue = (Enum) optionLabelValues.get(i);
										optionLabel = optionLabelValue.name();
										optionValue = optionLabelValue.name();
										optionSelected = fieldValue.equalsIgnoreCase(optionValue) ? " selected " : "";

										html.append("<OPTION VALUE=\"" + optionValue + "\"" + optionSelected + ">");
										html.append(optionLabel);
										html.append("</OPTION>");
									}
								}
								catch (ClassNotFoundException x) {
									x.printStackTrace();
								}
							}
							if (!StrUtl.isEmptyTrimmed(dropdownValues)) {
								String optionLabelValues[] = StringUtils.split(dropdownValues, dataDelimeter);
								for (String optionLabelValueStr : optionLabelValues) {
									String optionLabelValue[] = StringUtils.split(optionLabelValueStr, "=");
									optionLabel = (optionLabelValue.length > 1) ? optionLabelValue[1] : optionLabelValue[0];
									optionValue = optionLabelValue[0];
									optionSelected = fieldValue.equalsIgnoreCase(optionValue) ? " selected " : "";

									html.append("<OPTION VALUE=\"" + optionValue + "\"" + optionSelected + ">");
									html.append(optionLabel);
									html.append("</OPTION>");
								}
							}
							html.append("</SELECT>");
						}
						if (!StrUtl.isEmptyTrimmed(onChangeFn) && !isAngularJS()) {
							ajaxLoaderId = fieldName + "_LOADER";
							html.append("<IMG SRC=\"../images/ajax-loader.gif\" WIDTH=\"20\" HEIGHT=\"20\" style=\"visibility:hidden;\" id=\"" + ajaxLoaderId + "\"/>");
						}

						if (!StrUtl.isEmptyTrimmed(lookupLink) && !this.readOnly) {
							html.append(updateLookupLink(lookupLink, udfFieldDefinitions, udfFieldDefinition));
						}

						html.append("</td></tr></table></td>");

						colsPerRow = colsPerRowMap.get(group);
						colsPerRowCopy = colsPerRowMapCopy.get(group);
						colsPerRow--;
						if (colsPerRow.equals(new Integer(0))) {
							colsPerRow = colsPerRowCopy;
							html.append("</tr><tr>");
						}
						colsPerRowMap.put(group, colsPerRow);
					}
				}
			}

			Object actionErrors = pageContext.getAttribute(Globals.ERROR_KEY, PageContext.REQUEST_SCOPE);
			// ActionErrors actionErrors = null;
			if (actionErrors != null && actionErrors instanceof ActionErrors) {
				ActionErrors errors = (ActionErrors) actionErrors;
				Iterator reports = errors.get(this.groupName);
				ActionError actionError = null;
				while (reports.hasNext()) {
					actionError = (ActionError) reports.next();
					// System.out.println(actionError.getKey() +" = " + actionError.getValues()[0]);
					htmlError.append("<LI class='errorTextStd'>");
					htmlError.append(actionError.getValues()[0]);
					htmlError.append("</LI>");
				}

				if (htmlError.toString().length() > 0) {
					htmlError.insert(0, "<TABLE><TR><TD>");
					htmlError.append("</TD></TR></TABLE>");
				}
			}
			StringBuffer pageHtml = new StringBuffer();
			if (htmlError.toString().length() > 0) {
				pageHtml.append(htmlError).append(newline);
			}
			if (htmlHidden.toString().length() > 0) {
				pageHtml.append(htmlHidden).append(newline);
			}
			for (XUdfFieldGroup udfFieldGroup : udfFieldGroups) {
				group = udfFieldGroup.getName();
				html = htmlMap.get(group);
				colsPerRow = colsPerRowMap.get(group);
				colsPerRowCopy = colsPerRowMapCopy.get(group);
				if (html.toString().length() > 0) {
					pageHtml.append("<fieldset title=\"").append(ResourceUtils.message(pageContext, udfFieldGroup.getDescription(), udfFieldGroup.getDescription())).append("\" class=\"udffieldset\">");
					pageHtml.append("<legend class=\"udffieldsetlegend\">").append(ResourceUtils.message(pageContext, udfFieldGroup.getDescription(), udfFieldGroup.getDescription())).append("</legend>");
					html.insert(0, "<table class=\"udf\"><tr>");
					if (colsPerRow.equals(colsPerRowCopy) && html.toString().endsWith("</tr><tr>")) {
						html = html.replace(html.lastIndexOf("</tr><tr>"), html.lastIndexOf("</tr><tr>") + "</tr><tr>".length(), "");
					}
					html.append("</tr>");
					html.append("</table>");
					pageHtml.append(html).append(newline);
					pageHtml.append("</fieldset>");
				}
			}
			// System.out.println(pageHtml.toString());
			pageContext.getOut().write(pageHtml.toString());
			return SKIP_BODY;
		}
		catch (Exception e) {
			try {
				pageContext.getOut().write(e.toString());
				return SKIP_BODY;
			}
			catch (IOException ioe) {
				throw new JspTagException("UDFTag Error:" + ioe.toString());
			}
		}
	}

	public int doEndTag() throws JspTagException {
		try {
			return EVAL_PAGE;
		}
		catch (Exception e) {
			release();
			throw new JspTagException(e.getMessage());
		}
	}

	public void release() {
		super.release();
	}

	private String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return StringEscapeUtils.escapeHtml(value).replaceAll("'", "&#39;");
	}

	private List<String> getNewFields(List<XField> fields, List<XUdfFieldDefinition> udfFieldDefinitions) {
		List<String> newFields = new ArrayList<String>();
		String kindOfAction = pageContext.getRequest().getParameter("kindOfAction");
		if ("refreshUDF".equalsIgnoreCase(kindOfAction) || "checkMissingUDF".equalsIgnoreCase(kindOfAction)) {
			List<String> inputFields = new ArrayList<String>();
			String fieldName = null;
			if (fields != null) {
				for (XField field : fields) {
					inputFields.add(field.getName());
				}
			}
			boolean hasNonTriggerFields = false;
			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
				fieldName = udfFieldDefinition.getName();
				if (!inputFields.contains(fieldName)) {
					newFields.add(fieldName);
				}
				if (inputFields.contains(fieldName) && !udfFieldDefinition.getIsFieldGroupTrigger()) {
					hasNonTriggerFields = true;
				}
			}
			if (!hasNonTriggerFields) {
				newFields.clear();
			}
		}
		return newFields;
	}

	private String updateLookupLink(String lookupLink, List<XUdfFieldDefinition> udfFieldDefinitions, XUdfFieldDefinition currUdfFieldDefinition) {
		String fieldName = "UDF_" + this.groupName + "_" + currUdfFieldDefinition.getType() + "_" + currUdfFieldDefinition.getName();
		lookupLink = StringUtils.replace(lookupLink, "'" + currUdfFieldDefinition.getName() + "'", "'" + fieldName + "'");
		if (lookupLink.indexOf("new Array(") != -1) {
			// System.out.println(lookupLink);
			int pos = lookupLink.indexOf(",");
			if (pos != -1) {
				int index = 1;
				String first = "";
				String middle = "";
				String last = lookupLink;

				// Skip first 5 parameters
				while (pos != -1 && index <= 5) {
					first = first + last.substring(0, pos + 1);
					last = last.substring(pos + 1);
					index++;
					pos = last.indexOf(",");
				}

				if (last.startsWith("new Array(")) { // dependantFields configured
					pos = last.indexOf(")");
					first = first + "new Array(";
					middle = last.substring(0 + "new Array(".length(), pos);
					last = last.substring(pos + 1);

					// DBField1,UIField1,DBField2,UIField2,etc..
					// System.out.println("dependantFields : " + middle );
					String pair[] = middle.split(",");
					for (int ind = 0; ind < pair.length; ind = ind + 2) {
						for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
							if (StringUtils.trim(pair[ind + 1]).equalsIgnoreCase("'" + udfFieldDefinition.getName() + "'")) {
								fieldName = "UDF_" + this.groupName + "_" + udfFieldDefinition.getType() + "_" + udfFieldDefinition.getName();
								pair[ind + 1] = "'" + fieldName + "'";
							}
						}
						first += pair[ind] + "," + pair[ind + 1];
						if ((ind + 2) < pair.length) {
							first += ",";
						}
					}
					first += ")";
					pos = last.indexOf(",");
				}
				first = first + last.substring(0, pos + 1);
				last = last.substring(pos + 1);

				if (last.startsWith("new Array(")) { // multiFields configured
					pos = last.indexOf(")");
					first = first + "new Array(";
					middle = last.substring(0 + "new Array(".length(), pos);
					last = last.substring(pos);

					// System.out.println("multiFields : " + middle );
					// DBField1,UIField1,DBField2,UIField2,etc..
					String pair[] = middle.split(",");
					for (int ind = 0; ind < pair.length; ind = ind + 2) {
						for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
							if (StringUtils.trim(pair[ind + 1]).equalsIgnoreCase("'" + udfFieldDefinition.getName() + "'")) {
								fieldName = "UDF_" + this.groupName + "_" + udfFieldDefinition.getType() + "_" + udfFieldDefinition.getName();
								pair[ind + 1] = "'" + fieldName + "'";
							}
						}
						first += pair[ind] + "," + pair[ind + 1];
						if ((ind + 2) < pair.length) {
							first += ",";
						}
					}
				}
				lookupLink = first + last;
			}
			// System.out.println(lookupLink);
		}
		return lookupLink;
	}

	public static void main(String[] args) {
		try {
			org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
			xmlOptions.setLoadStripWhitespace();
			UDFTag udfTag = new UDFTag();
			// udfTag.setPageContext(new org.springframework.mock.web.MockPageContext());
			com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinitionDocument metadata = XUdfDomainDefinitionDocument.Factory.parse(new File(
					"C:\\projects\\cargores\\tags\\projects\\cargoops\\R5.8.0.0.016\\modules\\cargores-resources\\src\\main\\resources\\UdfDefinitions\\UdfDomainDefinitionDocument.cargores.xml"), xmlOptions);
			udfTag.setMetadata(metadata);
			List<XField> fields = new ArrayList<XField>();

			XField field = XField.Factory.newInstance();
			field.setName("NUMBER_OF_ANIMALS");
			field.setType("INTEGER");
			field.setInteger(1);
			fields.add(field);

			field = XField.Factory.newInstance();
			field.setName("ANIMAL_TYPE");
			field.setType("STRING");
			field.setString("CAT");
			fields.add(field);

			field = XField.Factory.newInstance();
			field.setName("ANIMAL_BREED");
			field.setType("STRING");
			field.setString("PERSIA");
			fields.add(field);

			field = XField.Factory.newInstance();
			field.setName("IS_AVI_BOOKING_LINE");
			field.setType("BOOLEAN");
			field.setBoolean(true);
			fields.add(field);

			field = XField.Factory.newInstance();
			field.setName("IS_DGR_BOOKING_LINE");
			field.setType("BOOLEAN");
			field.setBoolean(true);
			fields.add(field);

			/*
			field = XField.Factory.newInstance();
			field.setName("PACKAGING_INSTRUCTIONS");
			field.setType("STRING");
			field.setString("Test");
			fields.add(field);
			*/

			udfTag.setFields(fields);
			udfTag.setGrouptype("BookingLine");
			udfTag.setGroupname("BKGLINE1");
			UdfDomainUtils udfDomainUtils = udfTag.getUdfDomainUtils();

			List<String> unWantedFields = udfDomainUtils.getUnWantedFields(fields);
			for (String fieldName : unWantedFields) {
				System.out.println("Remove  field : " + fieldName);
			}

			udfTag.doStartTag();

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
