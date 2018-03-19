package com.addval.taglib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

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

			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
				uiCmd = udfFieldDefinition.getUIComponentMetadata();
				if (uiCmd != null) {
					fieldName = "UDF_" + this.groupName + "_" + udfFieldDefinition.getType() + "_" + udfFieldDefinition.getName();
					fieldLabel = uiCmd.getAttributeLabel();
					fieldValue = escapeHtml(uiCmd.getAttributeDefaultValue());
					newFieldCss = newFields.contains(udfFieldDefinition.getName()) ? "newudf" : "";
					// System.out.println(udfFieldDefinition.getType() + " : " + udfFieldDefinition.getName() + " : " + fieldValue);

					if (udfFieldDefinition.getIsFieldGroupTrigger()) {
						// HIDDEN
						htmlHidden.append("<INPUT type=\"hidden\" name=\"" + fieldName + "\" value=\"" + fieldValue + "\"/>");
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

						html.append("<td class=\"label\">");
						html.append(fieldLabel);
						if (mandatory) {
							html.append("<font class=\"mandatory\">*</font>");
						}
						html.append(" :");
						if (!StrUtl.isEmptyTrimmed(lookupLink) && !this.readOnly) {
							lookupLink = StringUtils.replace(lookupLink, "'" + udfFieldDefinition.getName() + "'", "'" + fieldName + "'");
							html.append(lookupLink);
						}
						html.append("</td>");
						html.append("<td class=\"content\">");
						if (isReadOnly || this.readOnly) {
							fieldDescValue = fieldValue;
							if (!StrUtl.isEmptyTrimmed(fieldValue)) {
								if ("BOOLEAN".equalsIgnoreCase(fieldType)) {
									fieldDescValue = Boolean.valueOf(fieldValue) ? "Yes" : "No";
								}
							}
							html.append(fieldDescValue + "<INPUT type=\"hidden\" name=\"" + fieldName + "\" value=\"" + fieldValue + "\">");
						}
						else if ("TEXT".equalsIgnoreCase(fieldType) || "INTEGER".equalsIgnoreCase(fieldType) || "DOUBLE".equalsIgnoreCase(fieldType) || "DATE".equalsIgnoreCase(fieldType)) {
							html.append("<input ");
							html.append(" type =\"text\" ");
							html.append(" name =\"").append(fieldName).append("\"");
							html.append(" value =\"").append(fieldValue).append("\"");
							if (!StrUtl.isEmptyTrimmed(fieldWidth)) {
								String width[] = StringUtils.split(fieldWidth, ",");
								if (width.length > 0) {
									html.append(" size =\"").append(width[0]).append("\"");
								}
								if (width.length > 1) {
									html.append(" maxlength =\"").append(width[1]).append("\"");
								}
							}
							if (!StrUtl.isEmptyTrimmed(onChangeFn)) {
								html.append(" onChange =\"" + onChangeFn + "\"");
							}
							if (!StrUtl.isEmptyTrimmed(newFieldCss)) {
								html.append(" class =\"" + newFieldCss + "\"");
							}

							if ("DATE".equalsIgnoreCase(fieldType)) {
								fieldFormat = !StrUtl.isEmptyTrimmed(fieldFormat) ? fieldFormat : "dd-MMM-yyyy";
								html.append(" onBlur=\"isValidDate(this,'" + fieldFormat + "' )\"");
							}

							html.append("/>");

							if ("DATE".equalsIgnoreCase(fieldType)) {
								html.append("<img  src=\"../images/calendar.gif\" border=\"0\" height=\"16\" width=\"25\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" onClick=\"return launchCalendar('").append(fieldName).append("','").append(fieldFormat).append("',this)\" />");
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
						if (!StrUtl.isEmptyTrimmed(onChangeFn)) {
							ajaxLoaderId = fieldName + "_LOADER";
							html.append("<IMG SRC=\"../images/ajax-loader.gif\" WIDTH=\"20\" HEIGHT=\"20\" style=\"visibility:hidden;\" id=\"" + ajaxLoaderId + "\"/>");
						}

						html.append("</td>");

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
			//ActionErrors actionErrors = null;
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
			pageHtml.append(htmlError).append(newline);
			pageHtml.append(htmlHidden).append(newline);

			for (XUdfFieldGroup udfFieldGroup : udfFieldGroups) {
				group = udfFieldGroup.getName();
				html = htmlMap.get(group);
				colsPerRow = colsPerRowMap.get(group);
				colsPerRowCopy = colsPerRowMapCopy.get(group);
				if (html.toString().length() > 0) {
					pageHtml.append("<fieldset title=\"").append(udfFieldGroup.getDescription()).append("\" class=\"udffieldset\">");
					pageHtml.append("<legend class=\"udffieldsetlegend\">").append(udfFieldGroup.getDescription()).append("</legend>");
					html.insert(0, "<br/><table><tr>");
					if (colsPerRow.equals(colsPerRowCopy) && html.toString().endsWith("</tr><tr>")) {
						html = html.replace(html.lastIndexOf("</tr><tr>"), html.lastIndexOf("</tr><tr>") + "</tr><tr>".length(), "");
					}
					html.append("</tr>");
					html.append("</table><br/>");
					pageHtml.append(html).append(newline);
					pageHtml.append("</fieldset>");
				}
			}
			//System.out.println(pageHtml.toString());
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
        String kindOfAction = pageContext.getRequest().getParameter( "kindOfAction" );
        if("refreshUDF".equalsIgnoreCase(kindOfAction) || "checkMissingUDF".equalsIgnoreCase(kindOfAction)){
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

	public static void main(String[] args) {
		try {
			org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
			xmlOptions.setLoadStripWhitespace();
			UDFTag udfTag = new UDFTag();
			com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinitionDocument metadata = XUdfDomainDefinitionDocument.Factory.parse(new File("C:\\projects\\cargores\\migrate\\modules\\cargores-resources\\src\\main\\resources\\UdfDomainDefinitionDocument.cargores.xml"), xmlOptions);
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

			UdfDomainUtils udfDomainUtils = udfTag.getUdfDomainUtils();
			
			List<String> unWantedFields = udfDomainUtils.getUnWantedFields(fields);
			for(String fieldName : unWantedFields){
				System.out.println("Remove  field : " + fieldName);
			}
			
			//udfTag.doStartTag();

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
