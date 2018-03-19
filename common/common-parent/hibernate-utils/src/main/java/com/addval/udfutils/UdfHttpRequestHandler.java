package com.addval.udfutils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.regexp.RE;

import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;
import com.addval.utils.date.DateXBeanConverter;
import com.addval.utils.udf.xmlschema.x2011.XField;

public class UdfHttpRequestHandler {
	public static String newline = System.getProperty("line.separator");

	private String dateFormat = null;
	private DateXBeanConverter dateXBeanConverter = null;

	public UdfHttpRequestHandler(String dateFormat) {
		this.dateFormat = dateFormat;
		dateXBeanConverter = new DateXBeanConverter();
	}

	public HashMap<String, List<XField>> getUldFields(HttpServletRequest request, List<String> udfGroupNames) throws Exception {
		LinkedHashMap<String, String> requestMap = new LinkedHashMap<String, String>();
		Enumeration paramNames = request.getParameterNames();
		String paramName = null;
		String fieldValue = null;
		while (paramNames.hasMoreElements()) {
			paramName = (String) paramNames.nextElement();
			if (paramName.startsWith("UDF_")) {
				fieldValue = request.getParameter(paramName);
				requestMap.put(paramName, fieldValue);
			}
		}
		return getUldFields(requestMap, udfGroupNames);
	}

	public HashMap<String, List<XField>> getUldFields(LinkedHashMap<String, String> request, List<String> udfGroupNames) throws Exception {

		// System.out.println(newline + "Process HttpServletRequest for UDF...." + newline);

		HashMap<String, List<XField>> udfFieldsMap = new HashMap<String, List<XField>>();
		List<XField> udfFields = null;
		XField xField = null;

		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
		sdf.setTimeZone(AVConstants._GMT_TIMEZONE);

		String paramNameKeys[] = null;

		String groupName = null;
		String fieldType = null;
		String fieldName = null;
		String fieldValue = null;

		// UDF Field Naming : UDF_<GROUPNAME>_<FieldType>_<FieldName>
		for (String paramName : request.keySet()) {
			if (paramName.startsWith("UDF_")) {
				paramNameKeys = StringUtils.split(paramName, "_");
				if (paramNameKeys.length < 4) {
					continue;
				}
				groupName = paramNameKeys[1];
				if (!udfGroupNames.contains(groupName)) {
					continue;
				}
				if (udfFieldsMap.containsKey(groupName)) {
					udfFields = udfFieldsMap.get(groupName);
				}
				else {
					udfFields = new ArrayList<XField>();
					udfFieldsMap.put(groupName, udfFields);
				}
				fieldType = paramNameKeys[2];
				fieldName = StringUtils.substring(paramName, paramName.indexOf("_" + fieldType + "_") + ("_" + fieldType + "_").length());
				fieldValue = request.get(paramName);

				// System.out.println(groupName + "," + fieldName + "," + fieldType + "," + fieldValue);

				xField = XField.Factory.newInstance();
				xField.setName(fieldName);
				xField.setType(fieldType);

				// (STRING|BOOLEAN|INTEGER|DOUBLE|DATE)
				if (!StrUtl.isEmptyTrimmed(fieldValue)) {
					if ("DATE".equalsIgnoreCase(fieldType)) {
						Date date = sdf.parse(fieldValue, new ParsePosition(0));
						xField.setDate(dateXBeanConverter.toDateString(date, false, 0));
					}
					else if ("BOOLEAN".equalsIgnoreCase(fieldType)) {
						xField.setBoolean(Boolean.valueOf(fieldValue));
					}
					else if ("INTEGER".equalsIgnoreCase(fieldType) && isInteger(fieldValue)) {
						xField.setInteger(Integer.valueOf(fieldValue));
					}
					else if ("DOUBLE".equalsIgnoreCase(fieldType) && isDouble(fieldValue)) {
						xField.setDouble(Double.valueOf(fieldValue));
					}
					else {
						xField.setString(fieldValue);
					}
				}
				udfFields.add(xField);
			}
		}
		return udfFieldsMap;
	}

	public boolean isInteger(String value) {
		try {
			String intRegExp = "^\\d*$";
			RE matcher = new RE(intRegExp, RE.MATCH_CASEINDEPENDENT);
			if (!matcher.match(value)) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean isDouble(String value) {
		try {
			String doubleRegExp = "^\\d*(\\.\\d*)?$";
			RE matcher = new RE(doubleRegExp, RE.MATCH_CASEINDEPENDENT);
			if (!matcher.match(value)) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

}
