package com.addval.validation;

import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;

import org.springframework.context.MessageSource;

public class CustomMessageInterpolator implements MessageInterpolator {
	private static CustomMessageInterpolator instance;

	private MessageSource messageSource;

	private MessageInterpolator defaultMessageInterpolator;

	private CustomMessageInterpolator() {

	}

	public static CustomMessageInterpolator getInstance() {
		if (instance == null) {
			synchronized (CustomMessageInterpolator.class) {
				if (instance == null) {
					instance = new CustomMessageInterpolator();
				}
			}
		}
		return instance;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public MessageInterpolator getDefaultMessageInterpolator() {
		return defaultMessageInterpolator;
	}

	public void setDefaultMessageInterpolator(MessageInterpolator messageInterpolator) {
		this.defaultMessageInterpolator = messageInterpolator;
	}

	@Override
	public String interpolate(String messageTemplate, Context context) {
		// return interpolate(messageTemplate, context, Locale.getDefault());
		return interpolate(messageTemplate, context, RequestLocaleThreadLocal.getLocale());

	}

	@Override
	public String interpolate(String messageTemplate, Context context, Locale locale) {
		if (getDefaultMessageInterpolator() == null) {
			setDefaultMessageInterpolator(Validation.byDefaultProvider().configure().getDefaultMessageInterpolator());
		}
		String message = getDefaultMessageInterpolator().interpolate(messageTemplate, context, locale);

		/*
		 * Usage :Bean Validation's Parameterization 
		 * 
		 * @NotNull(message = "{required-error}[UserCriteria.firstName-label,value1,value2]")
		 * public String getFirstName() {..}
		 * 
		 * ValidationMessages.properties
		 * required-error={0} is required {1} and {2}.
		 * 
		 */
		int paramIndex = message.lastIndexOf("[");
		if (paramIndex != -1) {
			String paramStr = message.substring(paramIndex + 1, message.lastIndexOf("]"));
			String[] params = paramStr.split(",");
			int i = 0;
			for (String param : params) {
				if (param.indexOf("-label") != -1) {
					String label = messageSource.getMessage(param, null, locale);
					message = message.replace("{" + i + "}", label);
				}
				else {
					message = message.replace("{" + i + "}", param);
				}
				i++;
			}
			message = message.replace("[" + paramStr + "]", "");
		}
		return message;
	}
}
