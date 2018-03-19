package com.addval.udfutils;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.regexp.RE;
import org.apache.xmlbeans.XmlException;

import com.addval.action.ActionMessage;
import com.addval.action.ActionMessages;
import com.addval.udf.api.UdfApplicationUsageException;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;
import com.addval.utils.ValidatorSourceParser;
import com.addval.utils.ValidatorSpecification;
import com.addval.utils.date.DateXBeanConverter;
import com.addval.utils.udf.xmlschema.x2011.XCriterion;
import com.addval.utils.udf.xmlschema.x2011.XField;
import com.addval.utils.udf.xmlschema.x2011.XFieldMissingValueRequest;
import com.addval.utils.udf.xmlschema.x2011.XRestrictions;
import com.addval.utils.udf.xmlschema.x2011.XUdfApplicationStaticInfo;
import com.addval.utils.udf.xmlschema.x2011.XUdfConditionalUIComponentMetadata;
import com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinition;
import com.addval.utils.udf.xmlschema.x2011.XUdfDomainDefinitionDocument;
import com.addval.utils.udf.xmlschema.x2011.XUdfEnabledObject;
import com.addval.utils.udf.xmlschema.x2011.XUdfEnabledObjectFields;
import com.addval.utils.udf.xmlschema.x2011.XUdfFieldDefinition;
import com.addval.utils.udf.xmlschema.x2011.XUdfFieldGroup;
import com.addval.utils.udf.xmlschema.x2011.XUdfUIComponentMetadata;

public class UdfDomainUtils {
	// private static transient final Logger logger = LogMgr.getLogger(UdfDomainUtils.class);
	private XUdfDomainDefinition udfDomain = null;
	private XUdfEnabledObjectFields udfEnabledObjectFields = null;
	private DateXBeanConverter dateXBeanConverter = null;

	public UdfDomainUtils(XUdfDomainDefinitionDocument udfDomainDoc, String udfEnabledObjectId) throws UdfApplicationUsageException {
		if (udfDomainDoc == null) {
			throw new UdfApplicationUsageException("XUdfDomainDefinitionDocument cannot be NULL");
		}
		try {
			XUdfDomainDefinitionDocument udfDomainDocCopy = XUdfDomainDefinitionDocument.Factory.parse(udfDomainDoc.toString());
			this.udfDomain = udfDomainDocCopy.getXUdfDomainDefinition();
		}
		catch (XmlException ex) {
			throw new UdfApplicationUsageException(ex.getMessage());
		}
		this.udfEnabledObjectFields = findXUdfEnabledObjectFields(udfEnabledObjectId);
		this.dateXBeanConverter = new DateXBeanConverter();
	}

	public List<String> getFieldGroupTriggerFieldNames() {
		List<String> fieldGroupTriggerFieldNames = new ArrayList<String>();
		XUdfFieldDefinition udfFieldDefinitionArray[] = udfEnabledObjectFields.getFieldDefinitionArray();
		if (udfFieldDefinitionArray != null) {
			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitionArray) {
				if (udfFieldDefinition.getIsFieldGroupTrigger()) {
					fieldGroupTriggerFieldNames.add(udfFieldDefinition.getName());
				}
			}
		}
		return fieldGroupTriggerFieldNames;
	}

	public List<String> getUnWantedFields(List<XField> udfFields) {
		List<String> unWantedFields = new ArrayList<String>();
		List<String> existingFields = new ArrayList<String>();
		String fieldName = null;
		List<XField> xFields = null;
		if (udfFields != null) {
			for (XField field : udfFields) {
				fieldName = field.getName();
				existingFields.add(fieldName);
			}
		}

		XField newField = XField.Factory.newInstance();
		newField.setType("BOOLEAN");
		newField.setBoolean(true);

		List<XUdfFieldDefinition> udfFieldDefinitions = null;
		List<String> fieldGroupTriggerFieldNames = getFieldGroupTriggerFieldNames();
		HashMap<String, ArrayList<String>> fieldsInGroupMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> fieldsInGroup = null;

		for (String triggerFieldName : fieldGroupTriggerFieldNames) {
			newField.setName(triggerFieldName);

			xFields = new ArrayList<XField>();
			xFields.addAll(udfFields);
			xFields.add(newField);

			udfFieldDefinitions = matchFieldDefinition(xFields);
			fieldsInGroup = new ArrayList<String>();
			//System.out.println(triggerFieldName);

			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
				fieldName = udfFieldDefinition.getName();
				//System.out.println("\t" + fieldName);
				fieldsInGroup.add(fieldName);
			}
			fieldsInGroupMap.put(triggerFieldName, fieldsInGroup);
		}

		List<String> validFields = new ArrayList<String>();
		for (String triggerFieldName : fieldsInGroupMap.keySet()) {
			if(existingFields.contains(triggerFieldName)){
				validFields.addAll(fieldsInGroupMap.get(triggerFieldName));
			}
		}
		
		for (String triggerFieldName : fieldsInGroupMap.keySet()) {
			if(!existingFields.contains(triggerFieldName)){
				for (String fldName : fieldsInGroupMap.get(triggerFieldName)) {
					if (!validFields.contains(fldName) && existingFields.contains(fldName) && !unWantedFields.contains(fldName)) {
						unWantedFields.add(fldName);	
					}
				}
			}
		}
		return unWantedFields;
	}

	public List<XField> getMissingFields(List<XField> udfFields) {
		List<XField> missingFields = new ArrayList<XField>();
		List<XUdfFieldDefinition> udfFieldDefinitions = matchFieldDefinition(udfFields);
		List<String> existingFields = new ArrayList<String>();
		String fieldName = null;
		if (udfFields != null) {
			for (XField field : udfFields) {
				existingFields.add(field.getName());
			}
		}
		XField missingField = null;
		XFieldMissingValueRequest missingValueRequest = null;
		for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
			fieldName = udfFieldDefinition.getName();
			if (!existingFields.contains(fieldName)) {
				missingField = XField.Factory.newInstance();
				missingField.setName(fieldName);
				missingField.setType(udfFieldDefinition.getType());
				missingValueRequest = missingField.addNewMissingValueRequest();

				missingFields.add(missingField);
			}
		}
		return missingFields;
	}

	public List<XUdfFieldGroup> matchFieldGroup(List<XField> udfFields) {
		List<XUdfFieldGroup> udfFieldGroups = new ArrayList<XUdfFieldGroup>();
		HashMap<String, Object> bindingVars = getBindingVars(udfFields);
		XUdfFieldGroup udfFieldGroupArray[] = udfEnabledObjectFields.getFieldGroupArray();
		if (udfFieldGroupArray != null) {
			XCriterion criterion = null;
			boolean match = false;
			for (XUdfFieldGroup udfFieldGroup : udfFieldGroupArray) {
				criterion = udfFieldGroup.getCriterion();
				if (criterion != null) {
					match = isMatch(bindingVars, criterion.getRestrictionsArray());
					if (match) {
						udfFieldGroups.add(udfFieldGroup);
					}
				}
				else {
					// Un-Conditional FieldGroup
					udfFieldGroups.add(udfFieldGroup);
				}
			}
		}
		return udfFieldGroups;
	}

	public List<XUdfFieldDefinition> matchFieldDefinition(List<XField> udfFields) {
		List<XUdfFieldDefinition> udfFieldDefinitions = new ArrayList<XUdfFieldDefinition>();
		udfFieldDefinitions.addAll(matchTriggerFieldDefinition(udfFields));
		List<XUdfFieldGroup> udfFieldGroups = matchFieldGroup(udfFields);
		for (XUdfFieldGroup udfFieldGroup : udfFieldGroups) {
			udfFieldDefinitions.addAll(matchFieldDefinition(udfFields, udfFieldGroup.getName()));
		}
		return sortXUdfFieldDefinitions(udfFieldDefinitions);
	}

	public ActionMessages validateFieldTypeValue(List<XField> udfFields,javax.servlet.http.HttpServletRequest request) {
		return validate(udfFields, request,true);
	}

	public ActionMessages validate(List<XField> udfFields,javax.servlet.http.HttpServletRequest request) {
		return validate(udfFields, request,false);
	}

	public ActionMessages validateFieldTypeValue(List<XField> udfFields) {
		return validate(udfFields, null,true);
	}

	public ActionMessages validate(List<XField> udfFields) {
		return validate(udfFields, null,false);
	}
	
	private ActionMessages validate(List<XField> udfFields, javax.servlet.http.HttpServletRequest request,boolean validateFieldTypeValueOnly) {
		ActionMessages errors = new ActionMessages();
		if (hasMissingValueRequest(udfFields)) {
			errors.add("MISSING_UDF", new ActionMessage(udfEnabledObjectFields.getEnabledObjectId(), "Missing UDF fields."));			
			return errors;
		}

		String requiredMsg = "Please provide a value for %s.";
		String minlengthMsg = "Please provide at least %d characters for %s.";
		String maxlengthMsg = "Please provide at most %d characters for %s.";
		String minMsg = "%2$s requires a value of at least %1$d.";
		String maxMsg = "%2$s requires a value no larger than %1$d.";
		String regexpMsg = "%2$s does not match pattern '%1$s'.";
		String integerMsg = "Please provide an integer value for %s.";
		String numberMsg = "Please provide a numeric value for %s.";

		/*
		 * Contributes the basic set of validators
		 * required
		 * minlength
		 * maxlength
		 * min
		 * max
		 * regexp
		 */

		/* eg..
		 * required
		 * required,regexp=^([\w\s\.\&\-\_]{0,30})?$
		 * required,regexp=([\w\s\.\&\-\_]{0,30})?
		 * required,min=4,max=30
		 * required,minlength=8,maxlength=11
		 */

		List<XUdfFieldDefinition> udfFieldDefinitions = matchFieldDefinition(udfFields);
		XUdfUIComponentMetadata uiCmd = null;

		String fieldName = null;
		String fieldType = null;
		String fieldLabel = null;
		String fieldValue = null;
		String fieldFormat = null;
		String validatorSpec = null;
		Pattern pattern = null;
		Matcher matcher = null;
		List<ValidatorSpecification> validators = null;
		for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
			uiCmd = udfFieldDefinition.getUIComponentMetadata();
			fieldType = udfFieldDefinition.getType();
			if (uiCmd != null) {
				fieldName = udfFieldDefinition.getName();
				fieldLabel = uiCmd.getAttributeLabel();
				if(request != null){
					fieldLabel = com.addval.springstruts.ResourceUtils.message(request, fieldLabel,fieldLabel);
				}
				
				fieldValue = uiCmd.getAttributeDefaultValue();
				fieldFormat = uiCmd.getAttributeFormat();
				validatorSpec = uiCmd.getValidate();
				validators = ValidatorSourceParser.parse(validatorSpec);
				try {
					// Data Type Validation.. (STRING|BOOLEAN|INTEGER|DOUBLE|DATE)
					if (!StrUtl.isEmptyTrimmed(fieldValue)) {
						if ("DATE".equalsIgnoreCase(fieldType)) {
							fieldFormat = !StrUtl.isEmptyTrimmed(fieldFormat) ? fieldFormat : "dd-MMM-yyyy";
							boolean isDateInFormat = isDateInFormat(fieldFormat, fieldValue);
							if (!isDateInFormat) {
								throw new RuntimeException("Invalid " + fieldLabel + " format.");
							}
						}
						else if ("INTEGER".equalsIgnoreCase(fieldType)) {
							boolean isInteger = isInteger(fieldValue);
							if (!isInteger) {
								throw new RuntimeException(String.format(integerMsg, fieldLabel));
							}
						}
						else if ("DOUBLE".equalsIgnoreCase(fieldType)) {
							boolean isDouble = isDouble(fieldValue);
							if (!isDouble) {
								throw new RuntimeException(String.format(numberMsg, fieldLabel));
							}
						}
					}

					if (!validateFieldTypeValueOnly) {
						for (ValidatorSpecification validator : validators) {
							if (validator.getValidatorType().equalsIgnoreCase("required")) {
								if (StrUtl.isEmptyTrimmed(fieldValue)) {
									throw new RuntimeException(String.format(requiredMsg, fieldLabel));
								}
							}
							else if (validator.getValidatorType().equalsIgnoreCase("regexp")) {
								if (!StrUtl.isEmptyTrimmed(fieldValue)) {
									pattern = Pattern.compile(validator.getConstraintValue());
									matcher = pattern.matcher(fieldValue);
									if (!matcher.matches()) {
										if(regexpMsg.indexOf("%2$") != -1){
											throw new RuntimeException(String.format(regexpMsg,validator.getConstraintValue(),fieldLabel));
										}
										else if(regexpMsg.indexOf("%1$") != -1){
											throw new RuntimeException(String.format(regexpMsg,fieldLabel));
										}
										else {
											throw new RuntimeException(String.format(regexpMsg,fieldLabel));
										}
									}
								}
							}
							else if (validator.getValidatorType().equalsIgnoreCase("minlength")) {
								fieldValue = !StrUtl.isEmptyTrimmed(fieldValue) ? fieldValue : "";
								Integer minlength = Integer.valueOf(validator.getConstraintValue());
								if (fieldValue.length() < minlength) {
									throw new RuntimeException(String.format(minlengthMsg, minlength, fieldLabel));
								}
							}
							else if (validator.getValidatorType().equalsIgnoreCase("maxlength")) {
								fieldValue = !StrUtl.isEmptyTrimmed(fieldValue) ? fieldValue : "";
								Integer maxlength = Integer.valueOf(validator.getConstraintValue());
								if (fieldValue.length() > maxlength) {
									throw new RuntimeException(String.format(maxlengthMsg, maxlength, fieldLabel));
								}
							}
							else if (validator.getValidatorType().equalsIgnoreCase("min")) {
								Double doubleValue = fieldValue != null ? Double.valueOf(fieldValue) : 0.0d;
								Integer min = Integer.valueOf(validator.getConstraintValue());
								if (doubleValue < min) {
									throw new RuntimeException(String.format(minMsg, min, fieldLabel));
								}
							}
							else if (validator.getValidatorType().equalsIgnoreCase("max")) {
								Double doubleValue = fieldValue != null ? Double.valueOf(fieldValue) : 0.0d;
								Integer max = Integer.valueOf(validator.getConstraintValue());
								if (doubleValue > max) {
									throw new RuntimeException(String.format(maxMsg, max, fieldLabel));
								}
							}
						}
					}
				}
				catch (Exception ex) {
					errors.add("UDF", new ActionMessage(fieldName, ex.getMessage()));
				}
			}
		}
		return errors;
	}

	private List<XUdfFieldDefinition> matchTriggerFieldDefinition(List<XField> udfFields) {
		HashMap<String, Object> bindingVars = getBindingVars(udfFields);
		List<XUdfFieldDefinition> udfFieldDefinitions = new ArrayList<XUdfFieldDefinition>();
		XUdfFieldDefinition udfFieldDefinitionArray[] = udfEnabledObjectFields.getFieldDefinitionArray();
		if (udfFieldDefinitionArray != null) {
			String fieldName = null;
			Object fieldValue = null;
			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitionArray) {
				fieldName = udfFieldDefinition.getName();
				if (bindingVars.containsKey(fieldName) && udfFieldDefinition.getIsFieldGroupTrigger()) {
					fieldValue = bindingVars.get(fieldName);
					if (fieldValue != null) {
						udfFieldDefinition.getUIComponentMetadata().setAttributeDefaultValue(fieldValue.toString());
					}
					udfFieldDefinitions.add(udfFieldDefinition);
				}
			}
		}
		return udfFieldDefinitions;
	}

	private List<XUdfFieldDefinition> matchFieldDefinition(List<XField> udfFields, String fieldGroup) {
		List<XUdfFieldDefinition> udfFieldDefinitions = new ArrayList<XUdfFieldDefinition>();
		HashMap<String, Object> bindingVars = getBindingVars(udfFields);
		HashMap<String, List<String>> dependentFieldsMap = new HashMap<String, List<String>>();
		List<String> fields = null;

		XUdfFieldDefinition udfFieldDefinitionArray[] = udfEnabledObjectFields.getFieldDefinitionArray();

		if (udfFieldDefinitionArray != null) {
			XCriterion criterion = null;
			boolean match = false;
			String fieldName = null;
			String fieldType = null;
			String fieldFormat = null;
			Object fieldValue = null;

			XUdfConditionalUIComponentMetadata udfConditionalUIComponentMetadataArray[] = null;
			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitionArray) {
				if (udfFieldDefinition.getGroup().equalsIgnoreCase(fieldGroup)) {
					match = true;
					fieldName = udfFieldDefinition.getName();
					fieldType = udfFieldDefinition.getType();
					criterion = udfFieldDefinition.getCriterion();
					if (criterion != null) {
						match = isMatch(bindingVars, criterion.getRestrictionsArray());
						fields = getDependentFields(criterion.getRestrictionsArray(), new ArrayList<String>());
						if (fields.size() > 0) {
							dependentFieldsMap.put(fieldName, fields);
						}
					}
					else if (udfFieldDefinition.getIsFieldGroupTrigger()) {
						match = false;
					}

					if (match) {
						udfFieldDefinitions.add(udfFieldDefinition);
					}

					udfConditionalUIComponentMetadataArray = udfFieldDefinition.getConditionalUIComponentMetadataArray();
					if (udfConditionalUIComponentMetadataArray != null && udfConditionalUIComponentMetadataArray.length > 0) {
						for (XUdfConditionalUIComponentMetadata udfConditionalUIComponentMetadata : udfConditionalUIComponentMetadataArray) {
							criterion = udfConditionalUIComponentMetadata.getCriterion();
							if (criterion != null) {
								match = isMatch(bindingVars, criterion.getRestrictionsArray());
								if (match) {
									udfFieldDefinition.setUIComponentMetadata(udfConditionalUIComponentMetadata.getUIComponentMetadata());
									udfFieldDefinition.setConditionalUIComponentMetadataArray(null);
									break;
								}
							}
						}
					}

					if (bindingVars.containsKey(fieldName)) {
						// Set the value from request.
						if (udfFieldDefinition.getUIComponentMetadata() != null) {
							fieldValue = bindingVars.get(fieldName);
							if (fieldValue != null) {
								if ("DATE".equalsIgnoreCase(fieldType)) {
									fieldFormat = udfFieldDefinition.getUIComponentMetadata().getAttributeFormat();
									fieldFormat = !StrUtl.isEmptyTrimmed(fieldFormat) ? fieldFormat : "dd-MMM-yyyy";
									SimpleDateFormat sdf = new SimpleDateFormat(fieldFormat);
									sdf.setTimeZone(AVConstants._GMT_TIMEZONE);
									udfFieldDefinition.getUIComponentMetadata().setAttributeDefaultValue(sdf.format((Date) fieldValue));
								}
								else {
									udfFieldDefinition.getUIComponentMetadata().setAttributeDefaultValue(fieldValue.toString());
								}

							}
							else {
								udfFieldDefinition.getUIComponentMetadata().setAttributeDefaultValue(null);
							}
						}
					}
				}
			}
		}

		// Set the dependantFields.
		HashMap<String, List<String>> dependantFieldsMap = new HashMap<String, List<String>>();
		for (String fieldName : dependentFieldsMap.keySet()) {
			fields = dependentFieldsMap.get(fieldName);
			for (String fldName : fields) {
				if (!dependantFieldsMap.containsKey(fldName)) {
					dependantFieldsMap.put(fldName, new ArrayList<String>());
				}
				dependantFieldsMap.get(fldName).add(fieldName);
			}
		}
		for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
			if (dependantFieldsMap.containsKey(udfFieldDefinition.getName())) {
				udfFieldDefinition.setDependantFields(convertToString(dependantFieldsMap.get(udfFieldDefinition.getName())));
			}
		}
		return udfFieldDefinitions;
	}

	private List<XUdfFieldDefinition> sortXUdfFieldDefinitions(List<XUdfFieldDefinition> udfFieldDefinitions) {
		HashMap<String, XUdfFieldDefinition> udfFieldRef = new HashMap<String, XUdfFieldDefinition>();
		for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitions) {
			udfFieldRef.put(udfFieldDefinition.getName(), udfFieldDefinition);
		}
		List<XUdfFieldDefinition> sortedUdfFieldDefinitions = new ArrayList<XUdfFieldDefinition>();
		XUdfFieldDefinition udfFieldDefinitionArray[] = udfEnabledObjectFields.getFieldDefinitionArray();
		if (udfFieldDefinitionArray != null) {
			for (XUdfFieldDefinition udfFieldDefinition : udfFieldDefinitionArray) {
				if (udfFieldRef.containsKey(udfFieldDefinition.getName())) {
					sortedUdfFieldDefinitions.add(udfFieldRef.get(udfFieldDefinition.getName()));
				}
			}
		}

		return sortedUdfFieldDefinitions;
	}

	private XUdfEnabledObjectFields findXUdfEnabledObjectFields(String udfEnabledObjectId) throws UdfApplicationUsageException {
		if (udfDomain != null) {
			XUdfApplicationStaticInfo udfEnabledObjectIds = udfDomain.getApplicationStaticInfo();
			XUdfEnabledObject udfEnabledObjectArray[] = udfEnabledObjectIds.getUdfEnabledObjectArray();
			boolean match = false;
			if (udfEnabledObjectArray != null) {
				for (XUdfEnabledObject udfEnabledObject : udfEnabledObjectArray) {
					if (udfEnabledObject.getId().equalsIgnoreCase(udfEnabledObjectId)) {
						match = true;
						break;
					}
				}
			}
			if (match) {
				XUdfEnabledObjectFields[] udfEnabledObjectFieldsArray = udfDomain.getEnabledFieldsArray();
				if (udfEnabledObjectFieldsArray != null) {
					for (XUdfEnabledObjectFields udfEnabledObjectFields : udfEnabledObjectFieldsArray) {
						if (udfEnabledObjectFields.getEnabledObjectId().equalsIgnoreCase(udfEnabledObjectId)) {
							return udfEnabledObjectFields;
						}
					}
				}
			}
		}
		throw new UdfApplicationUsageException("XUdfEnabledObjectFields not found. Id = " + udfEnabledObjectId);
	}

	private HashMap<String, Object> getBindingVars(List<XField> udfFields) {
		HashMap<String, Object> bindingVars = new HashMap<String, Object>();
		if (udfFields != null) {
			for (XField field : udfFields) {
				bindingVars.put(field.getName(), getFieldValue(field));
			}
		}
		return bindingVars;
	}

	private Boolean isMatch(HashMap<String, Object> bindingVars, XRestrictions restrictionsArray[]) {
		if (restrictionsArray != null && restrictionsArray.length > 0) {
			String expression = getLogicalExpression(bindingVars, restrictionsArray, "", " && ");
			if (!StrUtl.isEmptyTrimmed(expression)) {
				expression = StringUtils.substring(expression, " && ".length());
				if (!StrUtl.isEmptyTrimmed(expression)) {
					Object result = evaluateGroovy(bindingVars, expression);
					// System.out.println("Result : " + result + ",Expr : " + expression);
					if (result != null) {
						return Boolean.valueOf(result.toString());
					}
				}
			}
			return false;
		}
		return true;
	}

	private List<String> getDependentFields(XRestrictions restrictionsArray[], List<String> dependentFields) {
		if (restrictionsArray != null && restrictionsArray.length > 0) {
			String name = null;
			for (XRestrictions restrictions : restrictionsArray) {
				name = restrictions.getName();
				if (!StrUtl.isEmptyTrimmed(name) && !dependentFields.contains(name)) {
					dependentFields.add(name);
				}
				if (restrictions.getRestrictionsArray() != null && restrictions.sizeOfRestrictionsArray() > 0) {
					return getDependentFields(restrictions.getRestrictionsArray(), dependentFields);
				}
			}
		}
		return dependentFields;
	}

	private String getLogicalExpression(HashMap<String, Object> bindingVars, XRestrictions restrictionsArray[], String expression, String junction) {
		// (EQ|NE|GT|LT|GE|LE|OR)
		if (restrictionsArray != null) {
			String name = null;
			String opt = null;
			String value = null;
			for (XRestrictions restrictions : restrictionsArray) {

				name = restrictions.getName();
				opt = restrictions.getComparator();
				value = restrictions.getValue();

				if ("OR".equalsIgnoreCase(opt)) {
					if (restrictions.getRestrictionsArray() != null && restrictions.sizeOfRestrictionsArray() > 0) {
						String nestedExpression = getLogicalExpression(bindingVars, restrictions.getRestrictionsArray(), "", " || ");
						if (!StrUtl.isEmptyTrimmed(nestedExpression)) {
							nestedExpression = StringUtils.substring(nestedExpression, " || ".length());
							if (!StrUtl.isEmptyTrimmed(nestedExpression)) {
								expression += junction + " ( " + nestedExpression + " )";
							}
						}
					}
				}
				else if (!StrUtl.isEmptyTrimmed(name) && bindingVars.containsKey(name)) {
					expression += junction + getCondition(name, bindingVars.get(name), opt, value);
				}
			}
		}
		return expression;
	}

	private String getCondition(String name, Object value1, String opt, String value2) {
		// (EQ|NE|GT|LT|GE|LE|OR)

		if ("EQ".equalsIgnoreCase(opt)) {
			if (value1 instanceof String) {
				return name + ".equals(\"" + value2 + "\")";
			}
			if (value1 instanceof Boolean || value1 instanceof Integer || value1 instanceof Double) {
				return name + " == " + value2;
			}
			else if (value1 instanceof Date) {
				// TODO
			}
		}
		else if ("NE".equalsIgnoreCase(opt)) {
			if (value1 instanceof String) {
				return "!" + name + ".equals(\"" + value2 + "\")";
			}
			if (value1 instanceof Boolean || value1 instanceof Integer || value1 instanceof Double) {
				return name + " != " + value2;
			}
			else if (value1 instanceof Date) {
				// TODO
			}
		}
		else if ("GT".equalsIgnoreCase(opt)) {
			if (value1 instanceof String) {
				return name + " > \"" + value2 + "\"";
			}
			if (value1 instanceof Boolean || value1 instanceof Integer || value1 instanceof Double) {
				return name + " > " + value2;
			}
			else if (value1 instanceof Date) {
				// TODO
			}
		}
		else if ("LT".equalsIgnoreCase(opt)) {
			if (value1 instanceof String) {
				return name + " < \"" + value2 + "\"";
			}
			if (value1 instanceof Boolean || value1 instanceof Integer || value1 instanceof Double) {
				return name + " < " + value2;
			}
			else if (value1 instanceof Date) {
				// TODO
			}
		}
		else if ("GE".equalsIgnoreCase(opt)) {
			if (value1 instanceof String) {
				return name + " >= \"" + value2 + "\"";
			}
			if (value1 instanceof Boolean || value1 instanceof Integer || value1 instanceof Double) {
				return name + " >= " + value2;
			}
			else if (value1 instanceof Date) {
				// TODO
			}
		}
		else if ("LE".equalsIgnoreCase(opt)) {
			if (value1 instanceof String) {
				return name + " <= \"" + value2 + "\"";
			}
			if (value1 instanceof Boolean || value1 instanceof Integer || value1 instanceof Double) {
				return name + " <= " + value2;
			}
			else if (value1 instanceof Date) {
				// TODO
			}
		}
		return "";
	}

	private Object getFieldValue(XField field) {
		// (STRING|BOOLEAN|INTEGER|DOUBLE|DATE)
		try {
			if (field.isSetString()) {
				return field.getString();
			}
			else if (field.isSetInteger()) {
				return field.getInteger();
			}
			else if (field.isSetDouble()) {
				return field.getDouble();
			}
			else if (field.isSetBoolean()) {
				return field.getBoolean();
			}
			else if (field.isSetDate()) {
				return dateXBeanConverter.toDate(field.getDate());
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			// logger.error(ex);
		}
		return null;
	}

	private Object evaluateGroovy(HashMap<String, Object> bindingVars, String expression) {
		try {
			GroovyShell shell = new GroovyShell(UdfDomainUtils.class.getClassLoader());
			Script script = shell.parse(expression);
			if (bindingVars != null) {
				Binding binding = new Binding();
				for (String bindingVar : bindingVars.keySet()) {
					binding.setVariable(bindingVar, bindingVars.get(bindingVar));
				}
				script.setBinding(binding);
			}
			return script.run();
		}
		catch (Exception ex) {
			// logger.error(ex);
		}
		return null;
	}

	private boolean hasMissingValueRequest(List<XField> udfFields) {
		if (udfFields != null) {
			for (XField field : udfFields) {
				if (field.getMissingValueRequest() != null) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDateInFormat(String datePattern, String value) {
		if (StrUtl.isEmptyTrimmed(datePattern)) {
			return true;
		}
		String dateRegExp = null;
		if (datePattern.equalsIgnoreCase("HH:MM")) {
			dateRegExp = "^([0-9]|[0-1][0-9]|[2][0-3]):([0-5][0-9])$";
		}
		else if (datePattern.equalsIgnoreCase("MM/DD/YYYY")) {
			dateRegExp = "^((([0]\\d)|([1][012])|[1-9])|(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))(\\-|\\/|\\s)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\6(\\d{2}|([1][9]\\d{2})|([2]\\d{3})))$";
		}
		else if (datePattern.equalsIgnoreCase("DD/MM/YY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\/)((([0]\\d)|([1][0-2])|[1-9])|(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))(\\/)((\\d{2})|(\\d{2}|([1][9]\\d{2})|([2]\\d{3})))$";
		}
		else if (datePattern.equalsIgnoreCase("MM/DD/YY")) {
			dateRegExp = "^(([0]\\d)|([1][0-2])|([1-9]))(\\-|\\/|\\s)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-|\\/|\\s)(\\d{2})$";
		}
		else if (datePattern.equalsIgnoreCase("DDMMMYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\d{2})?$";
		}
		else if (datePattern.equalsIgnoreCase("DDMMYYYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01]))(([0]\\d)|([1][012]))(\\d{4})$";
		}
		else if (datePattern.equalsIgnoreCase("MMDDYYYY")) {
			dateRegExp = "^(([0]\\d)|([1][012]))(([0]\\d)|([1]\\d)|([2]\\d)|([3][01]))(\\d{4})$";
		}
		else if (datePattern.equalsIgnoreCase("DD-MMM-YY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\-)((\\d{2}|([1][9]\\d{2})|([2]\\d{3})))?$";
		}
		else if (datePattern.equalsIgnoreCase("YYYY/MM/DD")) {
			dateRegExp = "^(\\d{4})(\\/)(([0]\\d)|([1][012]))(\\/)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))$";
		}
		else if (datePattern.equalsIgnoreCase("YYYY-MM-DD")) {
			dateRegExp = "^(\\d{4})(-)(([0]\\d)|([1][012]))(-)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))$";
		}
		else if (datePattern.equalsIgnoreCase("DD/MMM/YYYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\/|\\/)?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\/|\\/)?((\\d{2}|([1][9]\\d{2})|([2]\\d{3})))?$";
		}
		else if (datePattern.equalsIgnoreCase("DD/MMM/YYYY HH:MM")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\/|\\/)?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\/|\\/)?((\\d{2}|([1][9]\\d{2})|([2]\\d{3}))( ([0-9]|[0-1][0-9]|[2][0-3]):([0-5][0-9])))?$";
		}
		else {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-|\\/)?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\-|\\/)?((\\d{2}|([1][9]\\d{2})|([2]\\d{3})))?$";
		}
		if (!StrUtl.isEmptyTrimmed(dateRegExp)) {
			RE matcher = new RE(dateRegExp, RE.MATCH_CASEINDEPENDENT);
			if (!matcher.match(value)) {
				return false;
			}
		}
		return true;
	}

	private boolean isInteger(String value) {
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

	private boolean isDouble(String value) {
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

	private String convertToString(List<String> values) {
		StringBuffer buffer = new StringBuffer();
		for (String value : values) {
			if (buffer.length() > 0) {
				buffer.append(",");
			}
			buffer.append(value);
		}
		return buffer.toString();
	}

}
