package com.addval.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ValidatorSourceParser {

	public static List<ValidatorSpecification> parse(String specification) {
		/*
		 * required
		 * required,regexp=^([\w\s\.\&\-\_]{0,30})?$
		 * required,regexp=([\w\s\.\&\-\_]{0,30})?
		 * required,min=4,max=30
		 */

		List<ValidatorSpecification> result = new ArrayList<ValidatorSpecification>();
		String pair[] = null;
		if (!StrUtl.isEmptyTrimmed(specification)) {
			int startPos = StringUtils.indexOf(specification, "regexp");
			int endPos = -1;

			if (startPos != -1) {
				endPos = StringUtils.indexOf(specification, "$", startPos);
			}

			if (startPos != -1 && endPos != -1) {
				String validator = StringUtils.substring(specification, startPos, endPos + 1);
				pair = StringUtils.split(validator, "regexp=");
				if (pair.length == 1) {
					result.add(new ValidatorSpecification("regexp", pair[0]));
				}
				specification = StringUtils.replace(specification, validator, "");
			}
			else if (startPos != -1) {
				String validator = StringUtils.substring(specification, startPos);
				pair = StringUtils.split(validator, "regexp=");
				if (pair.length == 1) {
					result.add(new ValidatorSpecification("regexp", pair[0]));
				}
				specification = StringUtils.replace(specification, validator, "");
			}
			String pairs[] = StringUtils.split(specification, ",");
			for (int i = 0; i < pairs.length; i++) {
				pair = StringUtils.split(pairs[i], "=");
				if (pair.length == 2) {
					result.add(new ValidatorSpecification(pair[0], pair[1]));
				}
				if (pair.length == 1) {
					result.add(new ValidatorSpecification(pair[0], null));
				}
			}
		}
		return result;
	}

}
