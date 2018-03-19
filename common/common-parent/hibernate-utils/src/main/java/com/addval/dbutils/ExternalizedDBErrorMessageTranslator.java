package com.addval.dbutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.addval.utils.MultiHashKey;

/**
 * Class is responsible for Loading all the Externalized DB ErrorMessages from Error Master table and returns the Error Description using translate method based on the given errorCode, errorType, errorDomain values.
 * 
 * Currently this class support always EN and US in error messages translator end now. In future planned to enhance full internalization support ( TO-DO) in common.
 * 
 * @author Ravinder Padigela
 * @author Accenture Technology Inc.
 * @version 1.0
 */
public class ExternalizedDBErrorMessageTranslator {

	protected Map<MultiHashKey, String> externalizedDBErrorMessages = null;

	public void setExternalizedDBErrorMessages(Map<MultiHashKey, String> externalizedDBErrorMessages) {
		this.externalizedDBErrorMessages = externalizedDBErrorMessages;
	}

	/**
	 * 
	 * Creates new static instance to load all Externalized DB ErrorMessages and set it into the errorMasterMessages property.
	 * 
	 * @param externalizedDBErrorMessageTranslatorClass
	 * @param serverType
	 * @param externalizedDBErrorMessages
	 */

	@SuppressWarnings("rawtypes")
	public static ExternalizedDBErrorMessageTranslator getInstance(String externalizedDBErrorMessageTranslatorClass, String serverType, Map<MultiHashKey, String> externalizedDBErrorMessages) {
		try {
			Class errorMasterTranslatorClass = Class.forName(externalizedDBErrorMessageTranslatorClass);
			ExternalizedDBErrorMessageTranslator externalizedDBErrorMessageTranslator = (ExternalizedDBErrorMessageTranslator) errorMasterTranslatorClass.newInstance();
			externalizedDBErrorMessageTranslator.setExternalizedDBErrorMessages(externalizedDBErrorMessages);
			return externalizedDBErrorMessageTranslator;
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Method returns Error Description based on the given errorCode, errorType, errorDomain values.
	 * 
	 * Currently supporting only EN and US for error messages. In future planned to enhance full internalization support.
	 * 
	 * @param errorMessages
	 * @return message
	 */
	public String translate(String... errorMessages) {
		List<String> keysList = new ArrayList<String>(5);
		for (String errorMessage : errorMessages) {
			keysList.add(errorMessage);
		}
		MultiHashKey multiHashKey = new MultiHashKey(keysList);
		if (externalizedDBErrorMessages != null) {
			return externalizedDBErrorMessages.get(multiHashKey);
		}
		return null;
	}
}
